package top.kangert.kspider.executor.node;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.driver.DriverProvider;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.io.SeleniumResponse;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.model.ConfigItem.ComponentType;
import top.kangert.kspider.model.ConfigItem.DataType;
import top.kangert.kspider.model.ConfigItem.SelectItem;
import top.kangert.kspider.support.ExpressionEngine;
import top.kangert.kspider.util.SeleniumResponseHolder;
import top.kangert.kspider.websocket.WebSocketEvent;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Selenium节点执行器
 */
@Slf4j
@Component
public class SeleniumExecutor implements NodeExecutor {

    public static final String NODE_VARIABLE_NAME = "nodeVariableName";

    public static final String DRIVER_TYPE = "driverType";

    /**
     * Session名称
     */
    private static final String REQUEST_SESSION = "header-session";

    public static final String URL = "url";

    public static final String PAGE_LOAD_TIMEOUT = "pageLoadTimeout";

    public static final String IMPLICITLY_WAIT_TIMEOUT = "implicitlyWaitTimeout";

    public static final String PROXY = "proxy";

    public static final String COOKIE_AUTO_SET = "cookie-auto-set";

    private final List<DriverProvider> driverProviders;

    private Map<String, DriverProvider> providerMap;

    private ExpressionEngine expressionEngine;

    public SeleniumExecutor(List<DriverProvider> driverProviders, ExpressionEngine expressionEngine) {
        this.driverProviders = driverProviders;
        this.expressionEngine = expressionEngine;
    }

    @PostConstruct
    private void init() {
        providerMap = driverProviders.stream().filter(provider -> provider.support() != null)
                .collect(Collectors.toMap(DriverProvider::support, value -> value));
    }

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String proxy = node.getJsonProperty(PROXY);
        String driverType = node.getJsonProperty(DRIVER_TYPE);
        String nodeVariableName = node.getJsonProperty(NODE_VARIABLE_NAME, Constants.RESPONSE_VARIABLE);
        boolean cookieAutoSet = Constants.YES.equals(node.getJsonProperty(COOKIE_AUTO_SET));
        List<Map<String, String>> headers = node.getJsonArrayProperty(REQUEST_SESSION);

        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, DRIVER_TYPE, driverType);
        if (StrUtil.isBlank(driverType) || !providerMap.containsKey(driverType)) {
            log.error("找不到驱动：{}", driverType);
            return;
        }

        if (StrUtil.isNotBlank(proxy)) {
            try {
                proxy = expressionEngine.execute(proxy, variables).toString();
                log.info("设置代理：{}", proxy);
            } catch (Exception e) {
                log.error("设置代理出错", e);
            }
        }

        Object oldResp = variables.get(nodeVariableName);
        // 一个任务流中只能有一个 Driver，在页面跳转操作可以使用 resp.toUrl。打开其他 Driver 时，原页面会关闭（同一个变量名）
        if (oldResp instanceof SeleniumResponse) {
            SeleniumResponse oldResponse = (SeleniumResponse) oldResp;
            oldResponse.quit();
        }

        WebDriver driver = null;


        try {
            String url = expressionEngine.execute(node.getJsonProperty(URL), variables).toString();
            log.info("设置请求 url：{}", url);
            driver = providerMap.get(driverType).getWebDriver(node, proxy);
            driver.manage().timeouts().pageLoadTimeout(
                    Duration.ofMillis(Convert.toInt(node.getJsonProperty(PAGE_LOAD_TIMEOUT), 60 * 1000)));
            driver.manage().timeouts().implicitlyWait(
                    Duration.ofMillis(Convert.toInt(node.getJsonProperty(IMPLICITLY_WAIT_TIMEOUT), 3 * 1000)));

            // 初始化打开浏览器
            driver.get(url);

            Map<String, String> cookieContext = context.getCookieContext();

            // 如果开启了自动管理 cookies，则将之前的 cookies 添加到浏览器中
            if (cookieAutoSet) {
                driver.manage().deleteAllCookies();
                java.net.URL tempUrl = new URL(url);
                // 设置 cookies 有效期 1 个月
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 1);
                for (Map.Entry<String, String> item : cookieContext.entrySet()) {
                    Cookie cookie = new Cookie(item.getKey(), item.getValue(), tempUrl.getHost(), "/",
                            calendar.getTime(), false, false);
                    driver.manage().addCookie(cookie);
                }
                log.debug("自动设置Cookie：{}", cookieContext);
            }

            // TODO: 设置session
            String headerKey = null;
            String headerValue = null;
            Object value = null;
            for (Map<String, String> header : headers) {
                String headerName = header.get(REQUEST_SESSION);
                if (StrUtil.isNotBlank(headerName)) {
                    Map<String, String> headerMap = JSONUtil.toBean(headerName, new TypeReference<Map<String, String>>() {
                    }, false);
                    headerKey = headerMap.get("key");
                    int i = header.size();
                    headerValue = headerMap.get("value");
                    value = expressionEngine.execute(headerValue, variables);
                    ((RemoteWebDriver) driver).executeScript(String.format("sessionStorage.setItem('%s', '%s')", headerKey, value));
                }
            }


            // 访问 url
            driver.get(url);
            SeleniumResponse response = new SeleniumResponse(driver);
            SeleniumResponseHolder.add(context, response);


            // 如果开启了自动管理 cookies，在页面响应后再把 cookies 放到上下文中
            if (cookieAutoSet) {
                Map<String, String> cookies = response.getCookies();
                cookieContext.putAll(cookies);
            }
            variables.put(nodeVariableName, response);

        } catch (Exception e) {
            log.error("请求出错", e);
            if (driver != null) {
                try {
                    driver.quit();
                } catch (Exception ignored) {
                }
            }
            ExceptionUtil.wrapAndThrow(e);
        }
    }

    @Override
    public String supportType() {
        return "selenium";
    }

    @Override
    public Shape shape() {
        Shape shape = new Shape();
        shape.setIcon("ele-ChromeFilled");
        shape.setName(supportType());
        shape.setTitle("Selenium");
        shape.setLabel("Selenium");
        return shape;
    }

    @Override
    public List<ConfigItem> configItems() {

        List<ConfigItem> configItemList = new ArrayList<>();

        // 节点变量
        ConfigItem nodeVariableName = new ConfigItem("节点变量", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, "nodeVariableName", "请输入节点变量", "resp", null, null);
        configItemList.add(nodeVariableName);

        // Cookie自动管理
        ConfigItem cookieAutoSet = new ConfigItem("Cookie自动管理", ConfigItem.ComponentType.EL_SWITCH,
                ConfigItem.DataType.BOOLEAN, COOKIE_AUTO_SET, null, true, null, null);
        configItemList.add(cookieAutoSet);

        // 启动参数
        List<SelectItem> selectItemList = new ArrayList<SelectItem>();
        SelectItem startMaximized = new SelectItem("最大窗口", "--start-maximized", DataType.STRING);
        selectItemList.add(startMaximized);

        // 无头模式
        SelectItem headless = new SelectItem("无头模式", "headless", DataType.STRING);
        selectItemList.add(headless);

        // 启动参数
        ConfigItem extConfig = new ConfigItem("启动参数", ComponentType.CUSTOM_MULT_KEY_VALUE, DataType.LIST_MAP,
                "extConfig",
                "请选择启动参数", new ArrayList<>(), null, selectItemList);
        configItemList.add(extConfig);

        // 循环变量
        ConfigItem loopVariableName = new ConfigItem("循环变量", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, "loopVariableName", "请输入循环变量", "", null, null);
        configItemList.add(loopVariableName);

        // 循环次数
        Map<String, Object> loopCountAttrs = new HashMap<>();
        loopCountAttrs.put("min", 1);
        ConfigItem loopCount = new ConfigItem("循环次数", ConfigItem.ComponentType.EL_NUMBER_INPUT, ConfigItem.DataType.INT,
                "loopCount", "请输入循环次数", 1, loopCountAttrs, null);
        configItemList.add(loopCount);

        // 页面加载超时时间
        ConfigItem pageLoadTimeout = new ConfigItem("页面加载超时时间(ms)", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.INT, "pageLoadTimeout", "请输入页面加载超时时间", 5000, null, null);
        configItemList.add(pageLoadTimeout);

        // 元素获取超时时间
        ConfigItem implicitlyWaitTimeout = new ConfigItem("元素获取超时时间", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.INT, "implicitlyWaitTimeout", "请输入元素获取超时时间", 5000, null, null);
        configItemList.add(implicitlyWaitTimeout);

        // 驱动类型
        List<ConfigItem.SelectItem> driverTypeSelectItem = new ArrayList<>();
        ConfigItem.SelectItem chromeItem = new ConfigItem.SelectItem("chrome", "chrome", ConfigItem.DataType.STRING);
        ConfigItem.SelectItem firefoxItem = new ConfigItem.SelectItem("firefox", "firefox", ConfigItem.DataType.STRING);
        driverTypeSelectItem.add(chromeItem);
        driverTypeSelectItem.add(firefoxItem);
        ConfigItem driverType = new ConfigItem("驱动类型", ConfigItem.ComponentType.EL_SELECT, ConfigItem.DataType.STRING,
                "driverType", "请选择驱动类型", "chrome", null, driverTypeSelectItem);
        configItemList.add(driverType);

        // 请求地址
        ConfigItem requestUrl = new ConfigItem("请求地址", ConfigItem.ComponentType.EL_INPUT, ConfigItem.DataType.STRING,
                "url", "请输入请求地址(url)", "", null, null);
        configItemList.add(requestUrl);

        //请求携带session REQUEST_HEADER_NAME
        ConfigItem reheadernameitem = new ConfigItem("携带session请求网址", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
                ConfigItem.DataType.LIST_MAP, SeleniumExecutor.REQUEST_SESSION, "请输入携带的session", new ArrayList<>(),
                null, null);
        configItemList.add(reheadernameitem);

        // 远程驱动地址
        ConfigItem remoteWebdriverUrl = new ConfigItem("远程驱动地址", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, "remote-webdriver-url", "请输入远程驱动地址", "", null, null);
        configItemList.add(remoteWebdriverUrl);

        return configItemList;
    }

}

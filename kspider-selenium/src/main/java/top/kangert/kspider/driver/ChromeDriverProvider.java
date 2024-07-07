package top.kangert.kspider.driver;

import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.util.IoUtils;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.*;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Chrome浏览器驱动提供者
 */
@Component
public class ChromeDriverProvider implements DriverProvider {

    private static final String USER_AGENT_OPTION = "user-agent=\"%s\"";
    private static final String DISABLED_JAVASCRIPT_OPTION = "–-disable-javascript";
    private static final String IMAGES_DISABLED_OPTION = "blink-settings=imagesEnabled=false";
    private static final String HIDE_SCROLLBARS_OPTION = "--hide-scrollbars";
    private static final String NO_SANDBOX_OPTION = "--no-sandbox";
    private static final String INCOGNITO_OPTION = "--incognito";
    private static final String DISABLE_PLUGINS_OPTION = "--disable-plugins";
    private static final String DISABLE_JAVA_OPTION = "--disable-java";
    private static final String WINDOW_SIZE_OPTION = "--window-size=%s";
    private static final String MAXIMIZED_OPTION = "--start-maximized";
    private static final String DISABLE_GPU_OPTION = "--disable-gpu";

    @Override
    public String support() {
        return Browser.CHROME.browserName();
    }

    @Override
    public WebDriver getWebDriver(SpiderNode node, String proxyStr) throws MalformedURLException {

        ChromeOptions options = new ChromeOptions();

        String userAgent = node.getJsonProperty(USER_AGENT);
        // 设置 User-Agent
        if (StrUtil.isNotBlank(userAgent)) {
            options.addArguments(String.format(USER_AGENT_OPTION, userAgent));
        }

        // 禁用 JS
        if (Constants.YES.equals(node.getJsonProperty(JAVASCRIPT_DISABLED))) {
            options.addArguments(DISABLED_JAVASCRIPT_OPTION);
        }

        // 不加载图片
        if (Constants.YES.equals(node.getJsonProperty(IMAGE_DISABLED))) {
            options.addArguments(IMAGES_DISABLED_OPTION);
        }

        // 隐藏滚动条
        if (Constants.YES.equals(node.getJsonProperty(HIDE_SCROLLBAR))) {
            options.addArguments(HIDE_SCROLLBARS_OPTION);
        }

        // 无头模式
        options.setHeadless(true);
        if (!Constants.YES.equals(node.getJsonProperty(HEADLESS))) {
            options.setHeadless(false);
        }

        // 禁用沙盒模式
        if (Constants.YES.equals(node.getJsonProperty(NO_SANDBOX))) {
            options.addArguments(NO_SANDBOX_OPTION);
        }

        // 隐身模式
        options.addArguments(INCOGNITO_OPTION);
        if (Constants.YES.equals(node.getJsonProperty(INCOGNITO))) {
            options.addArguments(INCOGNITO_OPTION);
        }

        // 禁用插件
        if (Constants.YES.equals(node.getJsonProperty(PLUGIN_DISABLE))) {
            options.addArguments(DISABLE_PLUGINS_OPTION);
        }

        // 禁用 Java
        if (Constants.YES.equals(node.getJsonProperty(JAVA_DISABLE))) {
            options.addArguments(DISABLE_JAVA_OPTION);
        }

        // 设置窗口大小
        String windowSize = node.getJsonProperty(WINDOW_SIZE, "1920x3000");
        if (StrUtil.isNotBlank(windowSize)) {
            options.addArguments(String.format(WINDOW_SIZE_OPTION, windowSize));
        }

        // 最大化 
        options.addArguments(MAXIMIZED_OPTION);
        if (Constants.YES.equals(node.getJsonProperty(MAXIMIZED))) {
            options.addArguments(MAXIMIZED_OPTION);
        }

        // 禁用 gpu 加速
        if (Constants.YES.equals(node.getJsonProperty(GPU_DISABLE))) {
            options.addArguments(DISABLE_GPU_OPTION);
        }

        // 设置其他参数
        String arguments = node.getJsonProperty(ARGUMENTS);
        if (StrUtil.isNotBlank(arguments)) {
            options.addArguments(Arrays.asList(arguments.split("\r\n")));
        }

        // 设置代理
        if (StrUtil.isNotBlank(proxyStr)) {
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(proxyStr);
            options.setProxy(proxy);
        }

        // 默认去掉 “chrome 正受到自动测试软件的控制” 的通知
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singleton("enable-automation"));

        String remoteWebdriverUrl = node.getJsonProperty(REMOTE_WEBDRIVER_URL, DEFAULT_REMOTE_WEBDRIVER_URL);
        WebDriver driver = new CdpRemoteWebDriver(new URL(remoteWebdriverUrl), options);

        // 设置selenium特征隐藏
        InputStream input = ChromeDriverProvider.class.getClassLoader().getResourceAsStream("stealth.min.js");
        String source = IoUtils.readStreamToString(input);

        Map<String, Object> params = new HashMap<>();
        params.put("source", source);

        ((CdpRemoteWebDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);

        return driver;
    }
}

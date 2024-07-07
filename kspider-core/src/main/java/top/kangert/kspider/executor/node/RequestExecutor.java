package top.kangert.kspider.executor.node;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.constant.RequestBodyType;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.io.HttpRequest;
import top.kangert.kspider.io.HttpResponse;
import top.kangert.kspider.io.SpiderResponse;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Grammar;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.support.ExpressionEngine;
import top.kangert.kspider.support.Grammarly;
import top.kangert.kspider.support.UserAgentManager;
import top.kangert.kspider.websocket.WebSocketEvent;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.Cookie;
import org.springframework.stereotype.Component;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 请求执行器
 */
@Component
@Slf4j
public class RequestExecutor implements NodeExecutor, Grammarly {

    /**
     * 请求的延迟时间
     */
    private static final String REQUEST_SLEEP = "sleep";

    /**
     * 请求的 URL
     */
    private static final String REQUEST_URL = "url";

    /**
     * 请求的代理
     */
    private static final String REQUEST_PROXY = "proxy";

    /**
     * 请求的方法
     */
    private static final String REQUEST_METHOD = "method";

    /**
     * 请求的查询参数名称
     */
    private static final String REQUEST_QUERY_PARAM_NAME = "query-param-name";

    /**
     * 请求的查询参数值
     */
    private static final String REQUEST_QUERY_PARAM_VALUE = "query-param-value";

    /**
     * 请求的表单参数名称
     */
    private static final String FORM_PARAM_NAME = "form-param-name";

    /**
     * 请求的表单参数值
     */
    private static final String FORM_PARAM_VALUE = "form-param-value";

    /**
     * 请求的表单参数类型
     */
    private static final String FORM_PARAM_TYPE = "form-param-type";

    /**
     * 请求的表单中文件的名称
     */
    private static final String FORM_PARAM_FILENAME = "form-param-filename";

    /**
     * 请求体的类型 BODY_TYPE
     */
    private static final String BODY_TYPE = "body-type";

    /**
     * 请求体的正文类型（MIME Type）
     */
    private static final String BODY_CONTENT_TYPE = "body-content-type";

    /**
     * 请求体
     */
    private static final String REQUEST_BODY = "request-body";

    /**
     * 请求的 Cookie 名称
     */
    private static final String REQUEST_COOKIE_NAME = "cookie-name";

    /**
     * 请求的 Cookie 值
     */
    private static final String REQUEST_COOKIE_VALUE = "cookie-value";

    /**
     * 请求头名称
     */
    private static final String REQUEST_HEADER_NAME = "header-name";

    /**
     * 请求头的值
     */
    private static final String REQUEST_HEADER_VALUE = "header-value";

    /**
     * 请求超时时间
     */
    private static final String REQUEST_TIMEOUT = "request-timeout";

    /**
     * 请求失败后的重试次数
     */
    private static final String REQUEST_RETRY_COUNT = "request-retry-count";

    /**
     * 重试间隔
     */
    private static final String REQUEST_RETRY_INTERVAL = "request-retry-interval";

    /**
     * 跟随重定向
     */
    private static final String REQUEST_FOLLOW_REDIRECT = "request-follow-redirect";

    /**
     * 自动管理 Cookie
     */
    private static final String REQUEST_AUTO_COOKIE = "request-cookie-auto";

    /**
     * 随机 User-Agent
     */
    private static final String RANDOM_USERAGENT = "request-random-useragent";

    /**
     * 响应内容编码
     */
    private static final String RESPONSE_CHARSET = "response-charset";

    @Resource
    private ExpressionEngine expressionEngine;

    @Resource
    private UserAgentManager userAgentManager;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 设置延迟时间
        this.setupSleepTime(node, context);
        // 执行
        this.doExecute(node, context, variables);
    }

    private void doExecute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 请求体类型
        //String s = node.getJsonProperty(BODY_TYPE, "none");
        RequestBodyType bodyType = RequestBodyType.geRequestBodyType(node.getJsonProperty(BODY_TYPE, "none"));
        // 重试次数
        int retryCount = Convert.toInt(node.getJsonProperty(REQUEST_RETRY_COUNT), 0);

        // 重试间隔，单位毫秒
        long retryInterval = Convert.toLong(node.getJsonProperty(REQUEST_RETRY_INTERVAL), 0L);

        boolean success = false;
        for (int i = 0; i < retryCount + 1 && !success; i++) {
            HttpRequest request = HttpRequest.create();
            
            // 设置 URL
            String url = this.setupUrl(request, node, context, variables);
            // 设置请求超时时间
            this.setupTimeout(request, node);
            // 设置请求方法
            this.setupMethod(request, node);
            // 设置是否跟随重定向
            this.setupFollowRedirects(request, node);
            // 设置随机 User-Agent
            this.setupRandomUserAgent(request, node);
            // 设置头部信息
            this.setupHeaders(request, node, context, variables);

            // 设置 Cookies
            this.setupCookies(request, node, context, variables);

            List<InputStream> streams = null;
            switch (bodyType) {
                case RAW_BODY_TYPE:
                    // 设置请求体
                    this.setupRequestBody(request, node, context, variables);
                    break;
                case FORM_DATA_BODY_TYPE:
                    // 设置请求表单
                    streams = this.setupRequestFormParam(request, node, context, variables);
                    break;
                default:
                    // 设置请求参数
                    this.setupQueryParams(request, node, context, variables);
            }

            // 设置代理
            this.setupProxy(request, node, context, variables);

            Throwable throwable = null;
            try {
                HttpResponse response = request.execute();
                if (success = response.getStatusCode() == 200) {
                    //查看url
                    log.info("查看发送请求 URL：{}", response.getUrl());
                    //System.out.println(response.getHtml());       //此处查看是否可以拿到我请求结果的html
                    // 设置响应编码
                    String charset = node.getJsonProperty(RESPONSE_CHARSET);
                    if (StrUtil.isNotBlank(charset)) {
                        response.setCharset(charset);
                        log.debug("设置响应的编码：{}", charset);
                    }
                    // 是否自动管理 Cookie
                    String cookeAuto = node.getJsonProperty(REQUEST_AUTO_COOKIE);
                    if (Constants.YES.equals(cookeAuto)) {
                        // 将响应的 Cookie 放入 Cookie 上下文中
                        context.getCookieContext().putAll(response.getCookies());
                    }

                    // 获取节点名
                    String nodeName = node.getJsonProperty("nodeVariableName");

                    // 将结果放入要传递的变量集合中
                    variables.put(nodeName, response);
                }
            } catch (IOException e) {
                success = false;
                throwable = e;
            } finally {
                // 关闭流
                if (streams != null) {
                    for (InputStream is : streams) {
                        IoUtil.close(is);
                    }
                }
                if (!success) {
                    if (i < retryCount) {
                        // 睡眠一段时间后重试
                        if (retryInterval > 0) {
                            try {
                                TimeUnit.MILLISECONDS.sleep(retryInterval);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        log.info("第 {} 次重试 URL：{}", i + 1, url);
                    } else {
                        log.error("请求 URL：{} 出错", url, throwable);
                    }
                }
            }
        }
    }

    /**
     * 设置代理
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param variables 传递的变量与值
     */
    private void setupProxy(HttpRequest request, SpiderNode node, SpiderContext context,
                            Map<String, Object> variables) {
        String proxy = node.getJsonProperty(REQUEST_PROXY);
        if (StrUtil.isNotBlank(proxy)) {
            try {
                Object value = expressionEngine.execute(proxy, variables);
                if (value != null) {
                    List<String> proxyArr = StrUtil.split((String) value, Constants.PROXY_HOST_PORT_SEPARATOR);
                    if (proxyArr.size() == 2) {
                        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_PROXY, value);
                        log.info("设置代理地址：{}", value);
                        request.proxy(proxyArr.get(0), Integer.parseInt(proxyArr.get(1)));
                    }
                }
            } catch (Exception e) {
                log.error("设置代理出错", e);
            }
        }
    }

    /**
     * 设置 Cookies
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void setupCookies(HttpRequest request, SpiderNode node, SpiderContext context,
                              Map<String, Object> variables) {
        // 获取根节点
        SpiderNode root = context.getRoot();

        // 根节点（全局）的 Cookie
        Map<String, String> cookies = this.getCookies(
                root.getJsonArrayProperty(REQUEST_COOKIE_NAME), context, root, variables);
        request.cookies(cookies);

        // Cookie 上下文，包含之前设置的 Cookie
        Map<String, String> cookieContext = context.getCookieContext();
        String cookeAuto = node.getJsonProperty(REQUEST_AUTO_COOKIE);
        if (Constants.YES.equals(cookeAuto) && !context.getCookieContext().isEmpty()) {
            context.pause(node.getNodeId(), WebSocketEvent.REQUEST_AUTO_COOKIE_EVENT, REQUEST_AUTO_COOKIE,
                    cookieContext);
            request.cookies(cookieContext);
            log.info("自动设置 Cookies：{}", cookieContext);
        }

        // 当前节点的 Cookie
        cookies = this.getCookies(
                node.getJsonArrayProperty(REQUEST_COOKIE_NAME), context, node, variables);

        request.cookies(cookies);

        // 将当前设置的全局 Cookie 和节点 Cookie 都放入 Cookie 上下文中
        if (Constants.YES.equals(cookeAuto)) {
            cookieContext.putAll(cookies);
        }
    }

    /**
     * 解析 Cookies
     *
     * @param cookies   需要解析的 Cookies
     * @param variables 传递的变量与值
     * @return 解析后的 Cookies
     */
    private Map<String, String> getCookies(List<Map<String, String>> cookies, SpiderContext context, SpiderNode node,
                                           Map<String, Object> variables) {
        Map<String, String> result = new HashMap<>();
        if (cookies != null) {
            for (Map<String, String> cookie : cookies) {
                String cookieName = cookie.get(REQUEST_COOKIE_NAME);
                if (StrUtil.isNotBlank(cookieName)) {
                    Map<String, String> cookieMap = JSONUtil.toBean(cookieName, new TypeReference<Map<String, String>>() {
                    }, false);
                    String cookieKey = cookieMap.get("key");
                    String cookieValue = cookieMap.get("value");
                    try {
                        Object value = expressionEngine.execute(cookieValue, variables);
                        result.put(cookieKey, (String) value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_COOKIE_EVENT, cookieKey, value);
                        log.info("设置 Cookie：{} = {}", cookieName, value);
                    } catch (Exception e) {
                        log.error("解析请求 Cookie：{} 出错", cookieName, e);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 设置请求体
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void setupRequestBody(HttpRequest request, SpiderNode node, SpiderContext context,
                                  Map<String, Object> variables) {
        String contentType = node.getJsonProperty(BODY_CONTENT_TYPE);
        request.contentType(contentType);
        try {
            Object requestBody = expressionEngine.execute(node.getJsonProperty(REQUEST_BODY), variables);
            request.requestBody(requestBody);
            context.pause(node.getNodeId(), WebSocketEvent.REQUEST_BODY_EVENT, REQUEST_BODY, requestBody);
            log.info("设置请求 Body：{}", requestBody);
        } catch (Exception e) {
            log.debug("设置请求 Body 出错", e);
        }
    }

    /**
     * 设置请求表单参数
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     * @return 表单中的二进制数据输入流集合
     */
    private List<InputStream> setupRequestFormParam(HttpRequest request, SpiderNode node, SpiderContext context,
                                                    Map<String, Object> variables) {
        List<Map<String, String>> formParams = node.getJsonArrayProperty(FORM_PARAM_NAME);
        List<InputStream> streams = new ArrayList<>();
        if (formParams != null) {
            for (Map<String, String> nameValue : formParams) {
                Object value;
                String paramName = nameValue.get(FORM_PARAM_NAME);
                if (StrUtil.isNotBlank(paramName)) {
                    Map<String, String> paramMap = JSONUtil.toBean(paramName, new TypeReference<Map<String, String>>() {
                    }, false);
                    String paramKey = paramMap.get("key");
                    String paramValue = paramMap.get("value");
                    boolean hasFile = "file".equals(expressionEngine.execute(node.getJsonProperty(FORM_PARAM_TYPE), variables));
                    try {
                        Object formfilename = expressionEngine.execute(node.getJsonProperty(FORM_PARAM_FILENAME), variables);
                        value = expressionEngine.execute(paramValue, variables);
                        if (hasFile) {
                            InputStream stream = null;
                            if (value instanceof byte[]) {
                                stream = new ByteArrayInputStream((byte[]) value);
                            } else if (value instanceof String) {
                                stream = new ByteArrayInputStream(((String) value).getBytes());
                            } else if (value instanceof InputStream) {
                                stream = (InputStream) value;
                            }
                            if (stream != null) {
                                streams.add(stream);
                                context.pause(node.getNodeId(), WebSocketEvent.REQUEST_BODY_EVENT, paramKey,
                                        formfilename);
                                log.info("设置请求表单参数：{} = {}", paramName, formfilename);
                            } else {
                                log.warn("设置请求表单参数：{} 失败，无二进制内容", paramName);
                            }
                        } else {
                            request.data(paramKey, value);
                            context.pause(node.getNodeId(), WebSocketEvent.REQUEST_BODY_EVENT, paramKey, value);
                            log.info("设置请求表单参数：{} = {}", paramName, value);
                        }
                    } catch (Exception e) {
                        log.error("设置请求表单参数：{} 出错", paramName, e);
                    }
                }
            }
        }
        return streams;
    }

    /**
     * 设置查询参数
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void setupQueryParams(HttpRequest request, SpiderNode node, SpiderContext context,
                                  Map<String, Object> variables) {
        // 获取根节点
        SpiderNode root = context.getRoot();
        // 设置根节点（全局）的查询参数
        List<Map<String, String>> rootParams = root.getJsonArrayProperty(REQUEST_QUERY_PARAM_NAME);
        this.setQueryParams(request, root, rootParams, context, variables);
        // 设置当前节点的查询参数
        List<Map<String, String>> params = node.getJsonArrayProperty(REQUEST_QUERY_PARAM_NAME);
        this.setQueryParams(request, node, params, context, variables);
    }

    /**
     * 设置查询参数
     *
     * @param request   请求包装对象
     * @param params    解析后的查询参数
     * @param variables 传递的变量与值
     */
    private void setQueryParams(HttpRequest request, SpiderNode node, List<Map<String, String>> params,
                                SpiderContext context, Map<String, Object> variables) {
        if (params != null) {
            for (Map<String, String> param : params) {
                String paramName = param.get(REQUEST_QUERY_PARAM_NAME);
                if (StrUtil.isNotBlank(paramName)) {
                    Map<String, String> paramMap = JSONUtil.toBean(paramName, new TypeReference<Map<String, String>>() {
                    }, false);
                    String paramKey = paramMap.get("key");
                    String paramValue = paramMap.get("value");
                    try {
                        Object value = expressionEngine.execute(paramValue, variables);
                        request.data(paramKey, value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_PARAM_EVENT, paramKey, value);
                        log.info("设置请求查询参数：{} = {}", paramName, value);
                    } catch (Exception e) {
                        log.error("设置请求查询参数：{} 出错", paramName, e);
                    }
                }
            }
        }
    }

    /**
     * 设置随机 User-Agent
     *
     * @param request 请求包装对象
     */
    private void setupRandomUserAgent(HttpRequest request, SpiderNode node) {
        // 是否使用随机 User-Agent
        boolean randomUserAgent = Constants.YES.equals(node.getJsonProperty(RANDOM_USERAGENT));
        if (randomUserAgent) {
            String userAgent = userAgentManager.getRandom();
            log.info("设置请求 Header：{} = {}", "User-Agent", userAgent);
            request.header("User-Agent", userAgent);

        }
    }

    /**
     * 设置头部信息
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    private void setupHeaders(HttpRequest request, SpiderNode node, SpiderContext context,
                              Map<String, Object> variables) {
        // 获取根节点
        SpiderNode root = context.getRoot();
        // 设置根节点（全局）的头部信息
        List<Map<String, String>> rootHeaders = root.getJsonArrayProperty(REQUEST_HEADER_NAME);
        this.setHeaders(request, rootHeaders, context, root, variables);
        // 设置当前节点的头部信息
        List<Map<String, String>> headers = node.getJsonArrayProperty(REQUEST_HEADER_NAME);
        this.setHeaders(request, headers, context, node, variables);

    }

    /**
     * 设置头部信息
     *
     * @param request   请求包装对象
     * @param headers   解析后的头部信息
     * @param variables 传递的变量与值
     */
    private void setHeaders(HttpRequest request, List<Map<String, String>> headers, SpiderContext context,
                            SpiderNode node, Map<String, Object> variables) {
        if (headers != null) {
            for (Map<String, String> header : headers) {
                String headerName = header.get(REQUEST_HEADER_NAME);
                if (StrUtil.isNotBlank(headerName)) {
                    Map<String, String> headerMap = JSONUtil.toBean(headerName, new TypeReference<Map<String, String>>() {
                    }, false);
                    String headerKey = headerMap.get("key");
                    String headerValue = headerMap.get("value");
                    try {
                        Object value = expressionEngine.execute(headerValue, variables);
                        request.header(headerKey, value);
                        context.pause(node.getNodeId(), WebSocketEvent.REQUEST_HEADER_EVENT, headerKey, value);
                        log.info("设置请求 Header：{} = {}", headerKey, value);
                    } catch (Exception e) {
                        log.error("设置请求 Header：{} 出错", headerName, e);
                    }
                }
            }
        }
    }

    /**
     * 设置是否跟随重定向
     *
     * @param request 请求包装对象
     * @param node    节点
     */
    private void setupFollowRedirects(HttpRequest request, SpiderNode node) {
        String followRedirect = node.getJsonProperty(REQUEST_FOLLOW_REDIRECT);
        boolean following = Constants.YES.equals(followRedirect);
        log.debug("设置是否跟随重定向：{}", following);
        request.followRedirects(following);
    }

    /**
     * 设置请求方法
     *
     * @param request 请求包装对象
     * @param node    节点
     */
    private void setupMethod(HttpRequest request, SpiderNode node) {
        String method = node.getJsonProperty(REQUEST_METHOD, "GET");
        log.debug("设置请求方法：{}", method);
        request.method(method);
    }

    /**
     * 设置请求超时时间
     *
     * @param request 请求包装对象
     * @param node    节点
     */
    private void setupTimeout(HttpRequest request, SpiderNode node) {
        // 默认 20s
        int timeout = Convert.toInt(node.getJsonProperty(REQUEST_TIMEOUT), 20000);
        log.debug("设置请求超时时间：{} ms", timeout);
        request.timeout(timeout);
    }

    /**
     * 设置 URL
     *
     * @param request   请求包装对象
     * @param node      节点
     * @param variables 传递的变量与值
     */
    private String setupUrl(HttpRequest request, SpiderNode node, SpiderContext context,
                            Map<String, Object> variables) {
        String url = null;
        try {
            url = (String) expressionEngine.execute(node.getJsonProperty(REQUEST_URL), variables);
            context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_URL, url);
            log.info("设置请求 URL：{}", url);
            request.url(url);
        } catch (Exception e) {
            log.error("设置请求 URL 出错", e);
            // 直接抛出异常
            ExceptionUtil.wrapAndThrow(e);
        }
        return url;
    }

    /**
     * 设置延迟时间
     *
     * @param node    节点
     * @param context 执行上下文
     */
    private void setupSleepTime(SpiderNode node, SpiderContext context) {
        // 获取睡眠时间
        String sleep = node.getJsonProperty(REQUEST_SLEEP);
        long sleepTime = Convert.toLong(sleep, 0L);

        try {
            // 实际等待的时间 = 上次执行的时间 + 睡眠的时间 - 当前时间
            Long lastTime = (Long) context.getExtends_map().get(Constants.LAST_REQUEST_EXECUTE_TIME + node.getNodeId());
            if (lastTime != null) {
                sleepTime = lastTime + sleepTime - System.currentTimeMillis();
            }
            if (sleepTime > 0) {
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, REQUEST_SLEEP, sleepTime);
                log.debug("设置延迟时间：{} ms", sleepTime);
                // 睡眠
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            }
            // 更新上次执行的时间
            context.getExtends_map().put(Constants.LAST_REQUEST_EXECUTE_TIME + node.getNodeId(),
                    System.currentTimeMillis());
        } catch (Throwable t) {
            log.error("设置延迟时间失败", t);
        }
    }

    @PostConstruct
    void initialize() {
        // 允许设置被限制的请求头
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    @Override
    public String supportType() {
        return "request";
    }

    @Override
    public List<Grammar> grammars() {
        List<Grammar> grammars = Grammar.findGrammars(SpiderResponse.class, "resp", "SpiderResponse", false);
        Grammar grammar = new Grammar();
        grammar.setFunction(Constants.RESPONSE_VARIABLE);
        grammar.setComment("抓取结果");
        grammar.setOwner("SpiderResponse");
        grammars.add(grammar);
        return grammars;
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "请求", "请求", "ele-Promotion", "标志着发送请求");
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<>();

        //节点变量
        ConfigItem nodeVariableName = new ConfigItem("节点变量", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, "nodeVariableName", "请输入节点变量", "rest", null, null);
        configItemList.add(nodeVariableName);

        //请求的延迟时间  默认0s
        Map<String, Object> maxDateTime = new HashMap<>();
        maxDateTime.put("min", 0);
        ConfigItem delaytimeitem = new ConfigItem("请求的延迟时间（秒）", ConfigItem.ComponentType.EL_NUMBER_INPUT,
                ConfigItem.DataType.INT, RequestExecutor.REQUEST_SLEEP, "请设置请求的延迟时间", "0", maxDateTime, null);
        configItemList.add(delaytimeitem);

        //请求的 URL
        ConfigItem urlitem = new ConfigItem("请求的url", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, RequestExecutor.REQUEST_URL, "请输入请求的URL", null, null, null);
        configItemList.add(urlitem);

        //请求的代理 REQUEST_PROXY
        ConfigItem proxyitem = new ConfigItem("请求的代理", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, RequestExecutor.REQUEST_PROXY, "请输入请求的代理", null, null, null);
        configItemList.add(proxyitem);

        //请求的方法 REQUEST_METHOD
        List<ConfigItem.SelectItem> remethodtem = new ArrayList<>();
        remethodtem.add(new ConfigItem.SelectItem("POST", "POST", ConfigItem.DataType.STRING));
        remethodtem.add(new ConfigItem.SelectItem("GET", "GET", ConfigItem.DataType.STRING));
        ConfigItem methoditem = new ConfigItem("请求的方法", ConfigItem.ComponentType.EL_SELECT,
                ConfigItem.DataType.STRING, RequestExecutor.REQUEST_METHOD, "请选择请求的方法", "GET", null, remethodtem);
        configItemList.add(methoditem);

        //请求的查询参数名称 REQUEST_QUERY_PARAM_NAME
        ConfigItem querynameitem = new ConfigItem("请求的查询参数名称及请求的查询参数值", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
                ConfigItem.DataType.LIST_MAP, RequestExecutor.REQUEST_QUERY_PARAM_NAME, "请输入请求的查询参数名称", new ArrayList<>(), null, null);
        configItemList.add(querynameitem);

//        //请求的查询参数值 REQUEST_QUERY_PARAM_VALUE
//        ConfigItem queryvalueitem = new ConfigItem("请求的查询参数值", ConfigItem.ComponentType.EL_INPUT,
//                ConfigItem.DataType.STRING, RequestExecutor.REQUEST_QUERY_PARAM_VALUE, "请输入请求的查询参数值", null,
//                null, null);
//        configItemList.add(queryvalueitem);

        //请求的表单参数名称 FORM_PARAM_NAME
        ConfigItem formnameitem = new ConfigItem("请求的表单参数名称及请求的表单参数值", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
                ConfigItem.DataType.LIST_MAP, RequestExecutor.FORM_PARAM_NAME, "请输入请求的表单参数名称", new ArrayList<>(),
                null, null);
        configItemList.add(formnameitem);

//        //请求的表单参数值 FORM_PARAM_VALUE
//        ConfigItem formvalueitem = new ConfigItem("请求的表单参数值", ConfigItem.ComponentType.CUSTOM_MULT_VALUE,
//                ConfigItem.DataType.STRING, RequestExecutor.FORM_PARAM_VALUE, "请输入请求的表单参数值", null,
//                null, null);
//        configItemList.add(formvalueitem);

        //请求的表单参数类型 FORM_PARAM_TYPE
        ConfigItem formtypeitem = new ConfigItem("请求的表单参数类型", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, RequestExecutor.FORM_PARAM_TYPE, "请输入请求的表单参数类型", "file",
                null, null);
        configItemList.add(formtypeitem);

        //请求的表单中文件的名称 FORM_PARAM_FILENAME
        ConfigItem formfilenameitem = new ConfigItem("请求的表单中文件的名称", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, RequestExecutor.FORM_PARAM_FILENAME, "请输入请求的表单中文件的名称", null,
                null, null);
        configItemList.add(formfilenameitem);

        //请求体的类型 BODY_TYPE
        List<ConfigItem.SelectItem> bodytype = new ArrayList<>();
        bodytype.add(new ConfigItem.SelectItem("RAW_BODY_TYPE", "raw", ConfigItem.DataType.STRING));
        bodytype.add(new ConfigItem.SelectItem("FORM_DATA_BODY_TYPE", "form-data", ConfigItem.DataType.STRING));
        bodytype.add(new ConfigItem.SelectItem("Other", "none", ConfigItem.DataType.STRING));
        ConfigItem bodytypeitem = new ConfigItem("请求体的类型", ConfigItem.ComponentType.EL_SELECT,
                ConfigItem.DataType.STRING, RequestExecutor.BODY_TYPE, "请选择请求体的类型", null, null, bodytype);
        configItemList.add(bodytypeitem);

        //请求体的正文类型（MIME Type）  BODY_CONTENT_TYPE
        ConfigItem mimetypeitem = new ConfigItem("请求体的正文类型", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, RequestExecutor.BODY_CONTENT_TYPE, "请输入请求体的正文类型", null,
                null, null);
        configItemList.add(mimetypeitem);

        //请求体 REQUEST_BODY
        ConfigItem requestbodyitem = new ConfigItem("请求体", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, RequestExecutor.REQUEST_BODY, "请输入请求体", null, null, null);
        configItemList.add(requestbodyitem);

        //请求的 Cookie 名称 REQUEST_COOKIE_NAME
        ConfigItem requestnameitem = new ConfigItem("请求的Cookie名称及Cookie的值", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
                ConfigItem.DataType.LIST_MAP, RequestExecutor.REQUEST_COOKIE_NAME, "请输入请求的 Cookie 名称", new ArrayList<>(),
                null, null);
        configItemList.add(requestnameitem);

//        //请求的 Cookie 值 REQUEST_COOKIE_VALUE
//        ConfigItem requestvalueittem = new ConfigItem("请求的 Cookie 值", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
//                ConfigItem.DataType.STRING, RequestExecutor.REQUEST_COOKIE_VALUE, "请输入请求的 Cookie 值", null,
//                null, null);
//        configItemList.add(requestvalueittem);

        //请求头名称 REQUEST_HEADER_NAME
        ConfigItem reheadernameitem = new ConfigItem("请求头名称及请求头的值", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
                ConfigItem.DataType.LIST_MAP, RequestExecutor.REQUEST_HEADER_NAME, "请输入请求头的名称", new ArrayList<>(),
                null, null);
        configItemList.add(reheadernameitem);

        //请求头的值 REQUEST_HEADER_VALUE
//        ConfigItem reheadervalueitem = new ConfigItem("请求头的值", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
//                ConfigItem.DataType.STRING, RequestExecutor.REQUEST_HEADER_VALUE, "请输入请求头的值", null,
//                null, null);
//        configItemList.add(reheadervalueitem);

        //请求超时时间 REQUEST_TIMEOUT 默认20s 最长可设置为60s
        Map<String, Object> maxtimeout = new HashMap<>();
        maxtimeout.put("min", 20000);
        ConfigItem retimeoutitem = new ConfigItem("请求超时时间（ms）", ConfigItem.ComponentType.EL_NUMBER_INPUT,
                ConfigItem.DataType.INT, RequestExecutor.REQUEST_TIMEOUT, "请设置请求超时时间", 20000,
                maxtimeout, null);
        configItemList.add(retimeoutitem);

        //请求失败后的重试次数 REQUEST_RETRY_COUNT 默认0次 最多可设置为3次
        Map<String, Object> retrycount = new HashMap<>();
        retrycount.put("min", 0);
        retrycount.put("max", 3);
        ConfigItem reretrycountitem = new ConfigItem("请求失败后的重试次数", ConfigItem.ComponentType.EL_NUMBER_INPUT,
                ConfigItem.DataType.INT, RequestExecutor.REQUEST_RETRY_COUNT, "请设置请求失败后的重试次数", 0,
                retrycount, null);
        configItemList.add(reretrycountitem);

        //重试间隔 REQUEST_RETRY_INTERVAL 默认为0毫秒 最长为100毫秒
        Map<String, Object> retryinterval = new HashMap<>();
        retryinterval.put("min", 0);
        ConfigItem retryintervalitem = new ConfigItem("重试间隔（毫秒）", ConfigItem.ComponentType.EL_NUMBER_INPUT,
                ConfigItem.DataType.INT, RequestExecutor.REQUEST_RETRY_INTERVAL, "请设置重试间隔时间", 0,
                retryinterval, null);
        configItemList.add(retryintervalitem);

        //跟随重定向 REQUEST_FOLLOW_REDIRECT
        List<ConfigItem.SelectItem> refollow = new ArrayList<>();
        refollow.add(new ConfigItem.SelectItem("是", "true", ConfigItem.DataType.BOOLEAN));
        refollow.add(new ConfigItem.SelectItem("否", "false", ConfigItem.DataType.BOOLEAN));
        ConfigItem refollowreitem = new ConfigItem("跟随重定向", ConfigItem.ComponentType.EL_SWITCH,
                ConfigItem.DataType.BOOLEAN, RequestExecutor.REQUEST_FOLLOW_REDIRECT, "请选择是否跟随重定向", null,
                null, refollow);
        configItemList.add(refollowreitem);

        //自动管理 Cookie REQUEST_AUTO_COOKIE
        ConfigItem reaotucookieitem = new ConfigItem("自动管理 Cookie", ConfigItem.ComponentType.EL_SWITCH,
                ConfigItem.DataType.BOOLEAN, RequestExecutor.REQUEST_AUTO_COOKIE, "请选择是否自动管理 Cookie", null,
                null, refollow);
        configItemList.add(reaotucookieitem);

        //随机 User-Agent RANDOM_USERAGENT
        ConfigItem reuseragentitem = new ConfigItem("随机 User-Agent", ConfigItem.ComponentType.EL_SWITCH,
                ConfigItem.DataType.BOOLEAN, RequestExecutor.RANDOM_USERAGENT, "请选择是否使用随机 User-Agent", null,
                null, refollow);
        configItemList.add(reuseragentitem);

        //响应内容编码 RESPONSE_CHARSET
        ConfigItem recharsetitem = new ConfigItem("响应内容编码", ConfigItem.ComponentType.EL_INPUT,
                ConfigItem.DataType.STRING, RequestExecutor.RESPONSE_CHARSET, "请设置响应内容编码", null,
                null, null);
        configItemList.add(recharsetitem);

        return configItemList;
    }
}

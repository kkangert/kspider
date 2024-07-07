package top.kangert.kspider.driver;

import org.openqa.selenium.WebDriver;

import top.kangert.kspider.model.SpiderNode;

import java.net.MalformedURLException;

/**
 * 浏览器驱动提供者
 */
public interface DriverProvider {

    /**
     * 默认的远程 WebDriver 地址
     */
    String DEFAULT_REMOTE_WEBDRIVER_URL = "http://localhost:4444";

    /**
     * 远程 WebDriver 地址
     */
    String REMOTE_WEBDRIVER_URL = "remote-webdriver-url";

    /**
     * 禁用 js
     */
    String JAVASCRIPT_DISABLED = "javascript-disabled";

    /**
     * User-Agent
     */
    String USER_AGENT = "user-agent";

    /**
     * 无头模式
     */
    String HEADLESS = "headless";

    /**
     * 不加载图片
     */
    String IMAGE_DISABLED = "image-disabled";

    /**
     * 隐藏滚动条（有时候需要通过隐藏滚动条来应对某些页面）
     */
    String HIDE_SCROLLBAR = "hide-scrollbar";

    /**
     * 禁用插件
     */
    String PLUGIN_DISABLE = "plugin-disable";

    /**
     * 禁用 java
     */
    String JAVA_DISABLE = "java-disable";

    /**
     * 隐身模式（无痕模式）
     */
    String INCOGNITO = "incognito";

    /**
     * 禁用沙盒模式
     */
    String NO_SANDBOX = "no-sandbox";

    /**
     * 窗口大小
     */
    String WINDOW_SIZE = "window-size";

    /**
     * 最大化
     */
    String MAXIMIZED = "maximized";

    /**
     * 禁用 gpu 加速
     */
    String GPU_DISABLE = "gpu-disable";

    /**
     * 其他参数
     */
    String ARGUMENTS = "arguments";

    /**
     * 返回支持的浏览器
     */
    String support();

    /**
     * 获取 WebDriver
     *
     * @param node     节点
     * @param proxyStr 代理
     * @return WebDriver 控制驱动对象
     */
    WebDriver getWebDriver(SpiderNode node, String proxyStr) throws MalformedURLException;
}

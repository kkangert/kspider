package top.kangert.kspider.driver;

import com.google.common.collect.ImmutableMap;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.CommandInfo;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpMethod;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 浏览器远程WEB自动化控制驱动
 */
public class CdpRemoteWebDriver extends RemoteWebDriver {

    private static final Map<String, CommandInfo> CHROME_COMMAND_NAME_TO_URL = new HashMap<String, CommandInfo>();

    /**
     * @see org.openqa.selenium.chromium.ChromiumDriverCommandExecutor
     */
    static {
        CHROME_COMMAND_NAME_TO_URL.put("launchApp", new CommandInfo("/session/:sessionId/chromium/launch_app", HttpMethod.POST));
        CHROME_COMMAND_NAME_TO_URL.put("getNetworkConditions", new CommandInfo("/session/:sessionId/chromium/network_conditions", HttpMethod.GET));
        CHROME_COMMAND_NAME_TO_URL.put("setNetworkConditions", new CommandInfo("/session/:sessionId/chromium/network_conditions", HttpMethod.POST));
        CHROME_COMMAND_NAME_TO_URL.put("deleteNetworkConditions", new CommandInfo("/session/:sessionId/chromium/network_conditions", HttpMethod.DELETE));
        CHROME_COMMAND_NAME_TO_URL.put("executeCdpCommand", new CommandInfo("/session/:sessionId/goog/cdp/execute", HttpMethod.POST));
        CHROME_COMMAND_NAME_TO_URL.put("getCastSinks", new CommandInfo("/session/:sessionId/goog/cast/get_sinks", HttpMethod.GET));
        CHROME_COMMAND_NAME_TO_URL.put("selectCastSink", new CommandInfo("/session/:sessionId/goog/cast/set_sink_to_use", HttpMethod.POST));
        CHROME_COMMAND_NAME_TO_URL.put("startDesktopMirroring", new CommandInfo("session/:sessionId/goog/cast/start_desktop_mirroring", HttpMethod.POST));
        CHROME_COMMAND_NAME_TO_URL.put("startCastTabMirroring", new CommandInfo("/session/:sessionId/goog/cast/start_tab_mirroring", HttpMethod.POST));
        CHROME_COMMAND_NAME_TO_URL.put("getCastIssueMessage", new CommandInfo("/session/:sessionId/goog/cast/get_issue_message", HttpMethod.GET));
        CHROME_COMMAND_NAME_TO_URL.put("stopCasting", new CommandInfo("/session/:sessionId/goog/cast/stop_casting", HttpMethod.POST));
        CHROME_COMMAND_NAME_TO_URL.put("setPermission", new CommandInfo("/session/:sessionId/permissions", HttpMethod.POST));
    }

    public CdpRemoteWebDriver(URL remoteAddress, Capabilities capabilities) {
        super((CommandExecutor) (new HttpCommandExecutor(ImmutableMap.copyOf(CHROME_COMMAND_NAME_TO_URL), remoteAddress)), capabilities);
    }

    /**
     * 浏览器控制命令执行
     * @param commandName 命令名称
     * @param parameters 命令参数
     * @return 结果
     */
    public Map<String, Object> executeCdpCommand(String commandName, Map<String, Object> parameters) {
        Objects.requireNonNull(commandName, "Command name must be set.");
        Objects.requireNonNull(parameters, "Parameters for command must be set.");
        Map<String, Object> toReturn = (Map) this.getExecuteMethod().execute("executeCdpCommand", ImmutableMap.of("cmd", commandName, "params", parameters));
        return ImmutableMap.copyOf(toReturn);
    }

}
package top.kangert.kspider.support;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;

import javax.annotation.PostConstruct;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户代理管理器
 */
@Component
@Slf4j
public class UserAgentManager {

    private static final String USERAGENT_FILE_PATH = "fake_useragent.json";

    private static List<BrowserUserAgent> user_agents;

    @PostConstruct
    private void initialize() {
        try {
            InputStream userAgentFileInputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(USERAGENT_FILE_PATH);
            byte[] readBytes = IoUtil.readBytes(userAgentFileInputStream);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            output.write(readBytes);
            String json = IoUtil.toStr(output, StandardCharsets.UTF_8);
            user_agents = JSONUtil.parseArray(json).toList(BrowserUserAgent.class);
        } catch (IOException e) {
            log.error("读取 {} 文件出错", USERAGENT_FILE_PATH, e);
        }
    }

    @Data
    static class BrowserUserAgent {
        private String browser;
        private List<String> useragent = new ArrayList<>();
    }

    /**
     * 获取一个随机的 User-Agent
     *
     * @return 一个随机的 User-Agent
     */
    public String getRandom() {
        int browserIndex = RandomUtil.randomInt(0, user_agents.size());
        List<String> userAgents = user_agents.get(browserIndex).useragent;
        int useragentIndex = RandomUtil.randomInt(0, userAgents.size());
        return user_agents.get(browserIndex).useragent.get(useragentIndex);
    }

    /**
     * 获取任意一款浏览器最新的 User-Agent
     *
     * @return 一个最新的 User-Agent
     */
    public String getNewest() {
        int browserIndex = RandomUtil.randomInt(0, user_agents.size());
        return user_agents.get(browserIndex).useragent.get(0);
    }

    /**
     * 获取 Chrome 浏览器最新的 User-Agent
     *
     * @return 一个最新的 User-Agent
     */
    public String getChromeNewest() {
        return user_agents.get(0).useragent.get(0);
    }

    /**
     * 获取 FireFox 浏览器最新的 User-Agent
     *
     * @return 一个最新的 User-Agent
     */
    public String getFireFoxNewest() {
        return user_agents.get(1).useragent.get(0);
    }

    /**
     * 获取 Edge 浏览器最新的 User-Agent
     *
     * @return 一个最新的 User-Agent
     */
    public String getEdgeNewest() {
        return user_agents.get(2).useragent.get(0);
    }
}

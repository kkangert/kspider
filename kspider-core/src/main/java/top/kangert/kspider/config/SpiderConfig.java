package top.kangert.kspider.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 爬虫配置
 */
@Configuration
@Getter
public class SpiderConfig {

    /**
     * 线程池最大线程数
     */
    @Value("${spider.thread-pool.max-threads}")
    private Integer maxThreads;

    /**
     * 单个任务默认的最大线程数
     */
    @Value("${spider.default-threads}")
    private Integer defaultThreads;

    /**
     * 死循环检测（节点执行次数超过该值时则认为出现了死循环）
     */
    @Value("${spider.dead-cycle}")
    private Integer deadCycle;

    /**
     * 工作目录
     */
    @Value("${spider.workspace}")
    private String workspace;

    /**
     * 是否开启定时任务
     */
    @Value("${spider.job.enabled}")
    private Boolean jobEnabled;

}

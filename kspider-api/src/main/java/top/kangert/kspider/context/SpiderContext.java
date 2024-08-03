package top.kangert.kspider.context;

import lombok.Getter;
import lombok.Setter;
import top.kangert.kspider.concurrent.SpiderThreadPoolExecutor;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.model.SpiderOutput;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 执行上下文
 */
public class SpiderContext {

    /**
     * ID
     */
    @Getter
    private String id = UUID.randomUUID().toString().replace("-", "");

    /**
     * 流程任务是否正在运行中（由用户或死锁检测等控制）
     */
    @Getter
    @Setter
    private volatile boolean running = true;

    /**
     * 调试模式
     */
    @Getter
    @Setter
    private boolean debug = false;

    /**
     * 子线程池
     */
    @Getter
    @Setter
    private SpiderThreadPoolExecutor.SubThreadPoolExecutor subThreadPool;

    /**
     * 根节点
     */
    @Getter
    @Setter
    private SpiderNode root;

    /**
     * 流程 ID
     */
    @Getter
    @Setter
    private Long flowId;

    /**
     * 任务 ID
     */
    @Getter
    @Setter
    private Long taskId;

    /**
     * Cookie 上下文
     */
    @Getter
    private Map<String, String> cookieContext = new HashMap<>();

    /**
     * 扩展集合，方便放入一些自定义的属性和值
     */
    @Getter
    private Map<String, Object> extends_map = new ConcurrentHashMap<>();

    /**
     * 当前正在执行的任务队列
     */
    @Getter
    private LinkedBlockingQueue<Future<?>> futureTaskQueue = new LinkedBlockingQueue<>();

    /**
     * 获取输出结果
     *
     * @return 默认返回空数组
     */
    public List<SpiderOutput> getOutputs() {
        return Collections.emptyList();
    }

    /**
     * 暂停运行
     *
     * @param nodeId 节点 ID
     * @param event  事件
     * @param key    属性名称
     * @param value  属性值
     */
    public void pause(String nodeId, String event, String key, Object value) {
        // 默认空实现，由子类重写
    }

    /**
     * 恢复，继续执行
     */
    public void resume() {
    }

    /**
     * 停止运行
     */
    public void stop() {
    }

    /**
     * 添加输出结果
     *
     * @param output 输出结果实体
     */
    public void addOutput(SpiderOutput output) {

    }

}

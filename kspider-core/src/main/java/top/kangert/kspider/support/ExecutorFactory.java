package top.kangert.kspider.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;

import javax.annotation.PostConstruct;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 执行器工厂
 */
@Component
public class ExecutorFactory {

    /**
     * 所有的节点执行器集合，通过容器注入
     */
    @Autowired
    private List<NodeExecutor> executors;

    /**
     * 节点类型 -> 节点执行器
     */
    private Map<String, NodeExecutor> executor_map;

    @PostConstruct
    public void initialize() {
        executor_map = this.executors.stream().collect(Collectors.toMap(NodeExecutor::supportType, v -> v));
    }

    /**
     * 获取节点执行器
     *
     * @param type 节点类型名称
     * @return 节点执行器
     */
    public NodeExecutor getExecutor(String type) {
        return executor_map.get(type);
    }

    /**
     * 获取所有的扩展图形
     *
     * @return 所有的扩展图形
     */
    public List<Shape> shapes() {
        return executors.stream().filter(e -> e.shape() != null).map(executor -> executor.shape())
                .collect(Collectors.toList());
    }

    /***
     * 获取该节点执行器的所有配置项
     * 
     * @param executorName 节点执行器名称
     * @return 配置项
     */
    public List<ConfigItem> configItemList(String executorName) {
        NodeExecutor nodeExecutor = getExecutor(executorName);
        List<ConfigItem> configItems = Collections.emptyList();
        if (nodeExecutor != null) {
            configItems = nodeExecutor.configItems();
        }
        return configItems;
    }
}

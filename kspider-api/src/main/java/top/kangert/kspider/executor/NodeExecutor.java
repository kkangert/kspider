package top.kangert.kspider.executor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;

/**
 * 节点执行器
 */
public interface NodeExecutor {

    /**
     * 执行器具体执行的逻辑
     *
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     */
    void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables);

    /**
     * 是否允许执行下一个节点
     *
     * @param node      节点
     * @param context   执行上下文
     * @param variables 传递的变量与值
     * @return 是否允许执行下一个节点
     */
    default boolean allowExecuteNext(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        return true;
    }

    /**
     * 节点对应的图形（只有扩展节点才会有该数据）
     *
     * @return 节点对应的图形
     */
    default Shape shape() {
        return null;
    }

    /**
     * 当前执行器节点的配置选项(用于前端动态渲染该节点的配置项)
     */
    default List<ConfigItem> configItems() {
        return Collections.emptyList();
    }

    /**
     * 是否能够异步执行
     *
     * @return 是否开启新线程来执行
     */
    default boolean isAsync() {
        return true;
    }

    /**
     * 支持的节点类型
     *
     * @return 节点类型名称
     */
    String supportType();

}

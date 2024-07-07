package top.kangert.kspider.executor.node;

import org.springframework.stereotype.Component;

import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.websocket.WebSocketEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * 等待执行结束执行器
 */
@Component
public class JoinExecutor implements NodeExecutor {

    /**
     * 缓存已完成节点的变量
     */
    private Map<String, Map<String, Object>> cachedVariables = new HashMap<>();

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, "JoinExecutor", "JoinExecutor");
    }

    @Override
    public boolean allowExecuteNext(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String key = context.getId() + "-" + node.getNodeId();
        synchronized (cachedVariables) {
            boolean isDone = node.isDone();
            Map<String, Object> cached = cachedVariables.get(key);
            // 所有的节点未完成
            if (!isDone) {
                if (cached == null) {
                    cached = new HashMap<>();
                    cachedVariables.put(key, cached);
                }
                cached.putAll(variables);
            } else if (cached != null) {
                // 将缓存的变量存入到当前变量中，传递给下一级
                variables.putAll(cached);
                cachedVariables.remove(key);
            }
            return isDone;
        }
    }

    @Override
    public String supportType() {
        return "join";
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "等待", "等待", "iconfont icon-waiting", "等待上一个节点执行结束");
    }

}

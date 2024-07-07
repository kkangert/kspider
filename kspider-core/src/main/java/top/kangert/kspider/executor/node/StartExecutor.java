package top.kangert.kspider.executor.node;

import org.springframework.stereotype.Component;

import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.websocket.WebSocketEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 开始执行器
 */
@Component
public class StartExecutor implements NodeExecutor {

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, "StartExecutor", "StartExecutor");
    }

    @Override
    public String supportType() {
        return "start";
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "开始", "开始", "ele-Flag", "标志着流程从这里开始");
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<>();

        // 最大线程数配置项
        Map<String, Object> maxThreadsAttr = new HashMap<>();
        maxThreadsAttr.put("min", 4);
        maxThreadsAttr.put("max", 12);
        ConfigItem maxThreads = new ConfigItem("最大线程数", ConfigItem.ComponentType.EL_NUMBER_INPUT,
                ConfigItem.DataType.INT, Constants.THREAD_COUNT, "请输入爬虫最大线程数", "4", maxThreadsAttr, null);
        configItemList.add(maxThreads);

        return configItemList;
    }

}

package top.kangert.kspider.executor.node;

import org.springframework.stereotype.Component;

import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.model.ConfigItem.ComponentType;
import top.kangert.kspider.model.ConfigItem.DataType;
import top.kangert.kspider.websocket.WebSocketEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 循环执行器
 */
@Component
public class LoopExecutor implements NodeExecutor {

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, Constants.NODE_LOOP_INDEX, variables.get(Constants.NODE_LOOP_INDEX));
    }

    @Override
    public String supportType() {
        return "loop";
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "循环", "循环", "ele-Refresh", "标志着流程从这里开始,进入循环状态");
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<ConfigItem>();

        ConfigItem configStart = new ConfigItem("开始下标", ComponentType.EL_INPUT, DataType.INT,
                Constants.NODE_LOOP_START_INDEX, "请输入开始下标", "0", null, null);
        configItemList.add(configStart);

        ConfigItem configEnd = new ConfigItem("结束下标", ComponentType.EL_INPUT, DataType.INT,
                Constants.NODE_LOOP_END_INDEX, "请输入结束下标", "0", null, null);
        configItemList.add(configEnd);

        ConfigItem configIndex = new ConfigItem("循环次数", ComponentType.EL_INPUT, DataType.STRING,
                Constants.NODE_LOOP_COUNT, "请输入循环次数（数字或表达式）", "0", null, null);
        configItemList.add(configIndex);

        ConfigItem configValue = new ConfigItem("循环下标", ComponentType.EL_INPUT, DataType.STRING,
                Constants.NODE_LOOP_INDEX, "请输入循环下标名称", Constants.NODE_LOOP_INDEX, null, null);
        configItemList.add(configValue);
        return configItemList;
    }

}

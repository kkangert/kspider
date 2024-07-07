package top.kangert.kspider.executor.node;

import lombok.extern.slf4j.Slf4j;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.support.ExpressionEngine;
import top.kangert.kspider.websocket.WebSocketEvent;

import org.springframework.stereotype.Component;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 定义变量执行器
 */
@Component
@Slf4j
public class VariableExecutor implements NodeExecutor {

    private final String VARIABLE = "variable";

    @Resource
    private ExpressionEngine expressionEngine;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        List<Map<String, String>> variableList = node.getJsonArrayProperty(VARIABLE);
        for (Map<String, String> nameValue : variableList) {
            String variableStr = nameValue.get(VARIABLE);
            Map<String, String> variableMap = JSONUtil.toBean(variableStr, new TypeReference<Map<String, String>>() {
            }, false);

            String variableKey = variableMap.get("key");
            String variableValue = variableMap.get("value");
            Object value = null;
            try {
                value = expressionEngine.execute(variableValue, variables);
                log.debug("设置变量 {} = {}", variableKey, value);
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, variableKey, value);
            } catch (Exception e) {
                log.error("设置变量 {} 出错", variableKey, e);
                ExceptionUtil.wrapAndThrow(e);
            }
            variables.put(variableKey, value);
        }
    }

    @Override
    public String supportType() {
        return "variable";
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<>();

        ConfigItem configItemName = new ConfigItem("变量列表", ConfigItem.ComponentType.CUSTOM_MULT_KEY_VALUE,
                ConfigItem.DataType.LIST_MAP, VARIABLE, null, new ArrayList<>(), null, null);
        configItemList.add(configItemName);

        return configItemList;
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "变量", "变量", "ele-Share", "提取变量的值");
    }

}

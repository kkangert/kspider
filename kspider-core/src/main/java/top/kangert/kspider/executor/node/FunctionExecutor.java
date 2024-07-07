package top.kangert.kspider.executor.node;

import lombok.extern.slf4j.Slf4j;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.Shape;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.support.ExpressionEngine;
import top.kangert.kspider.websocket.WebSocketEvent;

import org.springframework.stereotype.Component;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 函数执行器
 */
@Component
@Slf4j
public class FunctionExecutor implements NodeExecutor {

    @Resource
    private ExpressionEngine expressionEngine;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        List<Map<String, String>> functions = node.getJsonArrayProperty(Constants.FUNCTION);
        for (Map<String, String> item : functions) {
            Object functionStr = item.get(Constants.FUNCTION);
            JSONObject functionJson = JSONUtil.parseObj(functionStr);
            String function = functionJson.getStr("value");
            if (StrUtil.isNotBlank(function)) {
                try {
                    log.debug("执行函数 {}", function);
                    context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, functionJson.getStr("key"), function);
                    expressionEngine.execute(function, variables);
                } catch (Exception e) {
                    log.error("执行函数 {} 失败", function, e);
                    ExceptionUtil.wrapAndThrow(e);
                }
            }
        }
    }

    @Override
    public String supportType() {
        return "function";
    }

    @Override
    public Shape shape() {
        Shape shape = new Shape();
        shape.setName(supportType());
        shape.setLabel("函数");
        shape.setDesc("可使用全局函数和已经定义的内置函数");
        shape.setIcon("iconfont icon-terminal");
        return shape;
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<ConfigItem>();

        // 函数操作（可多种不同函数）
        ConfigItem functions = new ConfigItem("函数", ConfigItem.ComponentType.CUSTOM_MULT_VALUE,
                ConfigItem.DataType.LIST_MAP, Constants.FUNCTION, null, new ArrayList<>(), null, null);
        configItemList.add(functions);

        return configItemList;
    }

}

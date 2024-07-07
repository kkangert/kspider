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

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 延迟执行器
 */
@Component
@Slf4j
public class DelayExecutor implements NodeExecutor {

    /**
     * 延迟执行时间
     */
    private static final String DELAY_TIME = "delayTime";

    @Resource
    private ExpressionEngine expressionEngine;

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String delayTimes = node.getJsonProperty(DELAY_TIME);
        if (StrUtil.isNotBlank(delayTimes)) {
            try {
                Object value = expressionEngine.execute(delayTimes, variables);
                Long times = 0L;
                if (value instanceof String) {
                    times = Convert.toLong(value, 0L);
                } else if (value instanceof Integer) {
                    times = ((Integer) value).longValue();
                } else {
                    times = (Long) value;
                }
                if (times > 0) {
                    // 睡眠
                    try {
                        log.info("设置延迟执行时间：{} ms", times);
                        context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, DELAY_TIME, times);
                        TimeUnit.MILLISECONDS.sleep(times);
                    } catch (Throwable t) {
                        log.error("设置延迟执行时间失败", t);
                    }
                }
            } catch (Exception e) {
                log.error("解析延迟执行时间：{} 失败", delayTimes, e);
            }
        }
    }

    @Override
    public String supportType() {
        return "delay";
    }

    @Override
    public Shape shape() {
        return new Shape(supportType(), "延时", "延时", " iconfont icon-loading", "标志着该节点会等待指定时长");
    }

    @Override
    public List<ConfigItem> configItems() {
        List<ConfigItem> configItemList = new ArrayList<>();

        // 延时时间
        Map<String, Object> delayTimeAttrs = new HashMap<>();
        delayTimeAttrs.put("min", 0);
        ConfigItem delayTime = new ConfigItem("延时时间(ms)", ConfigItem.ComponentType.EL_NUMBER_INPUT,
                ConfigItem.DataType.INT,
                DELAY_TIME, "请输入延时时间", 0, delayTimeAttrs, null);
        configItemList.add(delayTime);

        return configItemList;
    }

}

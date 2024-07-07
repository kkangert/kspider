package top.kangert.kspider.executor.node;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.executor.NodeExecutor;
import top.kangert.kspider.executor.node.event.OutputEventPublisher;
import top.kangert.kspider.io.SpiderResponse;
import top.kangert.kspider.listener.SpiderListener;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.model.SpiderOutput;
import top.kangert.kspider.support.ExpressionEngine;
import top.kangert.kspider.websocket.WebSocketEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * 输出执行器
 */
@Component
@Slf4j
public class OutputExecutor implements NodeExecutor, SpiderListener {

    /**
     * 输出其他变量
     */
    String OUTPUT_OTHERS = "output-others";

    /**
     * 输出项的名称
     */
    String OUTPUT_NAME = "output-name";

    /**
     * 输出项的值
     */
    String OUTPUT_VALUE = "output-value";

    @Resource
    private ExpressionEngine expressionEngine;

    @Resource
    private OutputEventPublisher outputEventPublisher;

    /**
     * 一个节点对应一个 CSVPrinter
     */
    @Getter
    private static Map<String, CSVPrinter> cachePrinter = new HashMap<>();

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        // 创建输出结果对象
        SpiderOutput output = new SpiderOutput();
        output.setNodeName(node.getNodeName());
        output.setNodeId(node.getNodeId());
        // 是否需要输出其他变量（只在测试时输出）
        boolean outputOthers = Constants.YES.equals(node.getJsonProperty(OUTPUT_OTHERS));
        // 如果需要输出其他变量
        if (outputOthers) {
            // 输出其他变量
            this.outputOtherVariables(output, variables);
        }
        // 获取所有的输出项数据
        List<SpiderOutput.OutputItem> outputItems = this.getOutputItems(variables, context, node);
        // 发布输出事件
        outputEventPublisher.publish(context, node, outputItems);
        // 将所有的输出项放入
        output.getOutputItems().addAll(outputItems);
        // 添加输出结果到上下文
        context.addOutput(output);
    }

    /**
     * 获取用户定义的所有输出项的数据
     *
     * @param variables 传递的变量和值
     * @param context   执行上下文
     * @param node      节点
     * @return 所有输出项的数据
     */
    private List<SpiderOutput.OutputItem> getOutputItems(Map<String, Object> variables, SpiderContext context,
            SpiderNode node) {
        List<SpiderOutput.OutputItem> outputItems = new ArrayList<>();
        // 获取用户设置的所有输出项
        List<Map<String, String>> items = node.getJsonArrayProperty(OUTPUT_NAME, OUTPUT_VALUE);
        for (Map<String, String> item : items) {
            Object value = null;
            String itemName = item.get(OUTPUT_NAME);
            String itemValue = item.get(OUTPUT_VALUE);
            try {
                value = expressionEngine.execute(itemValue, variables);
                context.pause(node.getNodeId(), WebSocketEvent.COMMON_EVENT, itemName, value);
                log.debug("解析输出项：{} = {}", itemName, value);
            } catch (Exception e) {
                log.error("解析数据项：{} 出错", itemName, e);
            }
            outputItems.add(new SpiderOutput.OutputItem(itemName, value));
        }
        return outputItems;
    }

    /**
     * 填充其他变量
     *
     * @param output    输出结果
     * @param variables 传递的变量和值
     */
    private void outputOtherVariables(SpiderOutput output, Map<String, Object> variables) {
        for (Map.Entry<String, Object> item : variables.entrySet()) {
            Object value = item.getValue();
            // resp 变量
            if (value instanceof SpiderResponse) {
                SpiderResponse resp = (SpiderResponse) value;
                output.addItem(item.getKey() + ".html", resp.getHtml());
                continue;
            }
            // 去除不输出的信息
            if (Constants.EXCEPTION_VARIABLE.equals(item.getKey())) {
                continue;
            }
            // 去除不能序列化的参数
            try {
                JSONUtil.toJsonStr(value, JSONConfig.create());
            } catch (Exception e) {
                continue;
            }
            // 其他情况正常添加
            output.addItem(item.getKey(), item.getValue());
        }
    }

    @Override
    public void beforeStart(SpiderContext context) {

    }

    @Override
    public void afterEnd(SpiderContext context) {
        String key = context.getId();
        // 执行完毕后释放缓存，只释放同一个执行上下文中的缓存
        this.releasePrinter(key);
    }

    @Override
    public String supportType() {
        return "output";
    }

    private void releasePrinter(String key) {
        log.debug("release printer：key = {}", key);
        for (Iterator<Map.Entry<String, CSVPrinter>> iterator = cachePrinter.entrySet().iterator(); iterator
                .hasNext();) {
            Map.Entry<String, CSVPrinter> entry = iterator.next();
            CSVPrinter printer = entry.getValue();
            if (entry.getKey().contains(key)) {
                if (printer != null) {
                    try {
                        printer.flush();
                        printer.close();
                        iterator.remove();
                    } catch (IOException e) {
                        log.error("文件输出出现错误", e);
                        ExceptionUtil.wrapAndThrow(e);
                    }
                }
            }
        }
    }
}

package top.kangert.kspider.executor.node.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.constant.OutputType;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.model.SpiderOutput;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import cn.hutool.core.convert.Convert;

import java.util.List;

@Component
public class OutputEventPublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 发布输出事件
     *
     * @param context     执行上下文
     * @param node        节点
     * @param outputItems 所有的输出项数据
     */
    public void publish(SpiderContext context, SpiderNode node, List<SpiderOutput.OutputItem> outputItems) {
        OutputType[] outputTypes = OutputType.values();
        List<String> outputTypeArr = Convert.toList(String.class, node.getJsonProperty(Constants.OUTPUT_TYPE));
        for (OutputType outputType : outputTypes) {
            for (String userSelected : outputTypeArr) {
                if (userSelected.equals(outputType.getVariableName())) {
                    eventPublisher.publishEvent(new OutputEventBean(context, node, outputItems, outputType.getVariableName()));
                }
            }
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    class OutputEventBean {
        private SpiderContext context;
        private SpiderNode node;
        private List<SpiderOutput.OutputItem> outputItems;
        private String event;
    }

}

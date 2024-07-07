package top.kangert.kspider.vo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import top.kangert.kspider.model.ConfigItem;

@Getter
@Setter
public class SpiderNodeVo {
    /**
     * 节点执行器ID
     */
    private String nodeId;

    // 节点名称
    private String name;

    /**
     * 源执行器节点ID
     */
    private String sourceId;

    /**
     * 目标执行器节点ID
     */
    private String targetId;

    private String condition;

    private String exceptionFlow;

    private String transmitVariable;

    /**
     * 配置项表单
     */
    private List<ConfigItem> form;

}

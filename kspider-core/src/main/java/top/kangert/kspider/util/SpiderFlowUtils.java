package top.kangert.kspider.util;

import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;
import top.kangert.kspider.model.ConfigItem;
import top.kangert.kspider.model.SpiderNode;
import top.kangert.kspider.vo.SpiderNodeVo;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程处理工具类
 */
public class SpiderFlowUtils {
    /**
     * 加载流程图
     * 
     * @param json json
     * @return
     */
    public static SpiderNode parseJsonToSpiderNode(String json) {

        JSONObject parseObj = null;
        try {
            parseObj = JSONUtil.parseObj(json);
        } catch (Exception e) {
            throw new BaseException(ExceptionCodes.PARSE_JSON_ERROR);
        }

        Map<String, SpiderNode> nodeMap = new HashMap<>();
        SpiderNode fristNode = null;

        // 处理节点 解析节点json转换
        List<SpiderNodeVo> spiderNodeList = parseObj.getBeanList("nodeList", SpiderNodeVo.class);
        if (spiderNodeList == null) {
            throw new BaseException(ExceptionCodes.PARSE_JSON_ERROR);
        }
        for (SpiderNodeVo spiderNodeVo : spiderNodeList) {
            SpiderNode spiderNode = new SpiderNode();
            spiderNode.setNodeId(spiderNodeVo.getNodeId());
            spiderNode.setNodeName(spiderNodeVo.getName());
            Map<String, Object> spiderFlowJsonProperty = new HashMap<>();
            spiderFlowJsonProperty.put(Constants.NODE_TYPE, spiderNodeVo.getName());
            if (spiderNodeVo.getForm().size() != 0) {
                getSpiderFlowJsonProperty(spiderFlowJsonProperty, spiderNodeVo.getForm());
            }
            spiderNode.setJsonProperty(spiderFlowJsonProperty);
            nodeMap.put(spiderNode.getNodeId(), spiderNode);
            // 判断启动节点
            if ("start".equals(spiderNodeVo.getName())) {
                fristNode = spiderNode;
            }
        }

        // 处理连线 解析连线json转换
        List<SpiderNodeVo> connectList = parseObj.getBeanList("lineList", SpiderNodeVo.class);
        if (connectList == null) {
            throw new BaseException(ExceptionCodes.PARSE_JSON_ERROR);
        }
        for (SpiderNodeVo spiderNodeVo : connectList) {
            if (nodeMap.containsKey(spiderNodeVo.getSourceId()) && StrUtil.isNotBlank(spiderNodeVo.getTargetId())) {
                SpiderNode currNode = nodeMap.get(spiderNodeVo.getSourceId());

                SpiderNode targetNode = nodeMap.get(spiderNodeVo.getTargetId());
                // 设置流转条件
                targetNode.setCondition(currNode.getNodeId(), spiderNodeVo.getCondition());
                // 设置流转特性
                targetNode.setConditionType(currNode.getNodeId(), spiderNodeVo.getExceptionFlow());
                targetNode.setTransmitVariable(currNode.getNodeId(), spiderNodeVo.getTransmitVariable());
                // 添加节点
                currNode.addNextNode(targetNode);
            }
        }
        return fristNode;
    }

    /**
     * 获取流转条件
     * 
     * @param fromList 表单信息
     * @return
     */
    private static void getSpiderFlowJsonProperty(Map<String, Object> jsonProperty,
            List<ConfigItem> fromList) {
        if (fromList.size() > 0) {
            // 提取form配置属性
            fromList.stream().forEach(from -> {
                Object transValue = ConfigItem.transform(from.getValue(), from.getDataType());
                jsonProperty.put(from.getPropName(), transValue);
            });
        }

    }
}

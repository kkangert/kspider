package top.kangert.kspider.model;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.Setter;
import top.kangert.kspider.constant.ConditionType;
import top.kangert.kspider.constant.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 节点
 */
public class SpiderNode {

    /**
     * 节点 ID
     */
    @Getter
    @Setter
    private String nodeId;

    /**
     * 节点名称
     */
    @Getter
    @Setter
    private String nodeName;

    /**
     * 用来计算当前节点正在执行中的任务个数
     */
    private AtomicInteger taskCounter = new AtomicInteger();

    /**
     * 节点属性
     */
    @Setter
    private Map<String, Object> jsonProperty = new HashMap<>();

    /**
     * 前面的节点列表
     */
    private List<SpiderNode> prevNodes = new ArrayList<>();

    /**
     * 后面的节点列表
     */
    @Getter
    private List<SpiderNode> nextNodes = new ArrayList<>();

    /**
     * 存放的是是否需要将上一节点的变量和值传递到当前节点的设置（1 表示需要 0 表示不需要）
     */
    private Map<String, String> transmitVariables = new HashMap<>();

    /**
     * 存放的是上一节点流转到当前节点的条件表达式
     */
    private Map<String, String> conditions = new HashMap<>();

    /**
     * 存放的是上一节点流转到当前节点的条件类型
     *
     * @see ConditionType
     */
    private Map<String, String> conditionTypes = new HashMap<>();

    /**
     * 获取节点属性值
     *
     * @param key 属性名称
     * @return 属性值
     */
    public String getJsonProperty(String key) {
        String value = Convert.toStr(jsonProperty.get(key));
        if (value != null) {
            // 取消转义
            value = EscapeUtil.unescapeHtml4(value);
        }
        return value;
    }

    /**
     * 获取节点属性值
     *
     * @param key          属性名称
     * @param defaultValue 默认值
     * @return 属性值
     */
    public String getJsonProperty(String key, String defaultValue) {
        String value = this.getJsonProperty(key);
        return StrUtil.isNotBlank(value) ? value : defaultValue;
    }

    /**
     * 添加下一个节点
     *
     * @param nextNode 下一个节点
     */
    public void addNextNode(SpiderNode nextNode) {
        nextNode.prevNodes.add(this);
        this.nextNodes.add(nextNode);
    }

    /**
     * 获取节点的多个属性值，这些属性值都是 JSON 数组
     *
     * @param keys 属性名数组
     * @return 多个属性值
     */
    public List<Map<String, String>> getJsonArrayProperty(String... keys) {
        int size = -1;
        List<JSONArray> arrays = new ArrayList<>();
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < keys.length; i++) {
            JSONArray jsonArray = JSONUtil.parseArray(this.jsonProperty.get(keys[i]));
            if (jsonArray != null) {
                // 保证 size 只赋值一次
                if (size == -1) {
                    size = jsonArray.size();
                }
                // 确保它们的元素个数一致
                if (size != jsonArray.size()) {
                    throw new ArrayIndexOutOfBoundsException();
                }
                arrays.add(jsonArray);
            }
        }
        for (int i = 0; i < size; i++) {
            Map<String, String> item = new HashMap<>();
            for (int j = 0; j < keys.length; j++) {
                String value = arrays.get(j).getStr(i);
                if (value != null) {
                    value = EscapeUtil.unescapeHtml4(value);
                }
                item.put(keys[j], value);
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 是否需要将上一节点的变量和值传递到当前节点
     *
     * @param fromNodeId 上一节点的 ID
     * @return 是否需要传递变量和值
     */
    public boolean needTransmit(String fromNodeId) {
        String value = this.transmitVariables.get(fromNodeId);
        // 如果值为空则默认需要传递变量
        return StrUtil.isBlank(value) || Constants.YES.equals(value);
    }

    /**
     * 正在执行的任务数 +1
     */
    public void increment() {
        taskCounter.incrementAndGet();
    }

    /**
     * 正在执行的任务数 -1
     */
    public void decrement() {
        taskCounter.decrementAndGet();
    }

    /**
     * 当前节点以及它前面的节点的任务是否全部完成
     *
     * @return 是否全部完成
     */
    public boolean isDone() {
        return isDone(new HashSet<>());
    }

    /**
     * 当前节点以及它前面的节点的任务是否全部完成
     *
     * @param visited 记录访问过的节点
     * @return 是否全部完成
     */
    public boolean isDone(Set<String> visited) {
        if (this.taskCounter.get() == 0) {
            for (SpiderNode prevNode : prevNodes) {
                if (visited.add(nodeId) && !prevNode.isDone(visited)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 上一节点流转到当前节点的条件表达式
     *
     * @param fromNodeId 上一节点的 ID
     * @return 条件表达式
     */
    public String getCondition(String fromNodeId) {
        return this.conditions.get(fromNodeId);
    }

    /**
     * 上一节点流转到当前节点的条件类型
     *
     * @param fromNodeId 上一节点的 ID
     * @return 条件类型
     */
    public String getConditionType(String fromNodeId) {
        return this.conditionTypes.get(fromNodeId);
    }

    /**
     * 设置是否需要将上一节点的变量和值传递到当前节点
     *
     * @param fromNodeId 上一节点的 ID
     * @param value      是否需要传递变量和值的设置
     */
    public void setTransmitVariable(String fromNodeId, String value) {
        this.transmitVariables.put(fromNodeId, value);
    }

    /**
     * 设置上一节点流转到当前节点的条件表达式
     *
     * @param fromNodeId 上一节点的 ID
     * @param value      条件表达式
     */
    public void setCondition(String fromNodeId, String value) {
        this.conditions.put(fromNodeId, value);
    }

    /**
     * 设置上一节点流转到当前节点的条件类型
     *
     * @param fromNodeId 上一节点的 ID
     * @param value      条件类型
     */
    public void setConditionType(String fromNodeId, String value) {
        this.conditionTypes.put(fromNodeId, value);
    }

    @Override
    public String toString() {
        return "SpiderNode{" + "nodeId='" + nodeId + '\'' + ", nodeName='" + nodeName + '\'' + '}';
    }
}

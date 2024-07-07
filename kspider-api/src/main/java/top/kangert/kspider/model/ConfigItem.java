package top.kangert.kspider.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;

/**
 * 当前执行器节点的配置项(用于前端动态渲染该节点的配置项)
 */
@Data
@AllArgsConstructor
public class ConfigItem {
    /**
     * 输入项标签名称
     */
    private String labelName = "";

    /**
     * 输入项组件类型(决定前端表单该项类型)
     */
    private ComponentType componentType = ComponentType.EL_INPUT;

    /**
     * 当前配置项数据类型
     */
    public DataType dataType = DataType.STRING;

    /**
     * 属性名称(用于前后端读写值)
     */
    private String propName = "";

    /**
     * 提示信息
     */
    private String placeholder = "";

    /**
     * 当前配置项的值
     */
    private Object value;

    /**
     * 该组件特定的配置属性
     */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * 针对下拉选择的选项
     */
    private List<SelectItem> childrenItem = Collections.emptyList();

    /**
     * 是否必填项
     */
    private Boolean required = false;

    public ConfigItem(String labelName, ComponentType componentType, DataType dataType, String propName,
            String placeholder, Object value, Map<String, Object> attributes, List<SelectItem> childrenItem) {
        this.labelName = labelName;
        this.componentType = componentType;
        this.dataType = dataType;
        this.propName = propName;
        this.placeholder = placeholder;
        this.value = value;
        this.attributes = attributes;
        this.childrenItem = childrenItem;
    }

    @Override
    public String toString() {
        Map<String, Object> data = new HashMap<>();
        data.put("labelName", this.labelName);
        data.put("componentType", this.componentType.toString());
        data.put("propName", this.propName);
        data.put("value", transform(this.value, this.dataType));
        data.put("attributes", this.attributes);
        data.put("childrenItem", this.childrenItem.toString());
        return JSONUtil.parseObj(data).toString();
    }

    /**
     * 当执行器节点的配置项属于多选或单选，则提供此对象
     */
    @Data
    @AllArgsConstructor
    public static class SelectItem {

        /**
         * 选择项标签名称
         */
        private String labelName = "";

        /**
         * 选项的Value
         */
        public Object value = null;

        /**
         * 选项的数据类型
         */
        public DataType dataType = DataType.INT;

        @Override
        public String toString() {
            Map<String, Object> data = new HashMap<>();
            data.put("labelName", this.labelName);
            data.put("value", transform(this.value, this.dataType));
            return JSONUtil.parseObj(data).toString();
        }

    }

    /**
     * 组件类型枚举
     */
    public enum ComponentType {
        /**
         * 选择框（单选）, 需要设定childrenItem, 值类型取决于 dataType属性
         */
        EL_SELECT("el-select"),

        /**
         * 多选组件
         */
        EL_MULT_SELECT("EL_MULT_SELECT"),

        /**
         * 开关组件(针对需要布尔值的)
         */
        EL_SWITCH("el-switch"),

        /**
         * 普通输入框, 值类型取决于 dataType属性
         */
        EL_INPUT("el-input"),

        /**
         * 自定义多键值组件(数组)
         */
        CUSTOM_MULT_KEY_VALUE("MultKeyValue"),

        /**
         * 自定义多值组件(数组)
         */
        CUSTOM_MULT_VALUE("MultValue"),

        /**
         * 数字输入框, 该组件个性化配置可书写至 attributes属性
         */
        EL_NUMBER_INPUT("el-input-number");

        private String name = "";

        ComponentType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

    }

    /**
     * 支持的数据类型
     */
    public enum DataType {
        /**
         * Java内部：字符串、JS内部：字符串
         */
        STRING("string"),

        /**
         * Java内部：双精度、JS内部：数字型
         */
        DOUBLE("number"),

        /**
         * Java内部：整型、JS内部：数字型
         */
        INT("number"),

        /**
         * Java内部：字符串数组、JS内部：字符串数组
         */
        LIST_STRING("array<string>"),

        /***
         * Java内部：浮点数据数组、JS内部：数字型数组
         */
        LIST_DOUBLE("array<number>"),

        /***
         * Java内部：整型数组、JS内部：数字型数组
         */
        LIST_INT("array<number>"),

        /**
         * Java内部: Map型数组、JS内部: Map型数组
         */
        LIST_MAP("array<map>"),

        /**
         * Java内部: 布尔值、JS内部: 布尔值
         */
        BOOLEAN("boolean");

        private String name;

        DataType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }

    }

    /**
     * 数据类型转换(TODO: 需要支持其他数据类型的转换，不紧急)
     * 
     * @param str      原始值
     * @param dataType 数据类型
     * @return 转换后的数据类型
     */
    public static Object transform(Object str, DataType dataType) {
        Object tempVal = null;
        switch (dataType) {
            case STRING:
                tempVal = str;
                break;
            case DOUBLE:
                tempVal = Convert.toDouble(str);
                break;
            case INT:
                tempVal = Convert.toInt(str);
                break;
            case LIST_STRING:
                tempVal = Convert.toList(String.class, str);
                break;
            case LIST_DOUBLE:
                break;
            case LIST_INT:
                break;
            case LIST_MAP:
                tempVal = Convert.toList(Map.class, str);
                break;
            case BOOLEAN:
                tempVal = Convert.toBool(str);
                break;
            default:
                break;
        }
        return tempVal;
    }

    public static Object transform(Object str, String dataType) {
        DataType tempDatType = DataType.valueOf(dataType);
        Object transform = transform(str, tempDatType);
        return transform;
    }
}

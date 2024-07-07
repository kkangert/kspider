package top.kangert.kspider.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 爬虫输出对象
 */
@Getter
@Setter
public class SpiderOutput {

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点 ID
     */
    private String nodeId;

    /**
     * 所有的输出项
     */
    @Getter
    private List<OutputItem> outputItems = new ArrayList<>();


    @AllArgsConstructor
    public static class OutputItem {

        /**
         * 输出项的名称
         */
        @Getter
        private String name;

        /**
         * 输出项的值
         */
        @Getter
        private Object value;


        @Override
        public String toString() {
            return "OutputItem{" +
                    "name='" + name + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    /**
     * 添加一个输出项
     *
     * @param name  输出项的名称
     * @param value 输出项的值
     */
    public void addItem(String name, Object value) {
        this.outputItems.add(new OutputItem(name, value));
    }
}

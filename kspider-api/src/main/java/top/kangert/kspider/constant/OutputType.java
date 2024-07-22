package top.kangert.kspider.constant;

import lombok.Getter;

/**
 * 输出类型
 */
public enum OutputType {

    /**
     * 数据库输出
     */
    DATABASE("output-database"),

    /**
     * UI输出，主要用于测试
     */
    UI("output-ui"),

    /**
     * CSV文件输出
     */
    CSV("output-csv");

    // 待扩展

    @Getter
    private String variableName;

    OutputType(String variableName) {
        this.variableName = variableName;
    }
}

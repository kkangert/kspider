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

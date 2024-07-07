package top.kangert.kspider.constant;

import lombok.Getter;

/**
 * 流转条件类型
 */
public enum ConditionType {

    DIRECTION("直接流转", "0"),
    ON_EXCEPTION("当出现异常时流转", "1"),
    NO_EXCEPTION("当没有出现异常时流转", "2");

    /**
     * 描述
     */
    @Getter
    String description;

    /**
     * 编码
     */
    @Getter
    String code;

    ConditionType(String description, String code) {
        this.description = description;
        this.code = code;
    }
}

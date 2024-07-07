package top.kangert.kspider.constant;

import lombok.Getter;

public enum RequestBodyType {

    /**
     * 空类型
     */
    NONE("none"),

    /**
     * 原样数据
     */
    RAW_BODY_TYPE("raw"),

    /**
     * 表单数据
     */
    FORM_DATA_BODY_TYPE("form-data");

    @Getter
    private String bodyType;

    RequestBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public static RequestBodyType geRequestBodyType(String bodyType) {
        for (RequestBodyType requestBodyType : values()) {
            if (requestBodyType.getBodyType().equals(bodyType)) {
                return requestBodyType;
            }
        }
        return null;
    }
}

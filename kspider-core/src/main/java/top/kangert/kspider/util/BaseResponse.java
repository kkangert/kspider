package top.kangert.kspider.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import top.kangert.kspider.vo.PageVo;

@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {
    // 响应码
    private int code;

    // 响应消息
    private String message;

    // 响应实体信息
    private Object data;

    // 分页信息
    private PageVo pageInfo;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public PageVo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageVo pageInfo) {
        this.pageInfo = pageInfo;
    }
}

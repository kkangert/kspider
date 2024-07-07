package top.kangert.kspider.support;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错误响应实体
 */
@Data
public class ErrorResponse {

    @JsonIgnore
    private Integer status;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private ErrorResponse() {
        timestamp = LocalDateTime.now();
    }

    public ErrorResponse(Integer status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
}
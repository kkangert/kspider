package top.kangert.kspider.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常拦截器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 拦截非业务异常
     * 
     * @param e 异常类型
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        // 将异常信息写入日志
        log.error(e.getMessage(), e);
        // 输出通用错误代码和信息
        Map<String, Object> map = new HashMap<>();
        map.put("code", ExceptionCodes.ERROR.getCode());
        map.put("message", ExceptionCodes.ERROR.getMessage());
        return map;
    }

    /**
     * 拦截业务异常
     * 
     * @param e 异常类型
     * @return 异常信息
     */
    @ResponseBody
    @ExceptionHandler(BaseException.class)
    public Map<String, Object> handleBaseException(BaseException e) {
        // 将异常信息写入日志
        log.error("业务异常：code：{}，messageId：{}，message：{}", e.getCode(), e.getMessageId(), e.getMessage());
        // 输出错误代码和信息
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("message", e.getMessage());
        return map;
    }

}

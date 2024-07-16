package top.kangert.kspider.exception;

/**
 * 异常基类（所有异常需要通过此类抛出）
 */
public class BaseException extends RuntimeException {

    // 异常码
    private int code;

    // 异常信息ID
    private String messageId;

    // 异常信息
    private String message;

    public BaseException(String message) {
        this.message = message;
    }

    public BaseException(String message, Throwable e) {
        this.message = message;
    }

    public BaseException(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public BaseException(ExceptionCodes e) {
        this.code = e.getCode();
        this.messageId = e.getMessageId();
        this.message = e.getMessage();
    }

    public BaseException(ExceptionCodes e, String message) {
        this.code = e.getCode();
        this.messageId = e.getMessageId();
        this.message = e.getMessage() + ":" + message;
    }

    public BaseException(int code, String message, Throwable e) {
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

package top.kangert.kspider.exception;

public enum ExceptionCodes {
    // 0-99，通用异常
    SUCCESS(0, "message.SUCCESS", "操作成功"),
    FAILED(1, "message.FAILED", "操作失败"),
    ARGUMENTS_ERROR(2, "message.ARGUMENTS_ERROR", "参数错误"),
    FILE_UPLOAD_FAILED(3, "message.FILE_UPLOAD_FAILED", "上传失败"),
    OBJECT_IS_NULL(4, "message.OBJECT_IS_NULL", "对象为空"),
    FILE_EXPORT_FAILED(5, "message.FILE_EXPORT_FAILED", "文件导出失败"),
    ERROR(99, "message.ERROR", "操作异常"),
    BIND_ERROR(100, "message.BIND_ERROR", "已存在绑定关系,无需重复操作!"),
    REBIND_ERROR(101, "message.REBIND_ERROR", "不存在绑定关系,非法操作!"),

    // 鉴权
    CACHE_WRONG(10, "message.SESSION_DATA_WRONG", "缓存数据异常，请重新登录"),
    TOKEN_EXPIRE_TIME(11, "message.TOKEN_EXPIRE_TIME", "token过期, 请重新登录"),
    TOKEN_NON_EXISTENT(12, "message.TOKEN_NON_EXISTENT", "token不存在, 请重新登录"),
    USER_LOGIN_ERROR(13, "message.USER_LOGIN_ERROR", "用户登录失败, 请重试"),
    KEY_ERROR(14, "messag.KEY_ERROR", "密钥错误,请重新登录"),

    // 数据格式
    DATE_FORMAT_WRONG(20, "message.DATE_FORMAT_WRONG", "日期格式错误"),
    DB_DATA_WRONG(21, "message.DB_DATA_WRONG", "数据库中数据错误"),

    AI_TASK_NON_EXISTENT(22, "message.AI_TASK_NON_EXISTENT", "AI检测任务不存在!"),

    ENTITY_TRANFORM_ERROR(30, "message.ENTITY_TRANFORM_ERROR", "实体对象转换失败,请检查"),

    SCRIPT_VALID_ERROR(31, "message.", "自定义函数不符合规范"),

    // 节点解析
    PARSE_JSON_ERROR(40,"PARSE_JSON_ERROR","JSON解析失败"),

    // 文档
    WORD_ERROR(50, "WORD_ERROR", "文档解析失败"),

    // 任务管理模块
    FILE_NOT_EXIST(60, "FILE_NOT_EXIST", "文件不存在,请尝试启动任务"),
    CURRENT_TASK_RUN(61, "CURRENT_TASK_RUN", "当前任务正在运行"),

    ; // 定义结束

    // 返回码
    private int code;

    public int getCode() {
        return this.code;
    }

    // 返回消息ID
    private String messageId;

    public String getMessageId() {
        return this.messageId;
    }

    // 返回消息
    private String message;

    public String getMessage() {
        return this.message;
    }

    ExceptionCodes(int code, String messageId, String message) {
        this.code = code;
        this.messageId = messageId;
        this.message = message;
    }
}
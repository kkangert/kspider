package top.kangert.kspider.enums;

public enum TaskStateEnum {
    /**
     * 任务被创建
     */
    TASK_CREATED(1, "task_created"),

    /**
     * 任务执行中
     */
    TASK_RUNNING(2, "task_running"),

    /**
     * 任务执行错误
     */
    TASK_ERROR(3, "task_error"),

    /**
     * 任务完成
     */
    TASK_FINISHED(4, "task_finished");
   
        /**
     * 分类编号
     */
    private int typeCode = -1;

    /**
     * 备注信息
     */
    private String remark = "";

    TaskStateEnum(int typeCode, String remark) {
        this.typeCode = typeCode;
        this.remark = remark;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getRemark() {
        return remark;
    }

    public static TaskStateEnum getEnumObj(int typeCode) {
        for (TaskStateEnum item: values()) {
            if (item.getTypeCode() == typeCode) {
                return item;
            }
        }
        return null;
    }


}

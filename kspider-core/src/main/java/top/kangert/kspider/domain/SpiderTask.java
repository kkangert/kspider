package top.kangert.kspider.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.kangert.kspider.entity.BaseEntity;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 爬虫任务实体类
 */
@Table(name = "kspider_task")
@Entity
@Getter
@Setter
@ToString
public class SpiderTask extends BaseEntity {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long taskId;

    /**
     * 流程 ID
     */
    @Column(name = "flow_id")
    private Long flowId;

    /**
     * 任务名
     */
    @Column(name = "task_name")
    private String taskName;

    /**
     * 基础信息数据
     */
    @Column(name = "metadata", columnDefinition = "json COMMENT '元数据'")
    private String matedata;

    /**
     * 定时表达式
     */
    @Column(name = "cron")
    private String cron;

    /**
     * 执行次数
     */
    @Column(name = "execute_count", insertable = false)
    @ColumnDefault("0")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer executeCount;

    /**
     * 正在运行的任务数
     */
    @Transient
    private Integer runningCount;

    /**
     * 是否开启定时任务
     */
    @Column(name = "job_enabled", insertable = false)
    @ColumnDefault("false")
    private Boolean jobEnabled = false;

    /**
     * 上一次执行时间
     */
    @Column(name = "last_execute_time")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date lastExecuteTime;

    /**
     * 下一次执行时间
     */
    @Column(name = "next_execute_time")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date nextExecuteTime;

    @Column(name = "run_state")
    @ColumnDefault("1")
    private Integer runState = 1;
}

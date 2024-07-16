package top.kangert.kspider.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/***
 * 实体类基类
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @CreatedBy
    @LastModifiedBy
    @Column(name = "operation_name", columnDefinition = "varchar(8) COMMENT '操作名称'")
    public String operationName;

    @CreatedDate
    @Column(name = "create_time", updatable = false, columnDefinition = "datetime COMMENT '创建时间'")
    @ColumnDefault("CURRENT_TIMESTAMP()")
    public Date createTime = new Date();

    @LastModifiedDate
    @Column(name = "update_time", updatable = true, columnDefinition = "datetime COMMENT '更新时间'")
    @ColumnDefault("NULL ON UPDATE CURRENT_TIMESTAMP")
    public Date updateTime = null;

    @Column(name = "remark", columnDefinition = "varchar(32) COMMENT '备注'")
    private String remark;

    public String getOperationName() {
        return operationName;
    }

    public void setOperationName(String operationName) {
        this.operationName = operationName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}


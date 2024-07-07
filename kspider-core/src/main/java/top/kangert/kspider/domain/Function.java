package top.kangert.kspider.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.kangert.kspider.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 自定义函数实体类
 */
@Table(name = "kspider_function")
@Entity
@Getter
@Setter
@ToString
public class Function extends BaseEntity {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long funcId;

    /**
     * 函数名称
     */
    private String name;

    /**
     * 参数
     */
    private String parameter;

    /**
     * 函数内容
     */
    @Column(columnDefinition = "longtext")
    private String script;

}

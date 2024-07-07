package top.kangert.kspider.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import top.kangert.kspider.entity.BaseEntity;


/**
 * 全局变量实体类
 */
@Table(name = "kspider_variable")
@Entity
@Getter
@Setter
@ToString
public class Variable extends BaseEntity {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long varId;

    /**
     * 变量名称
     */
    private String name;

    /**
     * 变量值
     */
    private String val;

    /**
     * 描述
     */
    private String description;

}

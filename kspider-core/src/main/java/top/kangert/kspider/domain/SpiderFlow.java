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
 * 爬虫流程实体类
 */
@Table(name = "kspider_flow")
@Entity
@Getter
@Setter
@ToString
public class SpiderFlow extends BaseEntity {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long flowId;

    /**
     * 流程名称
     */
    private String name;

    /**
     * 流程图结构数据
     */
    @Column(columnDefinition = "longtext")
    private String json;
}

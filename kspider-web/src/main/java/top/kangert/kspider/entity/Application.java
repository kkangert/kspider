package top.kangert.kspider.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "kspider_application")
@Entity
@Getter
@Setter
@ToString
public class Application extends BaseEntity{
    /**
     * 应用ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long appId;

    /**
     * 应用名称
     */
    @Column(unique = true)
    private String appName;

    /**
     * 密钥
     */
    @Column(unique = true)
    private String appSecretKey;

    /**
     * 白名单
     */
    private String whiteList;
}

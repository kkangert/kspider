package top.kangert.kspider.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * 数据源实体类
 */
@Table(name = "kspider_database")
@Entity
@Getter
@Setter
@ToString
public class DataSource {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false)
    private Long id;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 驱动类
     */
    @Column(name = "driver_class_name")
    private String driverClassName;

    /**
     * JDBC URL
     */
    @Column(name = "jdbc_url")
    private String jdbcUrl;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    @Column(name = "create_time", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date createTime;
}

package top.kangert.kspider.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Getter
@Setter
@ToString
public class DataSourceDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源的名称不能为空", groups = Save.class)
    private String name;

    /**
     * 驱动类
     */
    @NotBlank(message = "驱动类的全限定名称不能为空", groups = {Test.class, Save.class})
    private String driverClassName;

    /**
     * JDBC URL
     */
    @NotBlank(message = "数据库的连接地址不能为空", groups = {Test.class, Save.class})
    private String jdbcUrl;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;


    public interface Test {
    }

    public interface Save {
    }
}

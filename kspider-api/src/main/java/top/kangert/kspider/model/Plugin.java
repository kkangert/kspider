package top.kangert.kspider.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 插件对象
 */
@Getter
@Setter
@AllArgsConstructor
public class Plugin {

    /**
     * 插件名称
     */
    private String name;

    /**
     * 插件模板页面路径（后续改为Vue）
     */
    private String url;
}

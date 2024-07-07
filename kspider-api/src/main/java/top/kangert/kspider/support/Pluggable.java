package top.kangert.kspider.support;

import top.kangert.kspider.model.Plugin;

/**
 * 插件扩展API
 */
public interface Pluggable {

    /**
     * 插件注册
     * 
     * @return 插件对象
     */
    Plugin register();

}

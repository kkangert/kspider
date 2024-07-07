package top.kangert.kspider.executor;

/**
 * Java类型对象扩展
 */
public interface FunctionExtension {

    /***
     * 扩展Java内置类型对象
     * @return  Java类型对象
     */
    Class<?> support();
}

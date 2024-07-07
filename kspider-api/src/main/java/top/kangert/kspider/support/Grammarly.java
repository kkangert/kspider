package top.kangert.kspider.support;

import java.util.List;

import top.kangert.kspider.model.Grammar;

/**
 * 前端语法提示接口
 */
public interface Grammarly {

    /**
     * 语法提示对象注册
     * @return 语法提示对象列表
     */
    List<Grammar> grammars();

}

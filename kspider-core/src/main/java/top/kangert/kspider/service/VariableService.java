package top.kangert.kspider.service;

import top.kangert.kspider.domain.Variable;
import top.kangert.kspider.util.PageInfo;

import java.util.Map;

public interface VariableService {

    /**
     * 新增一个全局变量
     * 
     * @param params 参数
     * @return 全局变量实体
     */
    Variable addItem(Map<String, Object> params);

    /**
     * 删除一个全局变量
     * 
     * @param params 参数
     */
    void deleteItem(Map<String, Object> params);

    /**
     * 编辑一个全局变量
     * 
     * @param params 参数
     */
    void editItem(Map<String, Object> params);

    /**
     * 查询一个全局变量
     * 
     * @param varId 变量ID
     * @return 全局变量实体
     */
    Variable queryItem(Long varId);

    /**
     * 按照条件查询全局变量
     * 
     * @param params 查询条件
     * @return 全局变量列表(分页)
     */
    PageInfo<Variable> queryItems(Map<String, Object> params);

}

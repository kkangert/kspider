package top.kangert.kspider.service;

import top.kangert.kspider.domain.Function;
import top.kangert.kspider.util.PageInfo;

import java.util.Map;

public interface FunctionService {

    /**
     * 创建全局自定义函数
     * 
     * @param function 函数对象
     * @return 函数对象
     */
    Function addItem(Map<String, Object> params);

    /**
     * 删除自定义全局函数
     * 
     * @param id 函数对象
     */
    void deleteItem(Map<String, Object> params);

    /**
     * 编辑函数对象
     * 
     * @param params 函数对象参数
     */
    void editItem(Map<String, Object> params);

    /**
     * 查询函数对象
     * 
     * @param id 函数ID
     * @return 函数对象
     */
    Function queryItem(Long id);

    /**
     * 按照条件查询函数
     * 
     * @param params 查询条件
     * @return 函数列表(分页)
     */
    PageInfo<Function> queryItems(Map<String, Object> params);

}

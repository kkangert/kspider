package top.kangert.kspider.service;

import top.kangert.kspider.domain.SpiderFlow;
import top.kangert.kspider.io.Line;
import top.kangert.kspider.util.PageInfo;

import java.util.List;
import java.util.Map;

public interface SpiderFlowService {
    /**
     * 查询爬虫列表
     * 
     * @param params
     * @return
     */
    PageInfo<SpiderFlow> queryItems(Map<String, Object> params);

    /**
     * 查询爬虫列表
     * 
     * @param params
     * @return
     */
    SpiderFlow queryItem(Long id);

    /**
     * 创建爬虫流程
     * 
     * @param params 参数
     * @return
     */
    SpiderFlow addItem(Map<String, Object> params);

    /**
     * 修改爬虫流程
     * 
     * @param params 参数
     * @return
     */
    void editItem(Map<String, Object> params);

    /**
     * 删除爬虫
     * 
     * @param params 参数(flowId必填)
     */
    void deleteItem(Map<String, Object> params);

    void removeById(Long id);

    SpiderFlow getById(Long id);

    List<SpiderFlow> findOtherFlows(Long id);

    List<String> getRecentTriggerTime(String cron, int numTimes);

    List<Line> log(Long id, Long taskId, String keywords, Long index, Integer count, Boolean reversed,
            Boolean matchCase, Boolean regex);

}

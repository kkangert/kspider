package top.kangert.kspider.service;

import top.kangert.kspider.domain.SpiderTask;
import top.kangert.kspider.util.PageInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

public interface SpiderTaskService {
    /**
     * 删除任务
     * 
     * @param params
     */
    void deleteItem(Map<String, Object> params);

    /**
     * 保存任务
     * 
     * @param params
     * @return
     */
    SpiderTask add(Map<String, Object> params);

    Long getMaxTaskIdByFlowId(Long flowId);

    /**
     * 通过流程Id获取运行任务数
     * 
     * @param params
     * @return
     */
    Integer getRunningCountByFlowId(Map<String, Object> params);

    /**
     * 查询任务
     * 
     * @param params
     * @return
     */
    PageInfo<SpiderTask> queryItems(Map<String, Object> params);

    /**
     * 查询任务
     * 
     * @param taskId
     * @return
     */
    SpiderTask queryItem(Long taskId);

    /**
     * 修改任务
     * 
     * @param params
     */
    void edit(Map<String, Object> params);

    /**
     * 执行次数自增
     * 
     * @param id              任务Id
     * @param lastExecuteTime 上次执行时间
     * @param nextExecuteTime 下次执行时间
     */
    void executeCountIncrement(Long id, Date lastExecuteTime, Date nextExecuteTime);

    /**
     * 查找定时任务
     * 
     * @param jobEnabled
     * @return
     */
    List<SpiderTask> findByJobEnabled(Boolean jobEnabled);

    /**
     * 更新下次执行时间
     * 
     * @param task 任务
     */
    void updateNextExecuteTime(SpiderTask task);

    /**
     * 
     * @param taskId
     * @param cron
     */
    void updateCronAndNextExecuteTime(Long taskId, String cron);

    /**
     * 清除下次执行时间
     */
    void clearNextExecuteTime();

    /**
     * 运行任务
     * 
     * @param taskId 任务Id
     */
    void run(Map<String, Object> params);

    /**
     * 停止任务
     * 
     * @param taskId 任务Id
     */
    void stop(Map<String, Object> params);

    /**
     *  下载文件
     * @param taskId 任务Id
     */
    void download(Map<String, Object> params, HttpServletResponse response);
}

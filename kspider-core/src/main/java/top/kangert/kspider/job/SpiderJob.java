package top.kangert.kspider.job;

import top.kangert.kspider.KspiderRuntime;
import top.kangert.kspider.config.SpiderConfig;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.context.SpiderContextHolder;
import top.kangert.kspider.context.SpiderJobContext;
import top.kangert.kspider.dao.SpiderTaskRepository;
import top.kangert.kspider.domain.SpiderFlow;
import top.kangert.kspider.domain.SpiderTask;
import top.kangert.kspider.enums.TaskStateEnum;
import top.kangert.kspider.service.SpiderFlowService;
import top.kangert.kspider.service.SpiderTaskService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;

import javax.annotation.Resource;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Spring Quartz定时任务
 */
@Component
@Slf4j
public class SpiderJob extends QuartzJobBean {

    private KspiderRuntime spider;

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private SpiderTaskService spiderTaskService;

    @Resource
    private SpiderFlowService spiderFlowService;

    @Resource
    private SpiderTaskRepository spiderTaskRepository;

    @Autowired
    public void setSpider(KspiderRuntime spider) {
        this.spider = spider;
    }

    private static Map<Long, SpiderContext> contextMap = new ConcurrentHashMap<>();

    @Override
    protected void executeInternal(JobExecutionContext context) {
        if (spiderConfig.getJobEnabled()) {
            JobDataMap jobDataMap = context.getMergedJobDataMap();
            SpiderTask spiderTask = (SpiderTask) jobDataMap.get(Constants.QUARTZ_KSPIDER_FLOW_PARAM_NAME);
            run(spiderTask, context.getNextFireTime());
        }
    }

    /**
     * 执行流程
     *
     * @param taskId 任务 ID
     */
    public void run(Long taskId) {
        run(spiderTaskService.queryItem(taskId), null);
    }

    /**
     * 执行流程
     *
     * @param task     任务
     * @param nextTime 下一次执行的时间
     */
    private void run(SpiderTask task, Date nextTime) {
        // 当前时间
        Date now = new Date();
        // 获取当前任务流程
        SpiderFlow flow = spiderFlowService.queryItem(task.getFlowId());
        task.setRunState(TaskStateEnum.TASK_RUNNING.getTypeCode());
        spiderTaskRepository.save(task);
        // 创建执行上下文
        SpiderJobContext context = null;
        try {
            context = SpiderJobContext.create(spiderConfig.getWorkspace(), flow.getFlowId(), task.getTaskId(), false);
            SpiderContextHolder.set(context);
            log.info("流程：{} 开始执行，任务 ID 为：{}", flow.getName(), task.getTaskId());
            contextMap.put(task.getTaskId(), context);
            // 将matedata导入变量
            Map<String, Object> variables = JSONUtil.toBean(task.getMatedata(),
                    new TypeReference<Map<String, Object>>() {
                    }, false);
            spider.run(flow, context, variables);
            log.info("流程：{} 执行完毕，任务 ID 为：{}，下次执行时间：{}", flow.getName(), task.getTaskId(),
                    DateUtil.format(nextTime, "yyyy-MM-dd HH:mm:ss"));
        } catch (FileNotFoundException e) {
            log.error("创建日志文件失败", e);
        } catch (Throwable t) {
            log.error("流程：{} 执行出错，任务 ID 为：{}", flow.getName(), task.getTaskId());
        } finally {
            // 关闭流
            if (context != null) {
                context.close();
            }
            contextMap.remove(task.getTaskId());
            SpiderContextHolder.remove();
            // 更新状态
            SpiderTask currentTask = spiderTaskRepository.findById(task.getTaskId()).get();
            System.out.println(task.getRunState());
            if (currentTask.getRunState() == TaskStateEnum.TASK_RUNNING.getTypeCode()) {
                currentTask.setRunState(TaskStateEnum.TASK_FINISHED.getTypeCode());
                spiderTaskRepository.save(currentTask);
            }
        }
        spiderTaskService.executeCountIncrement(task.getTaskId(), now, nextTime);
    }

    /**
     * 获取执行上下文
     *
     * @param taskId 任务 ID
     * @return 执行上下文
     */
    public static SpiderContext getSpiderContext(Long taskId) {
        return contextMap.get(taskId);
    }
}

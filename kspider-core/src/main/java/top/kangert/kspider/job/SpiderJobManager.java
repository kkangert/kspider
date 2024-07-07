package top.kangert.kspider.job;

import top.kangert.kspider.KspiderRuntime;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.domain.SpiderTask;
import lombok.extern.slf4j.Slf4j;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 爬虫定时任务管理器
 */
@Component
@Slf4j
public class SpiderJobManager {

    private KspiderRuntime spider;

    private SpiderJob spiderJob;

    @Resource
    private Scheduler quartzScheduler;

    @Autowired
    public void setSpider(KspiderRuntime spider) {
        this.spider = spider;
    }

    @Autowired
    public void setSpiderJob(SpiderJob spiderJob) {
        this.spiderJob = spiderJob;
    }

    /**
     * 创建定时任务
     *
     * @param flow 流程
     * @return 下次执行时间
     */
    public Date addJob(SpiderTask task) {
        try {
            // 构建任务
            JobDetail job = JobBuilder.newJob(SpiderJob.class).withIdentity(getJobKey(task.getFlowId())).build();
            job.getJobDataMap().put(Constants.QUARTZ_KSPIDER_FLOW_PARAM_NAME, task);
            // 设置触发时间
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron())
                    .withMisfireHandlingInstructionDoNothing();
            // 创建触发器
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey(task.getFlowId())).withSchedule(cronScheduleBuilder).build();

            return quartzScheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            log.error("创建定时任务出错", e);
            return null;
        }
    }

    /**
     * 直接运行
     *
     * @param flowId 流程 ID
     */
    public void run(Long taskId) {
        spider.getThreadPool().submit(() -> spiderJob.run(taskId));
    }

    /**
     * 删除定时任务
     *
     * @param flowId 流程 ID
     * @return 是否成功
     */
    public boolean removeJob(Long flowId) {
        try {
            quartzScheduler.deleteJob(getJobKey(flowId));
            return true;
        } catch (SchedulerException e) {
            log.error("删除定时任务失败", e);
            return false;
        }
    }

    /**
     * 获取 JobKey
     *
     * @param flowId 流程 ID
     * @return JobKey
     */
    private JobKey getJobKey(Long flowId) {
        return JobKey.jobKey(Constants.QUARTZ_JOB_NAME_PREFIX + flowId);
    }

    /**
     * 获取 TriggerKey
     *
     * @param flowId 流程 ID
     * @return TriggerKey
     */
    private TriggerKey getTriggerKey(Long flowId) {
        return TriggerKey.triggerKey(Constants.QUARTZ_JOB_NAME_PREFIX + flowId);
    }
}

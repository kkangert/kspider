package top.kangert.kspider.service.impl;

import cn.hutool.core.convert.Convert;
import top.kangert.kspider.config.SpiderConfig;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.dao.SpiderFlowRepository;
import top.kangert.kspider.domain.SpiderFlow;
import top.kangert.kspider.exception.BaseException;
import top.kangert.kspider.exception.ExceptionCodes;
import top.kangert.kspider.io.Line;
import top.kangert.kspider.io.RandomAccessFileReader;
import top.kangert.kspider.job.SpiderJobManager;
import top.kangert.kspider.service.BaseService;
import top.kangert.kspider.service.SpiderFlowService;
import top.kangert.kspider.service.SpiderTaskService;
import top.kangert.kspider.util.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerUtils;
import org.quartz.spi.OperableTrigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import cn.hutool.core.date.DateUtil;
import javax.annotation.Resource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class SpiderFlowServiceImpl extends BaseService implements SpiderFlowService {

    @Resource
    private SpiderConfig spiderConfig;

    @Resource
    private SpiderJobManager spiderJobManager;

    @Resource
    private SpiderTaskService spiderTaskService;

    @Resource
    private SpiderFlowRepository spiderFlowRepository;

    @Autowired
    @SuppressWarnings("all")
    private PlatformTransactionManager txManager;

    @Override
    public SpiderFlow getById(Long id) {
        return spiderFlowRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpiderFlow addItem(Map<String, Object> params) {
        // 参数检查
        checkParams(params, null);

        // 实体转换
        SpiderFlow spiderFlow = transformEntity(params, SpiderFlow.class);

        // 新增对象
        SpiderFlow flow = spiderFlowRepository.save(spiderFlow);

        return flow;
    }

    @Override
    public void editItem(Map<String, Object> params) {
        // 参数检查
        checkParams(params, new String[] { "flowId" });

        // 获取爬虫ID
        Long flowId = Convert.toLong(params.get("flowId"));

        // 实体查询
        SpiderFlow flow = queryItem(flowId);

        if (flow == null) {
            throw new BaseException(ExceptionCodes.DB_DATA_WRONG);
        }

        // 对象值拷贝
        copyProperties(params, flow);

        spiderFlowRepository.save(flow);
    }

    @Override
    public void deleteItem(Map<String, Object> params) {
        // 参数检查
        checkParams(params, new String[] { "flowId" });

        // 获取ID
        Long flowId = Convert.toLong(params.get("flowId"));

        // 删除定时器
        if (spiderJobManager.removeJob(flowId)) {
            // 删除SpiderFlow
            spiderFlowRepository.deleteById(flowId);
        }

    }

    @Override
    public SpiderFlow queryItem(Long id) {
        if (id == null) {
            throw new BaseException(ExceptionCodes.ARGUMENTS_ERROR);
        }
        SpiderFlow spiderFlow = spiderFlowRepository.findSpiderFlowByFlowId(id);
        return spiderFlow;
    }

    @Override
    public PageInfo<SpiderFlow> queryItems(Map<String, Object> params) {
        // 参数检查
        checkParams(params, null);

        // 获取分页配置
        Pageable pageable = processPage(params);

        // 获取分页信息
        Page<SpiderFlow> pageInfo = spiderFlowRepository.findAll(multipleConditionsBuilder(params), pageable);

        return new PageInfo<SpiderFlow>(pageInfo);
    }

    @Override
    public List<SpiderFlow> findOtherFlows(Long id) {
        return spiderFlowRepository.findByFlowIdNot(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        spiderFlowRepository.deleteById(id);
    }

    @Override
    public List<String> getRecentTriggerTime(String cron, int numTimes) {
        List<String> list = new ArrayList<>();
        CronTrigger trigger;
        try {
            trigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        } catch (Exception e) {
            throw new RuntimeException("cron 表达式 " + cron + " 有误");
        }
        List<Date> dates = TriggerUtils.computeFireTimes((OperableTrigger) trigger, null, numTimes);
        for (Date date : dates) {
            list.add(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
        }
        return list;
    }

    @Override
    public List<Line> log(Long id, Long taskId, String keywords, Long index, Integer count, Boolean reversed,
            Boolean matchCase, Boolean regex) {

        if (Objects.isNull(taskId)) {
            Long maxId = spiderTaskService.getMaxTaskIdByFlowId(id);
            if (Objects.isNull(maxId)) {
                throw new RuntimeException("该流程没有运行过的任务");
            } else {
                taskId = maxId;
            }
        }

        List<Line> lines;
        String flowFolder = Constants.KSPIDER_FLOW_LOG_DIR_PREFIX + id;
        String taskFolder = Constants.KSPIDER_TASK_LOG_DIR_PREFIX + taskId;
        File logFile = new File(new File(spiderConfig.getWorkspace()),
                "logs" + File.separator + flowFolder + File.separator + "logs" + File.separator + taskFolder + ".log");

        try (RandomAccessFileReader reader = new RandomAccessFileReader(new RandomAccessFile(logFile, "r"),
                index == null ? -1 : index, reversed == null || reversed)) {
            lines = reader.readLine(count == null ? 10 : count, keywords, matchCase != null && matchCase,
                    regex != null && regex);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("日志文件不存在", e);
        } catch (IOException e) {
            throw new RuntimeException("读取日志文件出错", e);
        }
        return lines;
    }
}

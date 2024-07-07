package top.kangert.kspider.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import top.kangert.kspider.domain.SpiderTask;

import java.util.Date;
import java.util.List;

public interface SpiderTaskRepository extends JpaRepository<SpiderTask, Long>, JpaSpecificationExecutor<SpiderTask> {

    @Query(value = "select count(taskId) from kspider_task where flowId = ?1 and update_time != null order by desc", nativeQuery = true)
    Integer countByFlowIdAndupdateTimeIsNull(Long flowId);

    List<SpiderTask> findByJobEnabled(Boolean jobEnabled);

    @Modifying
    @Query(value = "update SpiderTask st set st.nextExecuteTime = null")
    void clearNextExecuteTime();

    @Modifying
    @Query(value = "update SpiderTask st set st.cron = :cron, st.nextExecuteTime = :nextExecuteTime where st.taskId = :taskId")
    void updateCronAndNextExecuteTime(@Param("taskId") Long taskId, @Param("cron") String cron,
            @Param("nextExecuteTime") Date fireTimeAfter);

    @Query(value = "select taskId from SpiderTask where flowId = :flowId order by updateTime desc")
    List<Long> findTaskIdByFlowIdOrderByupdateTimeDesc(@Param("flowId") Long flowId);

    @Modifying
    @Query(value = "update kspider_task set execute_count = execute_count + 1, last_execute_time = :lastExecuteTime where task_id = :taskId", nativeQuery = true)
    void executeCountIncrement(@Param("lastExecuteTime") Date lastExecuteTime, @Param("taskId") Long taskId);

    @Modifying
    @Query(value = "update kspider_task set execute_count = execute_count + 1, last_execute_time = ?, next_execute_time = ? where task_id = ?", nativeQuery = true)
    void executeCountIncrementAndExecuteNextTime(Date lastExecuteTime, Date nextExecuteTime, Long taskId);

    @Modifying
    @Query(value = "update SpiderTask set nextExecuteTime = :nextExecuteTime where taskId = :taskId")
    void updateNextExecuteTime(@Param("nextExecuteTime") Date nextExecuteTime, @Param("taskId") Long taskId);

    @Modifying
    @Query(value = "update kspider_task set job_enabled = :enabled where task_id = :taskId", nativeQuery = true)
    void updateJobEnabled(@Param("taskId") Long taskId, @Param("enabled") boolean enabled);
}

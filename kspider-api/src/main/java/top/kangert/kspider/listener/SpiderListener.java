package top.kangert.kspider.listener;

import top.kangert.kspider.context.SpiderContext;

/**
 * 任务执行监听器
 */
public interface SpiderListener {

    /**
     * 在任务开始之前
     *
     * @param context 执行上下文
     */
    void beforeStart(SpiderContext context);

    /**
     * 在任务结束之后
     *
     * @param context 执行上下文
     */
    void afterEnd(SpiderContext context);
}

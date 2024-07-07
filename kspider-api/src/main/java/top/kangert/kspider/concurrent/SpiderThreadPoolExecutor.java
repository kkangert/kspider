package top.kangert.kspider.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import top.kangert.kspider.constant.Constants;

/**
 * 线程池
 */
public class SpiderThreadPoolExecutor {

    /**
     * 线程池最大线程数
     */
    private int maxThreads;

    /**
     * 线程存活时间，单位毫秒
     */
    private final long keepAliveTime = 10;

    /**
     * 真正执行任务的线程池
     */
    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * 线程编号，从 1 开始
     */
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * 线程名前缀
     */
    private final String KSPIDER_THREAD_NAME_PREFIX = Constants.KSPIDER_THREAD_NAME_PREFIX;

    /**
     * 线程组
     */
    private static final ThreadGroup KSPIDER_THREAD_GROUP = new ThreadGroup(Constants.KSPIDER_THREAD_GROUP_NAME);

    /**
     * 线程池构造器
     *
     * @param maxThreads 最大线程数
     */
    public SpiderThreadPoolExecutor(int maxThreads) {
        this.maxThreads = maxThreads;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                keepAliveTime,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(),
                // 重写线程组和线程名
                r -> new Thread(KSPIDER_THREAD_GROUP, r, KSPIDER_THREAD_NAME_PREFIX + threadNumber.getAndIncrement()));
    }

    /**
     * 直接提交任务到父线程池
     *
     * @param runnable 任务
     * @return 任务
     */
    public Future<?> submit(Runnable runnable) {
        return threadPoolExecutor.submit(runnable);
    }

    /**
     * 创建子线程池
     *
     * @param threads 子线程池的最大线程数
     * @return 子线程池
     */
    public SubThreadPoolExecutor createSubThreadPoolExecutor(int threads) {
        return new SubThreadPoolExecutor(Math.min(this.maxThreads, threads));
    }

    /**
     * 子线程池
     */
    public class SubThreadPoolExecutor {

        /**
         * 子线程池最大线程数量
         */
        private int threads;

        /**
         * 正在执行中的任务（已经提交到线程池中的任务）列表，它的作用是约束子线程池的线程数量
         */
        private Future<?>[] tasks;

        /**
         * 等待任务完成的超时时间
         */
        private final long waitTaskTimeout = 10;

        /**
         * 子线程池是否正在运行中
         */
        private volatile boolean running = true;

        /**
         * 提交线程是否正在运行中
         */
        private volatile boolean submitting = false;

        /**
         * 正在执行中的任务数量
         */
        private AtomicInteger executingTaskNumber = new AtomicInteger(0);

        /**
         * 候选任务队列
         */
        private LinkedBlockingQueue<FutureTask<?>> candidateTaskQueue;

        /**
         * 子线程池构造器
         *
         * @param threads 子线程池大小
         */
        public SubThreadPoolExecutor(int threads) {
            this.threads = threads;
            this.tasks = new Future[threads];
            this.candidateTaskQueue = new LinkedBlockingQueue<>();
        }

        /**
         * 获取当前能够提交任务的槽位
         *
         * @return -1 表示子线程池中所有的线程都正在执行任务，暂时没有空闲的线程提供
         */
        private int index() {
            for (int i = 0; i < threads; i++) {
                if (tasks[i] == null || tasks[i].isDone()) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 删除已经完成的任务
         */
        private void removeDoneFuture() {
            for (int i = 0; i < threads; i++) {
                try {
                    // 尝试等待任务完成
                    if (tasks[i] != null && tasks[i].get(waitTaskTimeout, TimeUnit.MILLISECONDS) == null) {
                        tasks[i] = null;
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        /**
         * 等待空闲的线程
         */
        private void await() {
            while (index() == -1) {
                removeDoneFuture();
            }
        }

        /**
         * 等待所有线程执行完毕
         */
        public void awaitTermination() {
            // 等待所有的线程执行完毕
            // 需要注意：在子线程池中有一个线程是调度线程，该线程会在所有的任务流程结束后才停止运行
            while (executingTaskNumber.get() > 1) {
                removeDoneFuture();
            }
            running = false;
            // 唤醒提交线程，使提交线程结束运行
            synchronized (candidateTaskQueue) {
                candidateTaskQueue.notifyAll();
            }
        }

        /**
         * 异步提交任务
         *
         * @param runnable 任务
         * @param value    任务返回值
         * @param <T>      返回值类型
         * @return 任务
         */
        public <T> Future<T> submitAsync(Runnable runnable, T value) {
            FutureTask<T> futureTask = new FutureTask<>(() -> {
                try {
                    // 正在执行的任务数量 +1
                    executingTaskNumber.incrementAndGet();
                    // 执行任务
                    runnable.run();
                } finally {
                    // 正在执行的任务数量 -1
                    executingTaskNumber.decrementAndGet();
                }
            }, value);
            // 将任务添加到候选任务队列中
            candidateTaskQueue.add(futureTask);
            // 第一次调用时，提交线程还没有启动，启动提交线程
            if (!submitting) {
                submitting = true;
                CompletableFuture.runAsync(this::submit);
            }
            // 通知继续从候选任务队列中获取任务提交到线程池中
            synchronized (candidateTaskQueue) {
                candidateTaskQueue.notifyAll();
            }
            return futureTask;
        }

        /**
         * 提交任务到线程池中
         */
        private void submit() {
            while (running) {
                synchronized (candidateTaskQueue) {
                    try {
                        // 如果候选任务队列为空，则等待添加
                        if (candidateTaskQueue.isEmpty()) {
                            candidateTaskQueue.wait();
                        }
                        // 当提交线程被唤醒后，从候选任务队列中获取所有的任务提交到线程池中
                        while (!candidateTaskQueue.isEmpty()) {
                            FutureTask<?> futureTask = candidateTaskQueue.remove();
                            // 等待有空闲线程
                            await();
                            // 需要注意：使用这种方式提交的任务返回值始终为 null
                            tasks[index()] = threadPoolExecutor.submit(futureTask);
                        }
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }
}

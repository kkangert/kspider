package top.kangert.kspider.executor.function;

import org.springframework.stereotype.Component;

import top.kangert.kspider.annotation.Comment;
import top.kangert.kspider.annotation.Example;
import top.kangert.kspider.executor.FunctionExecutor;

@Component
@Comment("thread 常用方法")
public class ThreadFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "thread";
    }

    @Comment("线程休眠")
    @Example("${thread.sleep(1000L)}")
    public static void sleep(Long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package top.kangert.kspider.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import top.kangert.kspider.constant.Constants;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.model.SpiderOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.io.IoUtil;

/**
 * 爬虫工作进程上下文
 */
@Slf4j
public class SpiderJobContext extends SpiderContext {

    @Getter
    private OutputStream outputStream;

    private List<SpiderOutput> outputs = new ArrayList<>();

    private boolean allowOutput;

    public SpiderJobContext(OutputStream outputStream, boolean allowOutput) {
        this.outputStream = outputStream;
        this.allowOutput = allowOutput;
    }

    @Override
    public void addOutput(SpiderOutput output) {
        if (this.allowOutput) {
            synchronized (this.outputs) {
                this.outputs.add(output);
            }
        }
    }

    @Override
    public List<SpiderOutput> getOutputs() {
        return outputs;
    }

    public void close() {
        IoUtil.close(this.outputStream);
    }

    /**
     * 创建执行上下文
     *
     * @param workspace   工作目录
     * @param flowId      流程 ID
     * @param taskId      任务 ID
     * @param allowOutput 是否允许输出结果
     * @return 执行上下文
     * @throws FileNotFoundException 文件不存在
     */
    public static SpiderJobContext create(String workspace, Long flowId, Long taskId,
            boolean allowOutput) throws FileNotFoundException {
        String flowFolder = Constants.KSPIDER_FLOW_LOG_DIR_PREFIX + flowId;
        String taskFolder = Constants.KSPIDER_TASK_LOG_DIR_PREFIX + taskId;
        File file = new File(new File(workspace),
                "logs" + File.separator + flowFolder + File.separator + "logs" + File.separator + taskFolder + ".log");
        File dirFile = file.getParentFile();
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        OutputStream os = new FileOutputStream(file, true);
        SpiderJobContext context = new SpiderJobContext(os, allowOutput);
        context.setFlowId(flowId);
        return context;
    }
}

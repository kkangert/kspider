package top.kangert.kspider.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import ch.qos.logback.core.spi.DeferredProcessingAware;
import ch.qos.logback.core.status.ErrorStatus;
import top.kangert.kspider.context.SpiderContext;
import top.kangert.kspider.context.SpiderContextHolder;
import top.kangert.kspider.context.SpiderJobContext;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class SpiderFlowFileAppender extends RollingFileAppender<ILoggingEvent> {

    @Override
    protected void subAppend(ILoggingEvent event) {

        TriggeringPolicy<ILoggingEvent> triggeringPolicy = this.getTriggeringPolicy();

        synchronized (triggeringPolicy) {
            if (triggeringPolicy.isTriggeringEvent(new File(this.getFile()), event)) {
                this.rollover();
            }
        }

        if (this.isStarted()) {
            // 将日志的文件流设置为执行上下文中的流
            SpiderContext context = SpiderContextHolder.get();
            OutputStream out = this.getOutputStream();
            if (context != null) {
                if (context instanceof SpiderJobContext) {
                    SpiderJobContext jobContext = (SpiderJobContext) context;
                    out = jobContext.getOutputStream();
                }
            }
            try {
                if (event instanceof DeferredProcessingAware) {
                    ((DeferredProcessingAware) event).prepareForDeferredProcessing();
                }
                byte[] byteArray = this.encoder.encode(event);
                this.writeBytes(out, byteArray);
            } catch (IOException e) {
                this.started = false;
                this.addStatus(new ErrorStatus("IO failure in appender", this, e));
            }

        }
    }

    private void writeBytes(OutputStream out, byte[] byteArray) throws IOException {
        if (byteArray != null && byteArray.length != 0) {
            this.lock.lock();
            try {
                out.write(byteArray);
                if (this.isImmediateFlush()) {
                    out.flush();
                }
            } finally {
                this.lock.unlock();
            }
        }
    }

}

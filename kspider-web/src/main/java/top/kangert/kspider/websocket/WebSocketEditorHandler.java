package top.kangert.kspider.websocket;

import top.kangert.kspider.KspiderRuntime;
import top.kangert.kspider.context.SpiderWebSocketContext;
import top.kangert.kspider.util.SpiderFlowUtils;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.concurrent.CompletableFuture;

public class WebSocketEditorHandler extends TextWebSocketHandler {

    /**
     * 需要特殊的注入方式
     */
    public KspiderRuntime spider;

    /**
     * WebSocket 执行上下文
     */
    private SpiderWebSocketContext context;

    public WebSocketEditorHandler(KspiderRuntime spider) {
        this.spider = spider;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // 将客户端发送的消息转换成 json 格式
        JSONObject event = JSONUtil.parseObj(message.getPayload());
        // 获取事件类型
        String eventType = event.getStr("eventType");
        // 是 debug 类型的事件吗
        boolean isDebug = WebSocketEvent.DEBUG_EVENT_TYPE.equalsIgnoreCase(eventType);
        // test 类型事件或 debug 类型事件
        if (WebSocketEvent.TEST_EVENT_TYPE.equalsIgnoreCase(eventType) || isDebug) {
            // 创建 WebSocket 通信时使用的爬虫上下文
            context = new SpiderWebSocketContext(session);
            context.setDebug(isDebug);
            context.setRunning(true);
            // 异步执行
            CompletableFuture.runAsync(() -> {
                String json = event.getStr("message");
                if (json != null) {
                    // 执行测试
                    try {
                        spider.runWithTest(SpiderFlowUtils.parseJsonToSpiderNode(json), context);
                    } catch (Exception e) {
                        context.write(new WebSocketEvent<>(WebSocketEvent.ERROR_EVENT_TYPE, e.getMessage()));
                    }
                    // 发送完成消息
                    context.write(new WebSocketEvent<>(WebSocketEvent.FINISH_EVENT_TYPE, null));
                } else {
                    // 发送出错消息
                    context.write(new WebSocketEvent<>(WebSocketEvent.ERROR_EVENT_TYPE, "节点信息无效"));
                }
                context.setRunning(false);
            });
        }
        // stop 事件，结束运行
        else if (WebSocketEvent.STOP_EVENT_TYPE.equals(eventType) && context != null) {
            context.setRunning(false);
            context.stop();
        }
        // resume 事件，唤醒
        else if (WebSocketEvent.RESUME_EVENT_TYPE.equalsIgnoreCase(eventType) && context != null) {
            context.resume();
        }
    }

}

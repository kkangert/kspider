package top.kangert.kspider.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import cn.hutool.core.util.StrUtil;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class WebSocketEditorInterceptor implements HandshakeInterceptor {

    private static final String TOKEN_PARAMETER = "token";

    @Autowired
    private HttpServletRequest request;

    public WebSocketEditorInterceptor() {
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse resp, WebSocketHandler handler,
            Map<String, Object> attributes) throws Exception {
        String sessionToken = (String) request.getSession().getAttribute(TOKEN_PARAMETER);

        if (req instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) req;
            String token = serverRequest.getServletRequest().getParameter(TOKEN_PARAMETER);
            if (StrUtil.isBlank(token)) {
                return false;
            } else if (StrUtil.equals(sessionToken, token)) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest req, ServerHttpResponse resp, WebSocketHandler handler, Exception e) {

    }
}

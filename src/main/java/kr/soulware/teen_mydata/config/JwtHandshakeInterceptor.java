package kr.soulware.teen_mydata.config;

import kr.soulware.teen_mydata.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
        // 쿼리 파라미터에서 token 추출
        String query = request.getURI().getQuery();
        String token = null;
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring("token=".length());
                    break;
                }
            }
        }
        if (token != null && jwtService.validateToken(token)) {
            attributes.put("email", jwtService.getEmail(token));
            attributes.put("role", jwtService.getRole(token));
            log.info("WebSocket 인증 성공: {}", jwtService.getEmail(token));
            return true;
        } else {
            log.warn("WebSocket JWT 인증 실패: 쿼리 파라미터 없음/불일치/만료");
        }
        return false; // 인증 실패 시 연결 거부
    }

    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception
    ) {
    }
} 
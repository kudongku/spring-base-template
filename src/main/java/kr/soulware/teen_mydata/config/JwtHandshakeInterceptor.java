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

/**
 * WebSocket 연결 시 JWT 토큰을 검증하는 핸드셰이크 인터셉터
 * 이 클래스는 WebSocket 연결이 설정되기 전에 JWT 토큰의 유효성을 검사하여
 * 인증된 사용자만 WebSocket 연결을 허용하도록 합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    /**
     * WebSocket 핸드셰이크 전에 실행되는 메서드
     * 이 메서드는 WebSocket 연결이 설정되기 전에 호출되며,
     * 클라이언트가 전송한 JWT 토큰을 검증하여 인증된 사용자인지 확인합니다.
     *
     * @param request    HTTP 요청 객체 (WebSocket 연결 요청 정보 포함)
     * @param response   HTTP 응답 객체
     * @param wsHandler  WebSocket 핸들러
     * @param attributes WebSocket 세션에 저장될 속성들 (인증 정보 포함)
     * @return true: 인증 성공 시 연결 허용, false: 인증 실패 시 연결 거부
     */
    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
        // 1단계: URL 쿼리 파라미터에서 JWT 토큰 추출
        String query = request.getURI().getQuery();
        String token = null;

        // 쿼리 파라미터가 존재하는 경우에만 토큰 추출 시도
        if (query != null) {
            // 쿼리 파라미터를 '&'로 분리하여 각 파라미터 검사
            for (String param : query.split("&")) {
                // "token="으로 시작하는 파라미터를 찾아 토큰 값 추출
                if (param.startsWith("token=")) {
                    token = param.substring("token=".length());
                    break;
                }
            }
        }

        // 2단계: 토큰 유효성 검증 및 사용자 정보 추출
        if (token != null && jwtService.validateToken(token)) {
            // 토큰이 유효한 경우, 사용자 정보를 WebSocket 세션 속성에 저장
            attributes.put("email", jwtService.getEmail(token));
            attributes.put("role", jwtService.getRole(token));
            log.info("WebSocket 인증 성공: {}", jwtService.getEmail(token));
            return true;
        } else {
            // 인증 실패 시 경고 로그 기록
            log.warn("WebSocket JWT 인증 실패: 쿼리 파라미터 없음/불일치/만료");
        }

        return false;  // 인증 실패 시 연결 거부
    }

    /**
     * WebSocket 핸드셰이크 후에 실행되는 메서드
     * 이 메서드는 핸드셰이크가 완료된 후 호출되며,
     * 현재 구현에서는 추가적인 후처리 작업이 필요하지 않아 비어있습니다.
     *
     * @param request   HTTP 요청 객체
     * @param response  HTTP 응답 객체
     * @param wsHandler WebSocket 핸들러
     * @param exception 핸드셰이크 과정에서 발생한 예외 (있는 경우)
     */
    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception
    ) {
        // 현재 구현에서는 핸드셰이크 후 추가 작업이 필요하지 않음
    }
}

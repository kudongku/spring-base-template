package kr.soulware.teen_mydata.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 및 STOMP 메시징 설정 클래스
 * 이 클래스는 WebSocket 연결 설정과 STOMP 메시지 브로커를 구성합니다.
 * JWT 인증을 통한 보안 WebSocket 연결과 메시지 라우팅을 담당합니다.
 * 주요 기능:
 * - WebSocket 엔드포인트 등록 및 보안 설정
 * - STOMP 메시지 브로커 구성
 * - JWT 기반 인증 인터셉터 적용
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * WebSocket 연결 시 JWT 토큰을 검증하는 핸드셰이크 인터셉터
     * 모든 WebSocket 연결 요청에 대해 인증을 수행합니다.
     */
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    /**
     * STOMP 메시지 브로커 설정
     * 이 메서드는 메시지 브로커의 동작 방식을 구성합니다.
     * - 클라이언트가 구독할 수 있는 목적지(destination) 설정
     * - 애플리케이션에서 메시지를 받을 수 있는 접두사 설정
     *
     * @param config 메시지 브로커 설정을 위한 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 1단계: 클라이언트 구독용 브로커 활성화
        // "/topic"으로 시작하는 목적지에 대한 메시지를 브로드캐스트
        // 예: /topic/chat, /topic/notification 등
        config.enableSimpleBroker("/topic");

        // 2단계: 애플리케이션 메시지 접두사 설정
        // 클라이언트가 서버로 메시지를 보낼 때 사용하는 접두사
        // 예: /app/chat.send, /app/user.join 등
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * WebSocket 엔드포인트 등록 및 보안 설정
     * 이 메서드는 WebSocket 연결을 위한 엔드포인트를 등록하고,
     * CORS 설정, 인증 인터셉터, SockJS 지원을 구성합니다.
     *
     * @param registry STOMP 엔드포인트 등록을 위한 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 엔드포인트 등록 및 설정
        registry.addEndpoint("/ws")  // WebSocket 연결 엔드포인트
            .setAllowedOriginPatterns("*")  // CORS 설정: 모든 도메인에서 접근 허용
            .addInterceptors(jwtHandshakeInterceptor)  // JWT 인증 인터셉터 추가
            .withSockJS();  // SockJS 지원 활성화 (WebSocket을 지원하지 않는 브라우저 대응)
    }

}

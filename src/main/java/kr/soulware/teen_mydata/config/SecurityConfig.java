package kr.soulware.teen_mydata.config;

import kr.soulware.teen_mydata.filter.JwtAuthenticationFilter;
import kr.soulware.teen_mydata.handler.OAuth2AuthenticationSuccessHandler;
import kr.soulware.teen_mydata.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    public static final String[] ALLOWED_URLS = {
        "/api/common/**",        // test용 엔드포인트
        "/v3/api-docs/**",       // OpenAPI 3 문서 관련 엔드포인트
        "/swagger-ui/**",        // Swagger UI 관련 엔드포인트
        "/swagger-ui.html",      // Swagger UI 메인 페이지
        "/swagger-resources/**", // Swagger 리소스
        "/webjars/**",           // 웹자바 리소스
        "/configuration/ui",     // Swagger UI 설정
        "/configuration/security",// Swagger 보안 설정
        "/ws/**"                 // SockJS/WebSocket 폴백 경로 허용 (중요)
    };

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(ALLOWED_URLS).permitAll()
                .anyRequest().authenticated()
            ).addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            ).oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                ).successHandler(oAuth2AuthenticationSuccessHandler)
            );

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
            """
                    ROLE_MASTER > ROLE_ADMIN
                    ROLE_ADMIN > ROLE_USER
                    ROLE_USER > ROLE_GUEST
                """
        );
    }

}

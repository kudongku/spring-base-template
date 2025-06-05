package kr.soulware.teen_mydata.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.soulware.teen_mydata.service.ApiLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;

@Slf4j(topic = "ApiLogFilter")
@Component
@RequiredArgsConstructor
public class ApiLogFilter extends OncePerRequestFilter {

    private final ApiLogService apiLogService;
    private final Environment env;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        long start = System.currentTimeMillis();

        filterChain.doFilter(wrappedRequest, wrappedResponse);

        long duration = System.currentTimeMillis() - start;
        String requestBody = getStringValue(wrappedRequest.getContentAsByteArray());
        String responseBody = getStringValue(wrappedResponse.getContentAsByteArray());
        String uri = request.getRequestURI();
        int status = wrappedResponse.getStatus();
        String message = null;
        String errorCode = null;

        if (status >= 400 && responseBody.trim().startsWith("{")) {
            try {
                JsonNode root = objectMapper.readTree(responseBody);
                message = root.path("message").asText(null);
                errorCode = root.path("errorCode").asText(null);
            } catch (Exception e) {
                log.debug("responseBody JSON 파싱 실패: {}", e.getMessage());
            }
        }

        // Todo. saveLogAsync logic을 dev와 prod 에만 적용
        if (!isSwaggerUri(uri) && isActiveProfile("local", "dev", "prod")) {
            apiLogService.saveLogAsync(
                request.getRequestURI(),
                request.getMethod(),
                requestBody,
                responseBody,
                status,
                errorCode,
                message,
                (int) duration
            );
        }

        log.debug("API 요청 로그 | URI: {} | Method: {} | Status: {} | Duration: {}ms | ErrorCode: {} | Message: {} | RequestBody: {} | ResponseBody: {}",
            request.getRequestURI(),
            request.getMethod(),
            status,
            duration,
            errorCode,
            message,
            requestBody,
            responseBody
        );

        wrappedResponse.copyBodyToResponse();
    }

    private boolean isActiveProfile(String... profiles) {
        return Arrays.stream(env.getActiveProfiles()).anyMatch(p -> Arrays.asList(profiles).contains(p));
    }

    private String getStringValue(byte[] content) {
        if (content == null || content.length == 0) return "";
        return new String(content);
    }

    private boolean isSwaggerUri(String uri) {
        return uri.startsWith("/swagger")
            || uri.startsWith("/v3/api-docs")
            || uri.startsWith("/swagger-ui")
            || uri.startsWith("/api-docs");
    }

}

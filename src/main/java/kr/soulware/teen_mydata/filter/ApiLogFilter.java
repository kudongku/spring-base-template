package kr.soulware.teen_mydata.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.soulware.teen_mydata.service.ApiLogService;
import kr.soulware.teen_mydata.service.MailService;
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
    private final MailService mailService;
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


        if (isSwaggerUri(request.getRequestURI())) {
            wrappedResponse.copyBodyToResponse();
            return;
        }

        long duration = System.currentTimeMillis() - start;
        String requestBody = getStringValue(wrappedRequest.getContentAsByteArray());
        String responseBody = getStringValue(wrappedResponse.getContentAsByteArray());
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

        if (status >= 500 && status < 600) {
            String subject = "[마이데이터 조성사업] 500번대 에러 발생 " + Arrays.toString(env.getActiveProfiles()) + ": " + request.getRequestURI();
            String text = "<h2>🚨 500번대 에러 발생</h2>"
                + "<p><strong>요청 URI:</strong> " + request.getRequestURI() + "</p>"
                + "<p><strong>Method:</strong> " + request.getMethod() + "</p>"
                + "<p><strong>Status:</strong> " + status + "</p>"
                + "<p><strong>RequestBody:</strong> <pre>" + requestBody + "</pre></p>"
                + "<p><strong>ResponseBody:</strong> <pre>" + responseBody + "</pre></p>";

            String notificationEmail = env.getProperty("notification.email");
            mailService.sendErrorMailAsync(subject, text, notificationEmail);
        }

        // Todo. saveLogAsync logic을 dev와 prod 에만 적용
        if (isActiveProfile("local", "dev", "prod")) {
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
            || uri.startsWith("/api-docs")
            || uri.startsWith("/.well-known/appspecific/com.chrome.devtools.json");
    }

}

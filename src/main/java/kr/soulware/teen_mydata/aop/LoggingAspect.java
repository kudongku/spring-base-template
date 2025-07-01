package kr.soulware.teen_mydata.aop;

import jakarta.servlet.http.HttpServletRequest;
import kr.soulware.teen_mydata.dto.request.LoggingData;
import kr.soulware.teen_mydata.dto.response.ApiResponse;
import kr.soulware.teen_mydata.enums.ProfileType;
import kr.soulware.teen_mydata.exception.BusinessException;
import kr.soulware.teen_mydata.service.ApiLogService;
import kr.soulware.teen_mydata.service.MailService;
import kr.soulware.teen_mydata.service.ProfileCheckService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j(topic = "LoggingAspect")
public class LoggingAspect {

    private final ApiLogService apiLogService;
    private final MailService mailService;
    private final ProfileCheckService profileCheckService;

    @Around("execution(* kr.soulware.teen_mydata.controller.*.*(..))" +
        " && !within(kr.soulware.teen_mydata.controller.WebSocketController)")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        LoggingData loggingData = LoggingData.builder().build();

        try {
            loggingData = extractLoggingDataFromJointPoint(joinPoint);
            Object returnValue = joinPoint.proceed();
            loggingData = updateLoggingDataWithResponse(loggingData, returnValue);
            return returnValue;
        } catch (Throwable t) {
            loggingData = handleException(t, loggingData);
            throw t;
        } finally {
            loggingData = loggingData.toBuilder()
                .duration((int) (System.currentTimeMillis() - startTime))
                .build();

            processLogging(loggingData);
        }
    }

    private LoggingData extractLoggingDataFromJointPoint(ProceedingJoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return LoggingData.builder().build();
        }

        HttpServletRequest request = attributes.getRequest();
        String requestUri = request.getRequestURI();
        String httpMethod = request.getMethod();
        String requestParams = request.getQueryString();
        String requestBody = extractRequestBody(joinPoint.getArgs());
        String controllerMethod = joinPoint.getSignature().getDeclaringType().getSimpleName()
            + "." + joinPoint.getSignature().getName();

        LoggingData.LoggingDataBuilder builder = LoggingData.builder()
            .requestUri(requestUri)
            .httpMethod(httpMethod)
            .requestBody(requestBody)
            .requestParams(requestParams)
            .summary(controllerMethod);

        return builder.build();
    }

    private LoggingData updateLoggingDataWithResponse(LoggingData loggingData, Object returnValue) {
        if (!(returnValue instanceof ResponseEntity<?> responseEntity)
            || !(responseEntity.getBody() instanceof ApiResponse<?> apiResponse)
        ) {
            return loggingData;
        }

        return loggingData.toBuilder()
            .responseBody(responseEntity.getBody().toString())
            .status(responseEntity.getStatusCode().value())
            .errorCode(apiResponse.getErrorCode())
            .message(apiResponse.getMessage())
            .build();
    }

    /**
     * extractRequestBody
     * args에 param, query 값을 제외하고 dto만 추출
     */
    private String extractRequestBody(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }

        return Arrays.stream(args)
            .filter(arg -> arg != null
                && !(arg instanceof String)
                && !(arg instanceof Number)
                && !(arg instanceof Boolean)
                && !(arg instanceof Map)
                && !(arg instanceof List)
                && !arg.getClass().isPrimitive()
            )
            .map(Object::toString)
            .collect(Collectors.joining(", "));
    }

    /**
     * extractRequestBody
     * BusinessException일 경우, status 추출
     * message와 stackTrace 추출
     */
    private LoggingData handleException(Throwable t, LoggingData loggingData) {
        int status = t instanceof BusinessException
            ? ((BusinessException) t).getStatus()
            : 500;

        return loggingData.toBuilder()
            .responseBody(Arrays.toString(t.getStackTrace()))
            .message(t.getMessage())
            .status(status)
            .build();
    }

    /**
     * processLogging
     * active.profiles에 따라 로그 프로세싱 과정이 달라집니다.
     * local: log.info
     * dev: log.info, db에 저장, error 메일 전송
     * prod: log.info, db에 저장, error 메일 전송
     */
    private void processLogging(LoggingData data) {
        log.info(
            "[API] URI: {} | Method: {} | Status: {} | Duration: {}ms" +
                " | RequestParam: {}" +
                " | RequestBody: {}" +
                " | ResponseBody: {}" +
                " | ErrorCode: {} | Message: {}",
            data.getRequestUri(),
            data.getHttpMethod(),
            data.getStatus(),
            data.getDuration(),
            data.getErrorCode(),
            data.getMessage(),
            data.getRequestParams(),
            data.getRequestBody(),
            data.getResponseBody()
        );

        if (profileCheckService.isActiveProfile(ProfileType.LOCAL, ProfileType.DEV, ProfileType.PROD)) {
            apiLogService.saveLogAsync(data);
        }

        if (profileCheckService.isActiveProfile(ProfileType.DEV, ProfileType.PROD)
            && data.getStatus() >= 500 && data.getStatus() < 600
        ) {
            mailService.sendErrorMailAsync(data);
        }
    }

}
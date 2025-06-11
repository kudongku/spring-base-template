package kr.soulware.teen_mydata.aop;

import jakarta.servlet.http.HttpServletRequest;
import kr.soulware.teen_mydata.dto.request.LoggingData;
import kr.soulware.teen_mydata.dto.response.ApiResponse;
import kr.soulware.teen_mydata.exception.BusinessException;
import kr.soulware.teen_mydata.service.ApiLogService;
import kr.soulware.teen_mydata.service.MailService;
import kr.soulware.teen_mydata.util.ProfileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j(topic = "LoggingAspect")
public class LoggingAspect {

    private final ApiLogService apiLogService;
    private final MailService mailService;
    private final ProfileUtil profileUtil;

    @Around("execution(* kr.soulware.teen_mydata.controller.*.*(..))")
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
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return LoggingData.builder().build();
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        String requestUri = request.getRequestURI();
        String httpMethod = request.getMethod();
        String requestBody = extractRequestBody(joinPoint.getArgs());

        LoggingData.LoggingDataBuilder builder = LoggingData.builder()
            .requestUri(requestUri)
            .httpMethod(httpMethod)
            .requestBody(requestBody);

        return builder.build();
    }

    private LoggingData updateLoggingDataWithResponse(LoggingData loggingData, Object returnValue) {
        if (!(returnValue instanceof ResponseEntity<?> responseEntity) ||
            !(responseEntity.getBody() instanceof ApiResponse<?> apiResponse)) {
            return loggingData;
        }

        return loggingData.toBuilder()
            .responseBody(responseEntity.getBody().toString())
            .status(responseEntity.getStatusCode().value())
            .errorCode(apiResponse.getErrorCode())
            .message(apiResponse.getMessage())
            .build();
    }

    private String extractRequestBody(Object[] args) {
        return args != null && args.length > 0
            ? String.join("", Arrays.stream(args).map(Object::toString).toArray(String[]::new))
            : null;
    }

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

    private void processLogging(LoggingData data) {
        if (profileUtil.isActiveProfile(ProfileUtil.LOCAL, ProfileUtil.DEV, ProfileUtil.PROD)) {
            apiLogService.saveLogAsync(data);
        }

        log.info("[API] URI: {} | Method: {} | Status: {} | Duration: {}ms | ErrorCode: {} | Message: {} | RequestBody: {} | ResponseBody: {}",
            data.getRequestUri(),
            data.getHttpMethod(),
            data.getStatus(),
            data.getDuration(),
            data.getErrorCode(),
            data.getMessage(),
            data.getRequestBody(),
            data.getResponseBody()
        );

        if (profileUtil.isActiveProfile(ProfileUtil.DEV, ProfileUtil.PROD)
            && data.getStatus() >= 500 && data.getStatus() < 600
        ) {
            mailService.sendErrorMailAsync(data);
        }
    }

}
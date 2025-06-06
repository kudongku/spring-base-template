package kr.soulware.teen_mydata.service;

import kr.soulware.teen_mydata.entity.ApiLog;
import kr.soulware.teen_mydata.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiLogService {

    private final ApiLogRepository apiLogRepository;

    @Transactional
    @Async
    public void saveLogAsync(
        String requestUri,
        String httpMethod,
        String requestBody,
        String responseBody,
        int statusCode,
        String errorCode,
        String errorMessage,
        int durationMs
    ) {
        ApiLog apiLog = new ApiLog(
            requestUri,
            httpMethod,
            requestBody,
            responseBody,
            statusCode,
            errorCode,
            errorMessage,
            durationMs
        );

        apiLogRepository.save(apiLog);
    }

}

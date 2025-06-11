package kr.soulware.teen_mydata.service;

import kr.soulware.teen_mydata.dto.request.LoggingData;
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
    public void saveLogAsync(LoggingData data) {
        ApiLog apiLog = new ApiLog(
            data.getRequestUri(),
            data.getHttpMethod(),
            data.getRequestBody(),
            data.getResponseBody(),
            data.getStatus(),
            data.getMessage(),
            data.getDuration(),
            data.getRequestParams(),
            data.getSummary()
        );

        apiLogRepository.save(apiLog);
    }

}

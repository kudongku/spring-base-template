package kr.soulware.teen_mydata.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class LoggingData {

    private String requestUri;
    private String httpMethod;
    private String requestBody;
    private String responseBody;
    private int status;
    private String errorCode;
    private String message;
    private int duration;

}
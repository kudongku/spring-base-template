package kr.soulware.teen_mydata.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ApiResponse<T> {

    private boolean success;
    private int status;

    // case of (success = true)
    private T data;

    // case of (success = false)
    private String message;
    private String errorCode;

    // 성공 응답용 정적 메서드
    public static <T> ApiResponse<T> success(int status, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .status(status)
            .data(data)
            .build();
    }

    // 실패 응답용 정적 메서드
    public static <T> ApiResponse<T> fail(
        int status,
        String message,
        String errorCode
    ) {
        return ApiResponse.<T>builder()
            .success(false)
            .status(status)
            .message(message)
            .errorCode(errorCode)
            .build();
    }

}

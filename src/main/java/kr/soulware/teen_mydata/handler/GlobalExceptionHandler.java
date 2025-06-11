package kr.soulware.teen_mydata.handler;

import io.swagger.v3.oas.annotations.Hidden;
import kr.soulware.teen_mydata.dto.response.ApiResponse;
import kr.soulware.teen_mydata.exception.CustomNullPointerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
@Hidden
@Slf4j(topic = "GlobalExceptionHandler")
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomNullPointerException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomNullPointerException(CustomNullPointerException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(404, e.getMessage(), "CUSTOM_NULL_POINTER"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        log.error("Unhandled Exception", ex);

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(500, ex.getMessage(), "Internal Server Error"));
    }

}

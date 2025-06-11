package kr.soulware.teen_mydata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.soulware.teen_mydata.dto.request.TestRequestDto;
import kr.soulware.teen_mydata.dto.response.ApiResponse;
import kr.soulware.teen_mydata.exception.CustomNullPointerException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common")
@Tag(name = "테스트 API", description = "테스트 관련 API입니다.")
public class CommonController {

    @GetMapping("/health")
    @Operation(summary = "healthCheck API", description = "200번대 반환")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(200, "ok"));
    }

    @PostMapping("/test/{type}")
    @Operation(summary = "test API", description = "type = fail or error or success")
    public ResponseEntity<ApiResponse<TestRequestDto>> test(
        @PathVariable String type,
        @RequestParam String message,
        @RequestBody TestRequestDto test
    ) throws Exception {
        return switch (type) {
            case "fail" -> throw new CustomNullPointerException(message);
            case "error" -> throw new Exception(message);
            default -> ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, test));
        };
    }

}

package kr.soulware.teen_mydata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.soulware.teen_mydata.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "테스트 API", description = "테스트 관련 API입니다.")
@RequestMapping("/api/common")
@RestController
public class CommonController {

    @GetMapping("/health")
    @Operation(summary = "healthCheck API", description = "200번대 반환")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "ok"));
    }

    @GetMapping("/fail")
    @Operation(summary = "failCheck API", description = "400번대 반환")
    public ResponseEntity<ApiResponse<Void>> failCheck() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(400, "fail", "INVALID_REQUEST"));
    }

    @GetMapping("/error")
    @Operation(summary = "errorCheck API", description = "500번대 반환")
    public ResponseEntity<ApiResponse<Void>> errorCheck() throws Exception {
        throw new Exception();
    }

}

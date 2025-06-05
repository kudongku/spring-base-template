package kr.soulware.teen_mydata.controller;

import kr.soulware.teen_mydata.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/common")
@RestController
public class CommonController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(200, "ok"));
    }

    @GetMapping("/fail")
    public ResponseEntity<ApiResponse<Void>> failCheck() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(400, "fail", "INVALID_REQUEST"));
    }

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<Void>> errorCheck() throws Exception {
        throw new Exception();
    }

}

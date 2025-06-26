package kr.soulware.teen_mydata.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.soulware.teen_mydata.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/private")
@Tag(name = "인증인가 테스트 API", description = "인증인가 테스트 관련 API입니다.")
public class PrivateController {

    @GetMapping("/test")
    @Operation(summary = "authTest API", description = "인증인가 여부 테스트")
    public ResponseEntity<ApiResponse<String>> authTest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = (String) authentication.getPrincipal();
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(200, email));
    }

    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "ADMIN 이상만 접근 가능", description = "ADMIN 이상만 성공, 그 외는 403")
    public ResponseEntity<ApiResponse<String>> adminTest() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(200, "ADMIN OK"));
    }

    @GetMapping("/user-test")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "USER 이상만 접근 가능", description = "USER 이상만 성공, 그 외는 403")
    public ResponseEntity<ApiResponse<String>> userTest() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(200, "USER OK"));
    }

}

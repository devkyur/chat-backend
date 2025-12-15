package com.dating.auth.controller;

import com.dating.auth.dto.LoginRequest;
import com.dating.auth.dto.RefreshTokenRequest;
import com.dating.auth.dto.SignupRequest;
import com.dating.auth.dto.TokenResponse;
import com.dating.auth.service.AuthService;
import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원가입, 로그인, 토큰 관리 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입",
            description = """
                    계정과 프로필을 함께 생성합니다.

                    **필수 필드:**
                    - email, password, name, nickname, birthDate, gender

                    **선택 필드:**
                    - phoneNumber, bio (자기소개), location (위치)
                    - minAgePreference (선호 최소 나이, 기본값: 18)
                    - maxAgePreference (선호 최대 나이, 기본값: 99)
                    - maxDistance (최대 거리 km, 기본값: 50)
                    """
    )
    public ApiResponse<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        log.info("=== Signup request received - email: {}", request.getEmail());
        TokenResponse response = authService.signup(request);
        log.info("=== Signup successful - email: {}", request.getEmail());
        return ApiResponse.success(response);
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 토큰을 발급받습니다.")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("=== Login request received - email: {}", request.getEmail());
        TokenResponse response = authService.login(request);
        log.info("=== Login successful - email: {}", request.getEmail());
        return ApiResponse.success(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token을 발급받습니다.")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refresh(request.getRefreshToken());
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃하여 Refresh Token을 무효화합니다.", security = @SecurityRequirement(name = "JWT"))
    public ApiResponse<Void> logout() {
        Long userId = SecurityUtil.getCurrentUserId();
        authService.logout(userId);
        return ApiResponse.success(null);
    }
}

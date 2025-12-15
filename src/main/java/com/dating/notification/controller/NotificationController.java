package com.dating.notification.controller;

import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import com.dating.notification.dto.FcmTokenRequest;
import com.dating.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "알림", description = "푸시 알림 관리 API - FCM 토큰 등록/삭제")
@SecurityRequirement(name = "JWT")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/tokens")
    @Operation(summary = "FCM 토큰 등록", description = "푸시 알림을 받기 위한 FCM 토큰을 등록합니다.")
    public ApiResponse<Void> registerToken(@Valid @RequestBody FcmTokenRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.registerToken(userId, request.getToken());
        return ApiResponse.success(null);
    }

    @DeleteMapping("/tokens")
    @Operation(summary = "FCM 토큰 삭제", description = "등록된 FCM 토큰을 삭제합니다.")
    public ApiResponse<Void> deleteToken(@Valid @RequestBody FcmTokenRequest request) {
        notificationService.deleteToken(request.getToken());
        return ApiResponse.success(null);
    }
}

package com.dating.notification.controller;

import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import com.dating.notification.dto.FcmTokenRequest;
import com.dating.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/tokens")
    public ApiResponse<Void> registerToken(@Valid @RequestBody FcmTokenRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.registerToken(userId, request.getToken());
        return ApiResponse.success(null);
    }

    @DeleteMapping("/tokens")
    public ApiResponse<Void> deleteToken(@Valid @RequestBody FcmTokenRequest request) {
        notificationService.deleteToken(request.getToken());
        return ApiResponse.success(null);
    }
}

package com.dating.profile.controller;

import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import com.dating.profile.dto.CreateProfileRequest;
import com.dating.profile.dto.ProfileResponse;
import com.dating.profile.dto.UpdateProfileRequest;
import com.dating.profile.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "프로필", description = "사용자 프로필 관리 API")
@SecurityRequirement(name = "JWT")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    @Operation(summary = "프로필 생성", description = "새로운 프로필을 생성합니다.")
    public ApiResponse<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileResponse response = profileService.createProfile(userId, request);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "현재 로그인한 사용자의 프로필을 조회합니다.")
    public ApiResponse<ProfileResponse> getMyProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileResponse response = profileService.getMyProfile(userId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{profileId}")
    @Operation(summary = "프로필 조회", description = "특정 사용자의 프로필을 조회합니다.")
    public ApiResponse<ProfileResponse> getProfile(@PathVariable Long profileId) {
        ProfileResponse response = profileService.getProfile(profileId);
        return ApiResponse.success(response);
    }

    @PatchMapping("/me")
    @Operation(summary = "프로필 수정", description = "내 프로필 정보를 수정합니다.")
    public ApiResponse<ProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileResponse response = profileService.updateProfile(userId, request);
        return ApiResponse.success(response);
    }
}

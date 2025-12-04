package com.dating.profile.controller;

import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import com.dating.profile.dto.CreateProfileRequest;
import com.dating.profile.dto.ProfileResponse;
import com.dating.profile.dto.UpdateProfileRequest;
import com.dating.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ApiResponse<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileResponse response = profileService.createProfile(userId, request);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileResponse response = profileService.getMyProfile(userId);
        return ApiResponse.success(response);
    }

    @GetMapping("/{profileId}")
    public ApiResponse<ProfileResponse> getProfile(@PathVariable Long profileId) {
        ProfileResponse response = profileService.getProfile(profileId);
        return ApiResponse.success(response);
    }

    @PatchMapping("/me")
    public ApiResponse<ProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileResponse response = profileService.updateProfile(userId, request);
        return ApiResponse.success(response);
    }
}

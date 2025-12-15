package com.dating.match.controller;

import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import com.dating.match.dto.MatchResponse;
import com.dating.match.service.MatchService;
import com.dating.profile.dto.ProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
@Tag(name = "매칭", description = "매칭 시스템 API - 추천, 좋아요, 매칭 관리")
@SecurityRequirement(name = "JWT")
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/candidates")
    @Operation(summary = "추천 프로필 조회", description = "매칭 가능한 추천 프로필 목록을 조회합니다.")
    public ApiResponse<List<ProfileResponse>> getCandidates() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ProfileResponse> candidates = matchService.getCandidates(userId);
        return ApiResponse.success(candidates);
    }

    @PostMapping("/{profileId}/like")
    @Operation(summary = "좋아요", description = "특정 프로필에 좋아요를 표시합니다. 상대방도 좋아요를 했다면 매칭이 성사됩니다.")
    public ApiResponse<MatchResponse> like(@PathVariable Long profileId) {
        Long userId = SecurityUtil.getCurrentUserId();
        MatchResponse response = matchService.like(userId, profileId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{profileId}/pass")
    @Operation(summary = "패스", description = "특정 프로필을 건너뜁니다.")
    public ApiResponse<MatchResponse> pass(@PathVariable Long profileId) {
        Long userId = SecurityUtil.getCurrentUserId();
        MatchResponse response = matchService.pass(userId, profileId);
        return ApiResponse.success(response);
    }

    @GetMapping
    @Operation(summary = "내 매칭 목록", description = "매칭 성사된 목록을 조회합니다.")
    public ApiResponse<List<MatchResponse>> getMyMatches() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<MatchResponse> matches = matchService.getMyMatches(userId);
        return ApiResponse.success(matches);
    }
}

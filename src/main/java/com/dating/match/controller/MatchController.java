package com.dating.match.controller;

import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import com.dating.match.dto.MatchResponse;
import com.dating.match.service.MatchService;
import com.dating.profile.dto.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/candidates")
    public ApiResponse<List<ProfileResponse>> getCandidates() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ProfileResponse> candidates = matchService.getCandidates(userId);
        return ApiResponse.success(candidates);
    }

    @PostMapping("/{profileId}/like")
    public ApiResponse<MatchResponse> like(@PathVariable Long profileId) {
        Long userId = SecurityUtil.getCurrentUserId();
        MatchResponse response = matchService.like(userId, profileId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{profileId}/pass")
    public ApiResponse<MatchResponse> pass(@PathVariable Long profileId) {
        Long userId = SecurityUtil.getCurrentUserId();
        MatchResponse response = matchService.pass(userId, profileId);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<List<MatchResponse>> getMyMatches() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<MatchResponse> matches = matchService.getMyMatches(userId);
        return ApiResponse.success(matches);
    }
}

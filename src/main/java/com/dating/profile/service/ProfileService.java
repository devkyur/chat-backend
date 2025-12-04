package com.dating.profile.service;

import com.dating.common.exception.BusinessException;
import com.dating.common.exception.ErrorCode;
import com.dating.profile.domain.Profile;
import com.dating.profile.dto.CreateProfileRequest;
import com.dating.profile.dto.ProfileResponse;
import com.dating.profile.dto.UpdateProfileRequest;
import com.dating.profile.repository.ProfileRepository;
import com.dating.user.domain.User;
import com.dating.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProfileResponse createProfile(Long userId, CreateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (profileRepository.existsByUserId(userId)) {
            throw new BusinessException(ErrorCode.PROFILE_NOT_FOUND, "Profile already exists");
        }

        Profile profile = Profile.builder()
                .user(user)
                .nickname(request.getNickname())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .bio(request.getBio())
                .location(request.getLocation())
                .build();

        Profile savedProfile = profileRepository.save(profile);
        return ProfileResponse.from(savedProfile);
    }

    public ProfileResponse getMyProfile(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        return ProfileResponse.from(profile);
    }

    public ProfileResponse getProfile(Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));
        return ProfileResponse.from(profile);
    }

    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        profile.updateProfile(
                request.getNickname(),
                request.getBio(),
                request.getLocation(),
                request.getMinAgePreference(),
                request.getMaxAgePreference(),
                request.getMaxDistance()
        );

        if (request.getImageUrls() != null) {
            profile.updateImages(request.getImageUrls());
        }

        if (request.getInterests() != null) {
            profile.updateInterests(request.getInterests());
        }

        return ProfileResponse.from(profile);
    }
}

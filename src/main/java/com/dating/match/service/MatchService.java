package com.dating.match.service;

import com.dating.common.exception.BusinessException;
import com.dating.common.exception.ErrorCode;
import com.dating.match.domain.Match;
import com.dating.match.dto.MatchResponse;
import com.dating.match.repository.MatchRepository;
import com.dating.profile.domain.Profile;
import com.dating.profile.dto.ProfileResponse;
import com.dating.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final ProfileRepository profileRepository;

    public List<ProfileResponse> getCandidates(Long userId) {
        Profile myProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        List<Profile> candidates = profileRepository.findAll().stream()
                .filter(profile -> !profile.getId().equals(myProfile.getId()))
                .filter(profile -> !matchRepository.existsByFromProfileIdAndToProfileId(
                        myProfile.getId(), profile.getId()))
                .filter(profile -> isWithinPreferences(myProfile, profile))
                .limit(10)
                .collect(Collectors.toList());

        return candidates.stream()
                .map(ProfileResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public MatchResponse like(Long userId, Long targetProfileId) {
        Profile myProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        Profile targetProfile = profileRepository.findById(targetProfileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        if (myProfile.getId().equals(targetProfile.getId())) {
            throw new BusinessException(ErrorCode.SELF_MATCH_NOT_ALLOWED);
        }

        if (matchRepository.existsByFromProfileIdAndToProfileId(myProfile.getId(), targetProfileId)) {
            throw new BusinessException(ErrorCode.ALREADY_MATCHED);
        }

        Match match = Match.builder()
                .fromProfile(myProfile)
                .toProfile(targetProfile)
                .action(Match.MatchAction.LIKE)
                .build();

        Optional<Match> reverseMatch = matchRepository.findByFromProfileIdAndToProfileId(
                targetProfileId, myProfile.getId());

        if (reverseMatch.isPresent() && reverseMatch.get().getAction() == Match.MatchAction.LIKE) {
            match.markAsMatched();
            reverseMatch.get().markAsMatched();
        }

        Match savedMatch = matchRepository.save(match);
        return MatchResponse.from(savedMatch);
    }

    @Transactional
    public MatchResponse pass(Long userId, Long targetProfileId) {
        Profile myProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        Profile targetProfile = profileRepository.findById(targetProfileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        if (myProfile.getId().equals(targetProfile.getId())) {
            throw new BusinessException(ErrorCode.SELF_MATCH_NOT_ALLOWED);
        }

        Match match = Match.builder()
                .fromProfile(myProfile)
                .toProfile(targetProfile)
                .action(Match.MatchAction.PASS)
                .build();

        Match savedMatch = matchRepository.save(match);
        return MatchResponse.from(savedMatch);
    }

    public List<MatchResponse> getMyMatches(Long userId) {
        Profile myProfile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        List<Match> matches = matchRepository.findMatchesByProfileId(myProfile.getId());
        return matches.stream()
                .map(MatchResponse::from)
                .collect(Collectors.toList());
    }

    private boolean isWithinPreferences(Profile myProfile, Profile candidate) {
        int candidateAge = LocalDate.now().getYear() - candidate.getBirthDate().getYear();
        return candidateAge >= myProfile.getMinAgePreference() &&
               candidateAge <= myProfile.getMaxAgePreference();
    }
}

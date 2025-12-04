package com.dating.profile.dto;

import com.dating.profile.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private Long userId;
    private String nickname;
    private LocalDate birthDate;
    private Profile.Gender gender;
    private String bio;
    private String location;
    private List<String> imageUrls;
    private List<String> interests;
    private Integer minAgePreference;
    private Integer maxAgePreference;
    private Integer maxDistance;

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getNickname(),
                profile.getBirthDate(),
                profile.getGender(),
                profile.getBio(),
                profile.getLocation(),
                profile.getImageUrls(),
                profile.getInterests(),
                profile.getMinAgePreference(),
                profile.getMaxAgePreference(),
                profile.getMaxDistance()
        );
    }
}

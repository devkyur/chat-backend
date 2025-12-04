package com.dating.profile.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String nickname;
    private String bio;
    private String location;
    private Integer minAgePreference;
    private Integer maxAgePreference;
    private Integer maxDistance;
    private List<String> imageUrls;
    private List<String> interests;
}

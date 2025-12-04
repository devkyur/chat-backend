package com.dating.match.dto;

import com.dating.match.domain.Match;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchResponse {
    private Long id;
    private Long fromProfileId;
    private Long toProfileId;
    private Match.MatchAction action;
    private Boolean isMatched;
    private LocalDateTime createdAt;

    public static MatchResponse from(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getFromProfile().getId(),
                match.getToProfile().getId(),
                match.getAction(),
                match.getIsMatched(),
                match.getCreatedAt()
        );
    }
}

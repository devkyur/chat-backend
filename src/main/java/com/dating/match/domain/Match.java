package com.dating.match.domain;

import com.dating.profile.domain.Profile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"from_profile_id", "to_profile_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_profile_id", nullable = false)
    private Profile fromProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_profile_id", nullable = false)
    private Profile toProfile;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MatchAction action;

    @Column(nullable = false)
    private Boolean isMatched = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Match(Profile fromProfile, Profile toProfile, MatchAction action) {
        this.fromProfile = fromProfile;
        this.toProfile = toProfile;
        this.action = action;
        this.isMatched = false;
    }

    public void markAsMatched() {
        this.isMatched = true;
    }

    public enum MatchAction {
        LIKE, PASS
    }
}

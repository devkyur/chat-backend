package com.dating.profile.domain;

import com.dating.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Gender gender;

    @Column(length = 500)
    private String bio;

    @Column(length = 100)
    private String location;

    @ElementCollection
    @CollectionTable(name = "profile_images", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "image_url", length = 500)
    @OrderColumn(name = "image_order")
    private List<String> imageUrls = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "profile_interests", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "interest", length = 50)
    private List<String> interests = new ArrayList<>();

    @Column(nullable = false)
    private Integer minAgePreference = 18;

    @Column(nullable = false)
    private Integer maxAgePreference = 99;

    @Column(nullable = false)
    private Integer maxDistance = 50;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Profile(User user, String nickname, LocalDate birthDate, Gender gender,
                   String bio, String location) {
        this.user = user;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.bio = bio;
        this.location = location;
    }

    public void updateProfile(String nickname, String bio, String location,
                              Integer minAgePreference, Integer maxAgePreference, Integer maxDistance) {
        if (nickname != null) this.nickname = nickname;
        if (bio != null) this.bio = bio;
        if (location != null) this.location = location;
        if (minAgePreference != null) this.minAgePreference = minAgePreference;
        if (maxAgePreference != null) this.maxAgePreference = maxAgePreference;
        if (maxDistance != null) this.maxDistance = maxDistance;
    }

    public void updateImages(List<String> imageUrls) {
        this.imageUrls.clear();
        this.imageUrls.addAll(imageUrls);
    }

    public void updateInterests(List<String> interests) {
        this.interests.clear();
        this.interests.addAll(interests);
    }

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}

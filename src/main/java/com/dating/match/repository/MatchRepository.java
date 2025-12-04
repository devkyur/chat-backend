package com.dating.match.repository;

import com.dating.match.domain.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findByFromProfileIdAndToProfileId(Long fromProfileId, Long toProfileId);

    boolean existsByFromProfileIdAndToProfileId(Long fromProfileId, Long toProfileId);

    @Query("SELECT m FROM Match m WHERE m.fromProfile.id = :profileId AND m.action = 'LIKE'")
    List<Match> findLikesByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT m FROM Match m WHERE m.fromProfile.id = :profileId AND m.isMatched = true")
    List<Match> findMatchesByProfileId(@Param("profileId") Long profileId);

    @Query("SELECT m FROM Match m WHERE m.toProfile.id = :profileId AND m.action = 'LIKE' AND m.isMatched = false")
    List<Match> findPendingLikesForProfile(@Param("profileId") Long profileId);
}

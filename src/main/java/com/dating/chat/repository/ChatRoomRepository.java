package com.dating.chat.repository;

import com.dating.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMatchId(Long matchId);

    @Query("SELECT cr FROM ChatRoom cr " +
           "WHERE cr.match.fromProfile.id = :profileId OR cr.match.toProfile.id = :profileId")
    List<ChatRoom> findByProfileId(@Param("profileId") Long profileId);
}

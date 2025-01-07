package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT r.chat_history from Room r where r.id = :roomId")
    Optional<List<Chat>> findByRoom(@Param("roomId") Long roomId);

}

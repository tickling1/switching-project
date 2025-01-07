package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n JOIN n.room r WHERE r.id = :roomId AND n.id = :noticeId")
    Optional<Notice> findByRoom (@Param("roomId") Long roomId, @Param("noticeId") Long noticeId);
}

package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Participation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {

    @Query("SELECT p FROM Participation p WHERE p.room.id = :roomId AND p.member.id = :memberId")
    Optional<Participation> findByRoomAndMember(@Param("roomId") Long roomId, @Param("memberId") Long memberId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Participation p WHERE p.room.id = :roomId AND p.member.id = :memberId")
    Optional<Participation> findByRoomAndMemberWithLock(@Param("roomId") Long roomId, @Param("memberId") Long memberId);

    @Query("SELECT p FROM Participation p WHERE p.member.id = :memberId AND p.leaveDate IS NULL")
    Optional<Participation> findActiveParticipation(@Param("memberId") Long memberId);

    @Query("""
    SELECT p FROM Participation p
        JOIN p.room r
        JOIN p.member m
    WHERE r.id = :roomId
        AND p.leaveDate IS NULL
        AND m.id <> :adminId
    """)
    List<Participation> findHandOverCandidates(@Param("roomId") Long roomId, @Param("adminId") Long adminId);


}

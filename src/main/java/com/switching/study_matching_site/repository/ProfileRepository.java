package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Boolean existsByIdAndMemberId(Long id, Long memberId);

    @Query("SELECT p FROM Member m JOIN m.profile p WHERE m.id = :memberId")
    Optional<Profile> findProfileByMemberId(@Param("memberId") Long memberId);

}

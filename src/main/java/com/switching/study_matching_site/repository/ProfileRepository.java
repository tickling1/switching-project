package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    @Query("select p from Profile p where p.id = :profileId and p.member.id = :memberId")
    Profile findProfileById(@Param("profileId") Long profileId, @Param("memberId") Long memberId);
}

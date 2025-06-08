package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> { // T: 타입 // id: PK 타입

    Optional<Member> findByLoginId(String loginId);
    Optional<Member> findMemberByLoginId(String loginId);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByPhoneNumber(String phoneNumber);
}

package com.switching.study_matching_site;

import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.dto.member.CustomUserDetails;
import com.switching.study_matching_site.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class SecurityUtil {

    private final MemberRepository memberRepository;

    public Member getMemberByUserDetails() {
        Long memberId = getMemberIdByUserDetails();
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            return findMember.get();
        } else {
            throw new IllegalStateException("잘못된 사용자 접근입니다.");
        }
    }


    public Long getMemberIdByUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        Long memberId = 0L;

        /**
         * Authentication 객체의 getPrincipal() 메서드는 사용자 정보를 반환한다.
         * 반환 값의 타입은 보안 설정과 인증 메커니즘에 따라 달라질 수 있다.
         * @return memberId
         */
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            memberId = principal.getMemberId();
        }

        return memberId;
    }

}

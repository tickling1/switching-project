package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.dto.member.CustomUserDetails;
import com.switching.study_matching_site.dto.member.LoginFilterDto;
import com.switching.study_matching_site.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {

        try {
            //DB에서 조회
            if (memberRepository.findByLoginId(loginId).isPresent()) {

                //UserDetails에 담아서 return하면 AutneticationManager가 검증 함
                return new CustomUserDetails(memberRepository.findByLoginId(loginId).get());
            }
            return null;

        } catch (IndexOutOfBoundsException e) {
            throw new UsernameNotFoundException(loginId);
        }
    }
}

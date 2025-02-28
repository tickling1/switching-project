package com.switching.study_matching_site.service;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.dto.member.LoginDto;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.member.MemberUpdateDto;
import com.switching.study_matching_site.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest(classes = StudyMatchingSiteApplication.class)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    public void 회원_가입() throws Exception {

        // given
        MemberCreateDto memberCreateDto = getMemberDto();
        Long memberId = memberService.joinMember(memberCreateDto);

        // when
        Member joinMember = memberRepository.findById(memberId).get();

        // then
        assertThat(joinMember.getUsername()).isEqualTo(memberCreateDto.getUsername());
        assertThat(joinMember.getLoginId()).isEqualTo(memberCreateDto.getLoginId());
        Assertions.assertTrue(bCryptPasswordEncoder.matches(memberCreateDto.getPassword(), joinMember.getPassword()));
        assertThat(joinMember.getEmail()).isEqualTo(memberCreateDto.getEmail());
        assertThat(joinMember.getPhoneNumber()).isEqualTo(memberCreateDto.getPhoneNumber());
        assertThat(joinMember.getBirthDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @Transactional
    void 중복_회원_예외() throws Exception {

        // given
        MemberCreateDto memberCreateDto1 = getMemberDto();
        MemberCreateDto sameMemberCreateDto1 = getMemberDto();

        // when
        memberService.joinMember(memberCreateDto1);

        try {
            memberService.joinMember(sameMemberCreateDto1); // 예외 발생
        } catch (IllegalStateException e) {
            return;
        }

        // then
        Assertions.fail("중복 예외가 발생해야 한다.");
    }

    @Test
    @Transactional
    void 회원_수정() {

        // given
        MemberCreateDto memberCreateDto = getMemberDto();
        Long memberId = memberService.joinMember(memberCreateDto);
        MemberUpdateDto updateDto = getUpdateDto();

        // when
        memberService.updateMember(memberId, updateDto);
        Member member = memberRepository.findById(memberId).get();

        // then
        Assertions.assertTrue(bCryptPasswordEncoder.matches(memberCreateDto.getPassword(), member.getPassword()));

    }

    @Test
    @Transactional
    void 회원_탈퇴() {

        // given
        MemberCreateDto memberCreateDto = getMemberDto();
        Long memberId = memberService.joinMember(memberCreateDto);

        // when
        memberService.leaveMember(memberId);

        // then
        assertThat(memberRepository.findById(memberId)).isEmpty();
    }

    @Test
    @Transactional
    void 나의_정보_조회() {

        // given
        MemberCreateDto memberCreateDto = getMemberDto();
        System.out.println(memberCreateDto.getPassword());
        System.out.println(memberCreateDto.getPassword() == null);
        Long memberId = memberService.joinMember(memberCreateDto);

        // when
        MemberReadDto myInfo = memberService.myInfo(memberId);

        // then
        assertThat(myInfo.getEmail()).isEqualTo(memberCreateDto.getEmail());
        assertThat(myInfo.getPhoneNumber()).isEqualTo(memberCreateDto.getPhoneNumber());
        assertThat(myInfo.getBirthDate()).isEqualTo(memberCreateDto.getBirthDate());
    }

    @Test
    @DisplayName("로그인 시도")
    @Transactional
    public void 로그인_시도() throws Exception {
        // given
        MemberCreateDto memberCreateDto = getMemberDto();
        Long memberId = memberService.joinMember(memberCreateDto);
        Member member = memberRepository.findById(memberId).get();

        LoginDto loginDto = getLoginDto();


        // when
        MemberCreateDto loginMember = memberService.tryLogin(loginDto);

        // then
        Assertions.assertNotNull(loginMember);
        assertThat(loginMember.getLoginId()).isEqualTo(member.getLoginId());
        assertThat(loginMember.getPassword()).isEqualTo(member.getPassword());
    }


    private static MemberCreateDto getMemberDto() {
        return MemberCreateDto.builder()
                .loginId("kqk1ds")
                .password("ssambbong123")
                .username("김승우")
                .email("xuni1300@gmail.com")
                .phoneNumber("010-9690-0126")
                .birthDate(LocalDate.now())
                .build();
    }


    private static MemberUpdateDto getUpdateDto() {
        return MemberUpdateDto.builder()
                .username("쌈뽕코딩")
                .password("ssambbong123")
                .email("kqk1ds@gmail.com")
                .phoneNumber("010-1234-1234")
                .build();
    }

    private static LoginDto getLoginDto() {
        LoginDto loginDto = LoginDto.builder()
                .loginId("kqk1ds")
                .password("ssambbong123")
                .build();
        return loginDto;
    }
}
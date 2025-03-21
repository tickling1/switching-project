package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.EnterStatus;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.dto.login.LoginRequestDto;
import com.switching.study_matching_site.dto.member.LoginDto;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.member.MemberUpdateDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecurityUtil securityUtil;

    /**
     * 회원 가입 - 중복 회원 검증 후 가입
     */

    public Long joinMember(MemberCreateDto memberCreateDto) {
        validateDuplicateMember(memberCreateDto);
        Member member = memberCreateDto.toEntity();
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }


    /**
     * 비밀번호 찾기 - 로그인 ID로 조회 후 결과 반환
     */
    public String forgetPassword(LoginRequestDto loginRequestDto) {
        Optional<Member> findMember = memberRepository.findMemberByLoginId(loginRequestDto.getLoginId());
        if (findMember.isPresent()) {
            Member member = findMember.get();
            String randomPassword = RandomStringUtils.randomAlphabetic(8);
            member.setPassword(randomPassword);
            return randomPassword;
        } else {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    /**
     * 회원 정보 수정
     */
    public void updateMember(Long memberId, MemberUpdateDto memberUpdateDto) {

        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            if (memberUpdateDto.getUsername() != null) member.setUsername(memberUpdateDto.getUsername());
            if (memberUpdateDto.getEmail() != null) member.setEmail(memberUpdateDto.getEmail());
            if (memberUpdateDto.getPhoneNumber() != null) member.setPhoneNumber(memberUpdateDto.getPhoneNumber());
            if (memberUpdateDto.getPassword() != null) {
                member.setPassword(bCryptPasswordEncoder.encode(memberUpdateDto.getPassword()));
            }
        }
    }

    /**
     * 회원 탈퇴
     */
    public void leaveMember(Long memberId) {
        memberRepository.deleteById(memberId);
    }

    /**
     * 내 정보 보기
     */
    @Transactional(readOnly = true)
    public MemberReadDto myInfo(Long memberId) {
        Long accessMemberId = securityUtil.getMemberIdByUserDetails();
        Optional<Member> findMember = memberRepository.findById(memberId);
            if (findMember.isPresent() && accessMemberId.equals(findMember.get().getId())) {
                return MemberReadDto.fromEntity(findMember.get());

            } else {
                throw new InvalidValueException(ErrorCode.ACCESS_DENIED);
            }
        }


    /**
     * 중복 아이디 검사하기
     * - 동시성 문제 발생 가능(유니크 제약 조건)
     */
    private void validateDuplicateMember(MemberCreateDto memberCreateDTO) {
        Optional<Member> findMember = memberRepository.findByLoginId(memberCreateDTO.getLoginId());
        if (findMember.isPresent()) {
            throw new InvalidValueException(ErrorCode.LOGIN_ID_DUPLICATION );
        }
        Optional<Member> findMemberEmail = memberRepository.findByEmail(memberCreateDTO.getEmail());
        if (findMemberEmail.isPresent()) {
            throw new InvalidValueException(ErrorCode.EMAIL_DUPLICATION);
        }
    }

    @Profile("dev")
    public void initData() {
        Member member = new Member();
        member.setLoginId("ksw");
        member.setUsername("김승우");
        member.setEnterStatus(EnterStatus.OUT);
        member.setPassword(bCryptPasswordEncoder.encode("1234"));
        memberRepository.save(member);

        Member member2 = new Member();
        member2.setLoginId("ksw2");
        member2.setEnterStatus(EnterStatus.OUT);
        member2.setPassword(bCryptPasswordEncoder.encode("1234"));
        memberRepository.save(member2);

        Member member3 = new Member();
        member3.setLoginId("ksw3");
        member3.setEnterStatus(EnterStatus.OUT);
        member3.setPassword(bCryptPasswordEncoder.encode("1234"));
        memberRepository.save(member3);

        Member member4 = new Member();
        member4.setLoginId("ksw4");
        member4.setEnterStatus(EnterStatus.OUT);
        member4.setPassword(bCryptPasswordEncoder.encode("1234"));
        memberRepository.save(member4);

        Member member5 = new Member();
        member5.setLoginId("ksw5");
        member5.setEnterStatus(EnterStatus.OUT);
        member5.setPassword(bCryptPasswordEncoder.encode("1234"));
        memberRepository.save(member5);

        Member member6 = new Member();
        member6.setLoginId("ksw5");
        member6.setEnterStatus(EnterStatus.OUT);
        member6.setPassword(bCryptPasswordEncoder.encode("1234"));
        memberRepository.save(member6);
    }
}

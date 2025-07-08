package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.type.EnterStatus;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.dto.login.LoginRequestDto;
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
        validateDuplicateCreateMember(memberCreateDto);
        Member member = memberCreateDto.toEntity();
        member.setPassword(bCryptPasswordEncoder.encode(member.getPassword()));
        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }


    /**
     * 비밀번호 찾기 - 로그인 ID로 조회 후 결과 반환
     */
    public String forgetPassword(LoginRequestDto loginRequestDto) {
        Optional<Member> findMember = memberRepository.findByLoginId(loginRequestDto.getLoginId());
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
    public void updateMember(MemberUpdateDto memberUpdateDto) {
        Member member = securityUtil.getMemberByUserDetails();

        if (memberUpdateDto.getUsername() != null) {
            member.setUsername(memberUpdateDto.getUsername());
        }
        if (memberUpdateDto.getEmail() != null) {
            validateDuplicateUpdateEmail(memberUpdateDto);
            member.setEmail(memberUpdateDto.getEmail());
        }
        if (memberUpdateDto.getPhoneNumber() != null) {
            validateDuplicateUpdatePhoneNum(memberUpdateDto);
            member.setPhoneNumber(memberUpdateDto.getPhoneNumber());
            }
        if (memberUpdateDto.getPassword() != null) {
            member.setPassword(bCryptPasswordEncoder.encode(memberUpdateDto.getPassword()));
        }
    }

    /**
     * 회원 탈퇴
     */
    public void leaveMember() {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        memberRepository.deleteById(memberId);
    }

    /**
     * 내 정보 보기
     */
    @Transactional(readOnly = true)
    public MemberReadDto myInfo() {
        Member member = securityUtil.getMemberByUserDetails();
        return MemberReadDto.fromEntity(member);
    }


    /**
     * 중복 아이디 검사하기
     * 중복 아이디, 중복 이메일, 중복 핸드폰 번호 검사
     * ++ 메서드 반환 타입 바꾸기
     */
    private void validateDuplicateCreateMember(MemberCreateDto dto) {
        if (memberRepository.existsByLoginId(dto.getLoginId())) {
            throw new InvalidValueException(ErrorCode.LOGIN_ID_DUPLICATION);
        }

        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new InvalidValueException(ErrorCode.EMAIL_DUPLICATION);
        }

        if (memberRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new InvalidValueException(ErrorCode.PHONE_NUM_DUPLICATION);
        }
    }


    private void validateDuplicateUpdateEmail(MemberUpdateDto dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new InvalidValueException(ErrorCode.EMAIL_DUPLICATION);
        }
    }

    private void validateDuplicateUpdatePhoneNum(MemberUpdateDto dto) {
        if (memberRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new InvalidValueException(ErrorCode.PHONE_NUM_DUPLICATION);
        }
    }
}

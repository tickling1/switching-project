package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.EnterStatus;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.dto.member.LoginDto;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.member.MemberUpdateDto;
import com.switching.study_matching_site.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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
     * 회원 로그인
     */
    public MemberCreateDto tryLogin(LoginDto loginDto) {

        Optional<Member> loginMember = memberRepository.findByLoginId(loginDto.getLoginId());
        if (loginMember.isPresent()) {
            Member member = loginMember.get();
            if (bCryptPasswordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
                return MemberCreateDto.fromEntity(member);
            } else {
                throw new IllegalStateException("아이디 또는 비밀번호가 틀립니다.");
            }

        } else {
            throw new IllegalStateException("일치하는 아이디가 없습니다.");
        }
    }

    public Long findMemberId(String loginId) {
        Optional<Member> findMember = memberRepository.findByLoginId(loginId);
        if (findMember.isPresent()) {
            return findMember.get().getId();
        } else {
            return null;
        }
    }

    /**
     * 비밀번호 찾기 - 로그인 ID로 조회 후 결과 반환
     */
    public String forgetPassword(LoginDto loginDto) {
        Optional<Member> findMember = memberRepository.findMemberByLoginId(loginDto.getLoginId());
        if (findMember.isPresent()) {
            Member member = findMember.get();
            String randomPassword = RandomStringUtils.randomAlphabetic(8);
            member.setPassword(randomPassword);
            return randomPassword;
        } else {
            throw new IllegalStateException("존재하는 회원이 없습니다.");
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
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            return MemberReadDto.fromEntity(findMember.get());

        } else {
            return null;
        }
    }


    /**
     *중복 아이디 검사하기
     * - 동시성 문제 발생 가능(유니크 제약 조건)
     */
    private void validateDuplicateMember(MemberCreateDto memberCreateDTO) {
        Optional<Member> findMember = memberRepository.findByLoginId(memberCreateDTO.getLoginId());
        if (findMember.isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디 입니다.");
        }
    }

    @Transactional
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

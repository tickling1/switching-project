package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@SpringBootTest(classes = StudyMatchingSiteApplication.class)
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;


    @Test
    @DisplayName("회원가입")
    @Transactional
    public void 회원_가입() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");
        member.setLoginId("kqk1ds");

        // when
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();
        // then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); // 같은 트랙잭션 JPA 동일성 보장
    }
    
    @Test
    @DisplayName("회원수정")
    @Transactional
    public void 회원_수정() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");
        member.setLoginId("kqk1ds");
        memberRepository.save(member);

        // when
        member.setUsername("memberBBB");
        // then
        Assertions.assertThat(memberRepository.findById(member.getId()).get().getUsername()).isEqualTo("memberBBB");
    }
    
    @Test
    @DisplayName("회원탈퇴")
    @Transactional
    public void 회원_탈퇴() throws Exception {
        // given
        Member member = new Member();
        member.setUsername("memberA");
        member.setLoginId("kqk1ds");
        Member saveMember = memberRepository.save(member);

        // when
        memberRepository.deleteById(saveMember.getId());

        // then
        Assertions.assertThat(memberRepository.findById(member.getId())).isEqualTo(Optional.empty());
    }
    
    @Test
    @DisplayName("회원 로그인")
    @Transactional
    public void 회원_로그인() throws Exception {
        // given
        Member member = new Member();
        member.setLoginId("kqk1ds");
        member.setUsername("memberA");
        member.setPassword("1234");
        memberRepository.save(member);

        // when
        Optional<Member> findMember = memberRepository.findMemberByLoginId(member.getLoginId());
        Member loginMember = findMember.get();

        // then
        Assertions.assertThat(loginMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(loginMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(loginMember.getPassword()).isEqualTo(member.getPassword());
    }

    @Test
    @DisplayName("존재하지 않는 회원 아이디로 비밀번호 조회 시")
    @Transactional
    public void 존재하지_않는_회원_조회() throws Exception {

        // given
        Member member = new Member();
        member.setUsername("memberA");
        member.setLoginId("kqk1ds");
        member.setBirthDate(LocalDate.now());
        memberRepository.save(member);

        // when
        Assertions.assertThatThrownBy(() -> {
            Member findMember = memberRepository.findMemberByLoginId("non-ExistId").get();
        }).isInstanceOf(NoSuchElementException.class);
    }
}

package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.type.EnterStatus;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.member.MemberUpdateDto;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        new MemberService(memberRepository, passwordEncoder, securityUtil);
    }

    private static MemberCreateDto getMemberCreateDto() {
        MemberCreateDto dto = new MemberCreateDto(
                "kqk1234",
                "dqwjfir!",
                "testMember",
                LocalDate.now(),
                "xuni1234@gmail.com",
                "010-1111-1111",
                EnterStatus.ENTER
        );
        return dto;
    }

    @Test
    void 회원_생성_성공() {
        // given
        String loginId = "kqk1234";
        when(memberRepository.findByLoginId(loginId)).thenReturn(Optional.empty());
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setId(100L);
            return member;
        });

        // when
        memberService.joinMember(getMemberCreateDto());

        // then
        // save()에 전달된 Member 객체를 캡처해서 검증할 수 있음
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());

        Member savedMember = captor.getValue();
        assertEquals("kqk1234", savedMember.getLoginId());
        assertEquals("testMember", savedMember.getUsername());
        assertEquals(100L, savedMember.getId());

        verify(memberRepository).save(any());  // save 메서드 호출 검증
    }

    @Test
    void 중복_아이디_회원_생성_실패() {
        /**
         * "어떤 loginId로 조회하든 항상 멤버가 하나 있다고 가정"
         * "어떤 email로 조회하든 항상 멤버가 없다고 가정"
         */
        // given
        when(memberRepository.findByLoginId(any())).thenReturn(Optional.of(new Member()));
        when(memberRepository.findByEmail(any())).thenReturn(Optional.empty());

        MemberCreateDto dto = getMemberCreateDto();

        // when & then
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> memberService.joinMember(dto));
        assertEquals(exception.getErrorCode(), ErrorCode.LOGIN_ID_DUPLICATION);
        // save()가 호출되지 않아야 함
        verify(memberRepository, never()).save(any());
    }

    @Test
    void 중복_이메일_회원_생성_실패() {
        /**
         * "어떤 email로 조회하든 항상 멤버가 하나 있다고 가정"
         * "어떤 loginId로 조회하든 항상 멤버가 없다고 가정"
         */
        // given
        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(new Member()));
        when(memberRepository.findByLoginId(any())).thenReturn(Optional.empty());
        MemberCreateDto dto = getMemberCreateDto();

        // when & then
        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> memberService.joinMember(dto));
        assertEquals(exception.getErrorCode(), ErrorCode.EMAIL_DUPLICATION);
        // save()가 호출되지 않아야 함
        verify(memberRepository, never()).save(any());
    }

    @Test
    void 회원_정보_수정_성공() {
        // given
        // 회원 생성용 Member 객체를 직접 생성하고 ID를 수동으로 설정
        Member member = getMemberCreateDto().toEntity();
        member.setId(100L);

        // memberRepository.findById(100L) 이 호출되면 위에서 만든 member 를 반환하도록 설정
        when(memberRepository.findById(100L)).thenReturn(Optional.of(member));

        // 비밀번호 인코딩 mock 설정: "updatePassword!" → 그대로 반환 (실제 인코딩하지 않음)
        when(passwordEncoder.encode("updatePassword!")).thenReturn("updatePassword!");

        // 회원 수정용 DTO 준비
        MemberUpdateDto dto = new MemberUpdateDto(
                "updateMember1",
                "updatePassword!",
                "xuni1300@gmail.com",
                "010-2222-3333"
        );

        // when
        assertEquals("kqk1234", member.getLoginId());
        assertEquals("dqwjfir!", member.getPassword());
        assertEquals("testMember", member.getUsername());

        // 실제 서비스 메서드 호출: ID 100번 회원의 정보를 dto 값으로 업데이트
        memberService.updateMember(100L, dto);

        // then

        // 수정된 회원 정보를 가져와 검증 (Mock이므로 여전히 위에서 만든 member 객체를 참조하게 됨)
        Member updated = memberRepository.findById(100L).get();

        // 각 필드가 예상대로 업데이트되었는지 확인
        assertEquals("updateMember1", updated.getUsername());
        assertEquals("updatePassword!", updated.getPassword()); // passwordEncoder로 인코딩된 값
        assertEquals("xuni1300@gmail.com", updated.getEmail());
        assertEquals("010-2222-3333", updated.getPhoneNumber());
    }

    @Test
    void 회원_정보_중복_이메일_수정_실패() {
        /**
         * 이메일을 수정하려고 하는데 이미 있는 이메일일 경우 실패해야 함.
         * "어떤 email로 조회하든 항상 멤버가 있다고 가정"
         */
        // given
        Member member = getMemberCreateDto().toEntity();
        member.setId(100L);

        when(memberRepository.findByEmail(any())).thenReturn(Optional.of(new Member()));

        // memberRepository.findById(100L) 이 호출되면 위에서 만든 member 를 반환하도록 설정
        when(memberRepository.findById(100L)).thenReturn(Optional.of(member));

        // 회원 수정용 DTO 준비
        MemberUpdateDto dto = new MemberUpdateDto(
                "updateMember1",
                "updatePassword!",
                "xuni1300@gmail.com",
                "010-2222-3333"
        );

        assertThrows(InvalidValueException.class, () -> memberService.updateMember(100L, dto));

    }

    @Test
    void 회원_탈퇴_성공() {
        when(memberRepository.save(any())).thenAnswer(
                invocationOnMock -> {
                    Member member = invocationOnMock.getArgument(0);
                    member.setId(100L);
                    return member;
                }
        );
        memberService.joinMember(getMemberCreateDto());
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());
        Member savedMember = captor.getValue();

        memberService.leaveMember(savedMember.getId());
        verify(memberRepository, times(1)).deleteById(savedMember.getId());

    }

    @Test
    void 내_정보_보기() {
        Member member = new Member();
        member.setId(100L);
        member.setLoginId("kqk1234");
        member.setPassword("dqwjfir!");
        member.setEmail("xuni1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setUsername("testMember");

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(100L);
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        MemberReadDto memberReadDto = memberService.myInfo(100L);
        verify(memberRepository).findById(100L);

        assertEquals("testMember", memberReadDto.getUsername());
        assertEquals("010-2222-3333", memberReadDto.getPhoneNumber());
        assertEquals("xuni1234@gmail.com", memberReadDto.getEmail());

    }
    @Test
    void 상대_정보_보기_실패() {
        Member member = new Member();
        member.setId(100L);
        member.setLoginId("kqk1234");
        member.setPassword("dqwjfir!");
        member.setEmail("xuni1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setUsername("testMember");

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(1L);
        when(memberRepository.findById(any())).thenReturn(Optional.of(member));

        InvalidValueException exception = assertThrows(InvalidValueException.class, () -> memberService.myInfo(100L));
        assertEquals(exception.getErrorCode(), ErrorCode.ACCESS_DENIED);

    }

}
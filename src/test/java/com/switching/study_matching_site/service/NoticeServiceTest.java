package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Notice;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.*;
import com.switching.study_matching_site.dto.notice.NoticeCreateDto;
import com.switching.study_matching_site.dto.notice.NoticeReadDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.NoticeRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private NoticeService noticeService;

    private static Room createRoomWithJava() {
        Room room = new Room(
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
                6,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.STUDY,
                TechSkill.JAVA,
                3,
                Region.SEOUL,
                OfflineStatus.OFFLINE
        );
        return room;
    }

    private static Room createRoomWithPython() {
        Room room = new Room(
                "파이썬 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
                3,
                LocalTime.now(),
                LocalTime.now().plusHours(2),
                Goal.STUDY,
                TechSkill.PYTHON,
                2,
                Region.INCHEON,
                OfflineStatus.OFFLINE
        );
        return room;
    }

    private static Member createMember() {
        Member member = new Member();
        member.setId(100L);
        member.setLoginId("kqk1234");
        member.setPassword("dqwjfir!");
        member.setEmail("xuni1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setUsername("testMember");
        member.setEnterStatus(EnterStatus.OUT);
        return member;
    }

    private static Member createOtherMember() {
        Member member = new Member();
        member.setId(500L);
        member.setLoginId("kqk1234");
        member.setPassword("dqwjfir!");
        member.setEmail("xuni1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setUsername("testMember");
        member.setEnterStatus(EnterStatus.ENTER);
        return member;
    }

    @Test
    void 공지사항_생성_성공() {
        // given
        Member member = createMember();
        member.setId(100L);
        Room room = createRoomWithJava();
        Participation participation = new Participation(room, RoleType.ADMIN, member);
        NoticeCreateDto noticeCreateDto = new NoticeCreateDto(
                "오늘은 저녁은 삼겹살",
                "세겹집이 되게 맛있음"
        );

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(participationRepository.findActiveParticipation(member.getId())).thenReturn(Optional.of(participation));

        // when
        noticeService.addNotice(noticeCreateDto);
        ArgumentCaptor<Notice> noticeArgumentCaptor = ArgumentCaptor.forClass(Notice.class);
        verify(noticeRepository).save(noticeArgumentCaptor.capture());
        Notice notice = noticeArgumentCaptor.getValue();

        // then
        assertNotNull(notice);
        assertEquals(notice.getNoticeTitle(), noticeCreateDto.getTitle());
        assertEquals(notice.getNoticeContent(), noticeCreateDto.getContent());
    }

    @Test
    void 참여하고_있지_않은_회원_작성_실패() {
        // given
        Member member = createMember();
        member.setId(100L);

        Room room = createRoomWithJava();
        room.setId(100L);

        Room otherRoom = createRoomWithPython();
        otherRoom.setId(200L);

        NoticeCreateDto noticeCreateDto = new NoticeCreateDto(
                "오늘은 저녁은 삼겹살",
                "세겹집이 되게 맛있음"
        );

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(participationRepository.findActiveParticipation(member.getId())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> noticeService.addNotice(noticeCreateDto));
        assertEquals(ErrorCode.PARTICIPATED_NOT_FOUND, ex.getErrorCode());
        verify(participationRepository, never()).save(any(Participation.class));

    }


    @Test
    void 유저_공지사항_생성_실패() {
        // given
        Member member = createMember();
        member.setId(100L);
        Room room = createRoomWithJava();
        Participation participation = new Participation(room, RoleType.USER, member);
        NoticeCreateDto noticeCreateDto = new NoticeCreateDto(
                "오늘은 저녁은 삼겹살",
                "세겹집이 되게 맛있음"
        );

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(participationRepository.findActiveParticipation(member.getId())).thenReturn(Optional.of(participation));

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> noticeService.addNotice(noticeCreateDto));
        assertEquals(ErrorCode.NOTICE_FORBIDDEN, ex.getErrorCode());
        verify(participationRepository, never()).save(any(Participation.class));
    }

    @Test
    void 유저_공지사항_읽기_성공() {
        // given
        Member member = createMember();
        member.setId(100L);

        Member otherMember = createOtherMember();
        otherMember.setId(500L);

        Room room = createRoomWithJava();
        room.setId(10L);

        Participation participation = new Participation(room, RoleType.ADMIN, member);
        Participation participation2 = new Participation(room, RoleType.USER, otherMember);
        Notice notice = new Notice();
        notice.setId(1L);
        notice.setNoticeTitle("삽겹살");
        notice.setNoticeContent("어디서 먹지?");

        room.addNotice(notice);


        when(securityUtil.getMemberIdByUserDetails()).thenReturn(otherMember.getId());
        when(participationRepository.findActiveParticipation(otherMember.getId())).thenReturn(Optional.of(participation2));
        when(noticeRepository.findById(any())).thenReturn(Optional.of(notice));

        // when
        NoticeReadDto noticeReadDto = noticeService.readNotice(notice.getId());

        // then
        assertEquals(noticeReadDto.getTitle(), notice.getNoticeTitle());
        assertEquals(noticeReadDto.getContent(), notice.getNoticeContent());
        verify(noticeRepository, times(1)).findById(1L);
    }

    @Test
    void 방이_없는_회원_읽기_실패() {
        // given
        Member member = createMember();
        member.setId(100L);

        Member otherMember = createOtherMember();
        otherMember.setId(500L);

        Room room = createRoomWithJava();
        room.setId(10L);

        Participation participation = new Participation(room, RoleType.ADMIN, member);
        Participation participation2 = new Participation(room, RoleType.USER, otherMember);
        Notice notice = new Notice();
        notice.setId(1L);
        notice.setNoticeTitle("삽겹살");
        notice.setNoticeContent("어디서 먹지?");

        room.addNotice(notice);


        when(securityUtil.getMemberIdByUserDetails()).thenReturn(otherMember.getId());
        when(participationRepository.findActiveParticipation(otherMember.getId())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> noticeService.readNotice(notice.getId()));
        assertEquals(ErrorCode.PARTICIPATED_NOT_FOUND, ex.getErrorCode());
        verify(participationRepository, times(1)).findActiveParticipation(otherMember.getId());
    }

    @Test
    void 방에_없는_읽기_실패() {
        // given
        Member member = createMember();
        member.setId(100L);

        Member otherMember = createOtherMember();
        otherMember.setId(500L);

        Room room = createRoomWithJava();
        room.setId(10L);

        Participation participation = new Participation(room, RoleType.ADMIN, member);
        Participation participation2 = new Participation(room, RoleType.USER, otherMember);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(otherMember.getId());
        when(participationRepository.findActiveParticipation(otherMember.getId())).thenReturn(Optional.of(participation2));
        when(noticeRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> noticeService.readNotice(1L));
        assertEquals(ErrorCode.NOTICE_NOT_FOUND, ex.getErrorCode());
        verify(noticeRepository, times(1)).findById(any());
    }
}
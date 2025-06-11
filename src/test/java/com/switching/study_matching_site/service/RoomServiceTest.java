package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.*;
import com.switching.study_matching_site.dto.room.RoomCreateDto;
import com.switching.study_matching_site.dto.room.RoomUpdateDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private RoomService roomService;

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

    private static Member alreadyRoomMember() {
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
    void 방_생성_성공() {
        // given
        RoomCreateDto dto = new RoomCreateDto(
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

        Member member = createMember();
        Room room = dto.toEntity();
        room.setId(1L);
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);
        // when
        roomService.newRoomAndParticipation(dto);
        ArgumentCaptor<Participation> participationCaptor = ArgumentCaptor.forClass(Participation.class);
        verify(participationRepository).save(participationCaptor.capture());
        Participation savedParticipation = participationCaptor.getValue();

        // then
        verify(roomRepository, times(1)).save(any());
        assertEquals(savedParticipation.getRoom().getId(), room.getId());
        assertEquals(savedParticipation.getRoom().getRoomTitle(), dto.getRoomTitle());
        assertEquals(EnterStatus.ENTER, savedParticipation.getMember().getEnterStatus());
        assertEquals(1, savedParticipation.getRoom().getParticipation_history().size());
        assertNotNull(room.getUuid());
    }

    @Test
    void 회원이_방_참여중_생성_실패() {
        // given
        RoomCreateDto dto = new RoomCreateDto(
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                1,
                6,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.STUDY,
                TechSkill.JAVA,
                3,
                Region.SEOUL,
                OfflineStatus.OFFLINE
        );

        Member member = alreadyRoomMember();
        Room room = dto.toEntity();
        room.setId(1L);
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> roomService.newRoomAndParticipation(dto));
        assertEquals(ErrorCode.ALREADY_PARTICIPATED, ex.getErrorCode());
        verify(participationRepository, never()).save(any());
        verify(roomRepository, never()).save(any());
    }

    @Test
    void 방_수정_성공() {
        // given
        Room room = createRoomWithJava();
        room.setId(50L);
        Member member = createMember();
        Participation participation = new Participation(room, RoleType.ADMIN, member);

        RoomUpdateDto roomUpdateDto = new RoomUpdateDto(
                "파이썬 프로젝트 인원 모집",
                3,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.IMPROVE,
                TechSkill.PYTHON,
                1,
                Region.JEJU,
                OfflineStatus.ONLINE

        );
        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(participationRepository.findByRoomAndMember(room.getId(), member.getId())).thenReturn(Optional.of(participation));

        // when
        roomService.updateRoom(room.getId(), roomUpdateDto);

        // then
        assertEquals(room.getRoomTitle(), roomUpdateDto.getRoomTitle());
        assertEquals(room.getProjectRegion(), roomUpdateDto.getProjectRegion());
    }

    @Test
    void 방장이_아닌_회원_방_수정() {
        // given
        Room room = createRoomWithJava();
        room.setId(50L);
        Member member = createMember();
        // 방장이 아닌 유저가 참여했을 경우로 가정
        Participation participation = new Participation(room, RoleType.USER, member);

        RoomUpdateDto roomUpdateDto = new RoomUpdateDto(
                "파이썬 프로젝트 인원 모집",
                3,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.IMPROVE,
                TechSkill.PYTHON,
                1,
                Region.JEJU,
                OfflineStatus.ONLINE

        );
        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(participationRepository.findByRoomAndMember(room.getId(), member.getId())).thenReturn(Optional.of(participation));

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> roomService.updateRoom(room.getId(), roomUpdateDto));
        assertEquals(ErrorCode.NOT_ROOM_ADMIN, ex.getErrorCode());
        assertEquals("자바 프로젝트 인원 모집", room.getRoomTitle());
    }

    @Test
    void 수정_하려는_방이_없음_실패() {
        // given
        Room room = createRoomWithJava();
        room.setId(50L);
        Member member = createMember();
        RoomUpdateDto roomUpdateDto = new RoomUpdateDto(
                "파이썬 프로젝트 인원 모집",
                3,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.IMPROVE,
                TechSkill.PYTHON,
                1,
                Region.JEJU,
                OfflineStatus.ONLINE

        );
        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.updateRoom(room.getId(), roomUpdateDto));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, ex.getErrorCode());
        assertEquals("자바 프로젝트 인원 모집", room.getRoomTitle());
    }

    @Test
    void 다른_방의_방장이_다른_방을_수정_실패() {
        // given
        Room room = createRoomWithJava();
        room.setId(50L);
        Room otherRoom = createRoomWithPython();
        otherRoom.setId(200L);

        Member member = createMember();
        Participation participation = new Participation(room, RoleType.ADMIN, member);

        RoomUpdateDto roomUpdateDto = new RoomUpdateDto(
                "자바스크립트 프로젝트 인원 모집",
                3,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.STARTUP,
                TechSkill.JAVASCRIPT,
                2,
                Region.GYEONGGI,
                OfflineStatus.OFFLINE

        );
        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(participationRepository.findByRoomAndMember(otherRoom.getId(), member.getId())).thenReturn(Optional.empty());

        // when
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.updateRoom(otherRoom.getId(), roomUpdateDto));
        assertEquals(ErrorCode.PARTICIPATED_NOT_FOUND, ex.getErrorCode());

        // then
        assertEquals("자바 프로젝트 인원 모집", room.getRoomTitle());
        assertEquals(TechSkill.JAVA, room.getTechSkill());
    }

    @Test
    void 방_삭제_성공() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        Participation participation2 = new Participation(room, RoleType.USER, user);

        when(securityUtil.getMemberByUserDetails()).thenReturn(admin);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(participationRepository.findByRoomAndMember(room.getId(), admin.getId())).thenReturn(Optional.of(participation));

        // when
        roomService.removeRoom(room.getId());

        // then
        assertEquals(EnterStatus.OUT, user.getEnterStatus());
        assertEquals(EnterStatus.OUT, admin.getEnterStatus());
        assertNotNull(participation.getLeaveDate());
        assertNotNull(participation2.getLeaveDate());
        assertEquals(RoomStatus.OFF, room.getRoomStatus());
    }

    @Test
    void 없는_방_삭제() {
        // given
        Member member = createMember();

        when(securityUtil.getMemberByUserDetails()).thenReturn(member);
        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.removeRoom(3L));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 해당_방장이_아닌데_삭제_시도() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        
        Room otherRoom = createRoomWithPython();
        otherRoom.setId(20L);

        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        Participation participation2 = new Participation(room, RoleType.USER, user);

        when(securityUtil.getMemberByUserDetails()).thenReturn(admin);
        when(roomRepository.findById(any())).thenReturn(Optional.of(otherRoom));
        when(participationRepository.findByRoomAndMember(otherRoom.getId(), admin.getId())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.removeRoom(otherRoom.getId()));
        assertEquals(ErrorCode.PARTICIPATED_NOT_FOUND, ex.getErrorCode());

    }

    @Test
    void 방_참여자가_삭제_시도() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        Participation participation2 = new Participation(room, RoleType.USER, user);

        when(securityUtil.getMemberByUserDetails()).thenReturn(user);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(participationRepository.findByRoomAndMember(room.getId(), user.getId())).thenReturn(Optional.of(participation2));

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> roomService.removeRoom(room.getId()));
        assertEquals(ErrorCode.NOT_ROOM_ADMIN, ex.getErrorCode());
    }

    @Test
    void 방_참여_성공() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(2);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));

        // when
        roomService.participateRoom(room.getId());

        // then
        ArgumentCaptor<Participation> captor = ArgumentCaptor.forClass(Participation.class);
        verify(participationRepository).save(captor.capture());
        Participation savedParticipation = captor.getValue();
        assertEquals(savedParticipation.getMember().getId(), user.getId());
        assertEquals(savedParticipation.getMember().getUsername(), user.getUsername());
        verify(participationRepository, times(1)).save(any(Participation.class));
    }

    @Test
    void 중복_참여_실패() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(2);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.ENTER);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> roomService.participateRoom(room.getId()));
        assertEquals(ErrorCode.ALREADY_PARTICIPATED, ex.getErrorCode());
        verify(participationRepository, never()).save(any(Participation.class));
    }

    @Test
    void 방이_없어_참여_실패() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(2);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);
        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.participateRoom(room.getId()));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, ex.getErrorCode());
        verify(participationRepository, never()).save(any(Participation.class));
    }

    @Test
    void 방_인원_초과_입장_실패() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(1);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> roomService.participateRoom(room.getId()));
        assertEquals(ErrorCode.ROOM_FULL, ex.getErrorCode());
        verify(participationRepository, never()).save(any(Participation.class));
    }

    @Test
    void 일반_유저_방_퇴장_성공() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(1);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        Participation participation2 = new Participation(room, RoleType.USER, user);

        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);
        when(participationRepository.findByRoomAndMember(room.getId(), user.getId())).thenReturn(Optional.of(participation2));

        // when
        roomService.leaveRoom(room.getId());

        // then
        assertNotNull(participation2.getLeaveDate());
        assertEquals(EnterStatus.OUT, user.getEnterStatus());
    }

    @Test
    void 방장_넘겨주고_방_퇴장_성공() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(1);
        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        Participation participation2 = new Participation(room, RoleType.USER, user);

        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(securityUtil.getMemberByUserDetails()).thenReturn(admin);
        when(participationRepository.findByRoomAndMember(room.getId(), admin.getId())).thenReturn(Optional.of(participation2));

        // when
        roomService.leaveRoom(room.getId());

        // then
        assertEquals(RoleType.ADMIN, participation.getRoleType());
        assertNotNull(participation.getLeaveDate());
        assertEquals(EnterStatus.OUT, admin.getEnterStatus());
        assertEquals(RoomStatus.ON, room.getRoomStatus());
    }

    @Test
    void 방장_혼자_퇴장_성공() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(1);
        Member admin = createMember();

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));
        when(securityUtil.getMemberByUserDetails()).thenReturn(admin);
        when(participationRepository.findByRoomAndMember(room.getId(), admin.getId())).thenReturn(Optional.of(participation));

        // when
        roomService.leaveRoom(room.getId());

        // then
        assertEquals(RoleType.ADMIN, participation.getRoleType());
        assertNotNull(participation.getLeaveDate());
        assertEquals(EnterStatus.OUT, admin.getEnterStatus());
        assertEquals(RoomStatus.OFF, room.getRoomStatus());
    }

    @Test
    void 퇴장하려는_방이_없음() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(1);
        Member admin = createMember();

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(admin);
        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.leaveRoom(room.getId()));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 해당_방에_참여_X() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setMaxCount(1);
        Member admin = createMember();

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(admin);
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.leaveRoom(room.getId()));
        assertEquals(ErrorCode.PARTICIPATED_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 방_UUID_참여_성공() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setUuid("uuid");

        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);
        when(roomRepository.findRoomByUuid(any())).thenReturn(Optional.of(room));

        // when
        roomService.participateWithUUID(room.getUuid());

        // then
        verify(participationRepository, times(1)).save(any(Participation.class));
        ArgumentCaptor<Participation> captor = ArgumentCaptor.forClass(Participation.class);
        verify(participationRepository).save(captor.capture());
        Participation savedParticipation = captor.getValue();
        assertEquals(RoleType.USER, savedParticipation.getRoleType());
    }

    @Test
    void UUID_로_찾는_방이_X() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setUuid("uuid");

        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.OUT);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);
        when(roomRepository.findRoomByUuid(any())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomService.participateWithUUID(room.getUuid()));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 방에_이미_참여() {
        // given
        Room room = createRoomWithJava();
        room.setId(1L);
        room.setUuid("uuid");

        Member admin = createMember();
        Member user = createMember();
        user.setEnterStatus(EnterStatus.ENTER);
        user.setId(33L);

        Participation participation = new Participation(room, RoleType.ADMIN, admin);
        when(securityUtil.getMemberByUserDetails()).thenReturn(user);

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> roomService.participateWithUUID(room.getUuid()));
        assertEquals(ErrorCode.ALREADY_PARTICIPATED, ex.getErrorCode());
    }
}
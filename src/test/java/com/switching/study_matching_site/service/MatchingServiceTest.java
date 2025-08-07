package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.*;
import com.switching.study_matching_site.dto.matching.ProfileCond;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.ProfileRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private MatchingService matchingService;

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
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
                6,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.IMPROVE,
                TechSkill.PYTHON,
                3,
                Region.JEJU,
                OfflineStatus.OFFLINE
        );
        return room;
    }

    private static Room createRoomWithKotlin() {
        Room room = new Room(
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
                5,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.STUDY,
                TechSkill.KOTLIN,
                3,
                Region.SEOUL,
                OfflineStatus.OFFLINE
        );
        return room;
    }

    private static Member createMemberAndProfile() {
        Member member = new Member();
        member.setId(100L);
        member.setLoginId("kqk1234");
        member.setPassword("dqwjfir!");
        member.setEmail("xuni1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setUsername("testMember");
        member.setEnterStatus(EnterStatus.OUT);

        ProfileCreateDto dto = new ProfileCreateDto(
                OfflineStatus.ONLINE,
                TechSkill.JAVA,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                false
        );
        Profile profile = dto.toProfile();
        member.setProfile(profile);
        profile.setMember(member);
        return member;
    }

    // goal, region, tech skill, isOffline
    @Test
    void 회원이_방_매칭을_성공() {
        // given
        Room room = createRoomWithJava();
        Member member = createMemberAndProfile();
        Profile profile = member.getProfile();

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(profileRepository.findProfileByMemberId(member.getId())).thenReturn(Optional.of(profile));

        ProfileCond profileCond = new ProfileCond(
                profile.getOfflineStatus(),
                profile.getTechSkill(),
                profile.getDesiredLevel(),
                profile.getStudyGoal(),
                profile.getStartTime(),
                profile.getEndTime(),
                profile.getRegion()
        );

        PageRequest pageRequest = PageRequest.of(0, 10);

        RoomInfoResponseDto dto = new RoomInfoResponseDto(
                room.getRoomTitle(),
                room.getCurrentCount(),
                room.getMaxCount(),
                room.getUuid()
        );

        List<RoomInfoResponseDto> dtoList = List.of(dto);
        Page<RoomInfoResponseDto> resultPage = new PageImpl<>(dtoList, pageRequest, dtoList.size());

        when(roomRepository.matchingRoom(any(ProfileCond.class), eq(pageRequest)))
                .thenReturn(resultPage);

        // when
        Page<RoomInfoResponseDto> roomInfoResponseDtos = matchingService.matchingRoomsList();

        // then
        assertThat(roomInfoResponseDtos).isNotNull();
        assertThat(roomInfoResponseDtos.getContent()).isNotEmpty();
        assertThat(roomInfoResponseDtos.getTotalElements()).isEqualTo(1);

        RoomInfoResponseDto roomInfo = roomInfoResponseDtos.getContent().get(0);


        assertThat(roomInfo.getRoomTitle()).isEqualTo(room.getRoomTitle());
        assertThat(roomInfo.getCurrentCount()).isEqualTo(room.getCurrentCount());
        assertThat(roomInfo.getMaxCount()).isEqualTo(room.getMaxCount());
    }

    @Test
    void 회원이_프로필이_없어_방_매칭을_실패() {
        // given
        Room room = createRoomWithJava();
        Member member = createMemberAndProfile();
        member.setProfile(null);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(profileRepository.findProfileByMemberId(member.getId())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> matchingService.matchingRoomsList());
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.PROFILE_NOT_FOUND);
    }

}
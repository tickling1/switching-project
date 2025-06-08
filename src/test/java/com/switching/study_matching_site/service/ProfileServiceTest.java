package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.domain.type.Goal;
import com.switching.study_matching_site.domain.type.OfflineStatus;
import com.switching.study_matching_site.domain.type.Region;
import com.switching.study_matching_site.domain.type.TechSkill;
import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileReadDto;
import com.switching.study_matching_site.dto.profile.ProfileUpdateDto;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ProfileService profileService;

    private static Member createMember() {
        Member member = new Member();
        member.setId(100L);
        member.setLoginId("kqk1234");
        member.setPassword("dqwjfir!");
        member.setEmail("xuni1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setUsername("testMember");
        return member;
    }

    @Test
    void 프로필_생성_성공() {
        // given
        Member member = createMember();
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);

        // when
        ProfileCreateDto dto = new ProfileCreateDto(OfflineStatus.OFFLINE,
                TechSkill.KOTLIN,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                false
                );

        Profile profile = dto.toProfile();
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        profileService.writeProfile(dto);

        // then
        assertEquals(dto.getDesiredLevel(), 3);
        assertEquals(dto.getStudyGoal(), Goal.STUDY);
        assertEquals(dto.getRegion(), Region.SEOUL);
        assertEquals(dto.getIsPrivate(), false);

    }

    @Test
    void 프로필_중복_생성_실패() {
        // given
        Member member = createMember();
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);
        ProfileCreateDto dto = new ProfileCreateDto(OfflineStatus.OFFLINE,
                TechSkill.KOTLIN,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                false
        );

        Profile profile = dto.toProfile();

        // when
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        profileService.writeProfile(dto);

        // then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> profileService.writeProfile(dto));
        assertEquals(ex.getErrorCode(), ErrorCode.PROFILE_ALREADY_EXISTS);
    }

    @Test
    void 프로필_조회_성공() {
        // given
        Member member = createMember();
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);

        Profile profile = new Profile(
                1L,
                OfflineStatus.OFFLINE,
                TechSkill.KOTLIN,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                false,
                member);
        member.setProfile(profile);

        // when
        when(profileRepository.findById(any())).thenReturn(Optional.of(profile));
        ProfileReadDto dto = profileService.readProfile(1L);

        // then
        assertEquals(dto.getStudyGoal(), Goal.STUDY);
        assertEquals(dto.getRegion(), Region.SEOUL);
        assertEquals(dto.getDesiredLevel(), 3);
    }

    @Test
    void 프로필_조회_비공개_실패() {
        // given
        Member member = createMember();
        member.setLoginId("kqk1234");

        // 다른 사용자
        Member member2 = createMember();
        member2.setLoginId("kqk1111");

        ProfileCreateDto dto = new ProfileCreateDto(
                OfflineStatus.OFFLINE,
                TechSkill.KOTLIN,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                true
        );

        Profile profile = dto.toProfile();
        profile.setId(1L);

        // when
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        profileService.writeProfile(dto);

        // 다른 사용자(member2)가 member 를 조회했을 때
        when(securityUtil.getMemberByUserDetails()).thenReturn(member2);
        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        // then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> profileService.readProfile(1L));
        assertEquals(ex.getErrorCode(), ErrorCode.PROFILE_IS_PRIVATE);
    }

    @Test
    void 자신_프로필_조회_비공개_성공() {
        // given
        Member member = createMember();
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);

        ProfileCreateDto dto = new ProfileCreateDto(
                OfflineStatus.OFFLINE,
                TechSkill.KOTLIN,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                true
        );

        Profile profile = dto.toProfile();

        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        profileService.writeProfile(dto);

        // Spring Data JPA의 실제 동작을 흉내내는 게 아니라,
        // "Mock 객체가 save를 호출하면 이 profile을 그대로 리턴해라"는 뜻이기 때문에 테스트 시 직접 연관관계 주입이 필요
        member.addProfile(profile);

        when(profileRepository.findById(1L)).thenReturn(Optional.of(profile));

        // when
        ProfileReadDto readDto = profileService.readProfile(1L);

        // then
        assertEquals(3, readDto.getDesiredLevel());
        assertEquals(Goal.STUDY, readDto.getStudyGoal());
        assertEquals(Region.SEOUL, readDto.getRegion());
        assertTrue(readDto.getIsPrivate());
    }

    @Test
    void 프로필_수정_성공() {
        // given
        Member member = createMember();
        when(securityUtil.getMemberByUserDetails()).thenReturn(member);

        ProfileCreateDto dto = new ProfileCreateDto(
                OfflineStatus.OFFLINE,
                TechSkill.KOTLIN,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                true
        );
        Profile profile = dto.toProfile();
        profile.setId(1L);

        when(profileRepository.save(any(Profile.class))).thenReturn(profile);
        profileService.writeProfile(dto);
        member.addProfile(profile);

        // when & then
        ProfileUpdateDto updateDto = new ProfileUpdateDto(
                OfflineStatus.OFFLINE,
                TechSkill.KOTLIN,
                3,
                Goal.STUDY,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Region.SEOUL,
                true
        );

        profileService.updateProfile(updateDto);
        Profile updateProfile = member.getProfile();

        assertEquals(updateProfile.getStudyGoal(), Goal.STUDY);
        assertEquals(updateProfile.getRegion(), Region.SEOUL);
        assertEquals(updateProfile.getDesiredLevel(), 3);
    }

}
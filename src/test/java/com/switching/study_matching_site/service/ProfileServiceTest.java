package com.switching.study_matching_site.service;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.domain.Region;
import com.switching.study_matching_site.domain.TechSkill;
import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileReadDto;
import com.switching.study_matching_site.dto.profile.ProfileUpdateDto;
import com.switching.study_matching_site.repository.MemberRepository;
import com.switching.study_matching_site.repository.ProfileRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest(classes = StudyMatchingSiteApplication.class)
class ProfileServiceTest {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("프로필 작성")
    @Transactional
    void writeProfile() {
        // given
        ProfileCreateDto profileCreateDto = getProfileDto();

        // when
        Long memberId = memberRepository.save(new Member()).getId();
        Long profileId = profileService.writeProfile(profileCreateDto, memberId).getId();

        Profile findProfile = profileRepository.findById(profileId).get();
        Member writeMember = findProfile.getMember();
        Member member = memberRepository.findById(memberId).get();

        // then
        assertThat(member).isEqualTo(writeMember);
        assertThat(profileCreateDto.getDesiredLevel()).isEqualTo(findProfile.getDesiredLevel());
        assertThat(profileCreateDto.getTechSkill()).isEqualTo(findProfile.getTechSkill());
        assertThat(profileCreateDto.getRegion()).isEqualTo(findProfile.getRegion());
    }

    @Test
    @DisplayName("프로필 수정")
    @Transactional
    public void 프로필_수정() throws Exception {

        // given
        ProfileCreateDto profileCreateDto = getProfileDto();
        Long memberId = memberRepository.save(new Member()).getId();
        ProfileCreateDto createdProfile = profileService.writeProfile(profileCreateDto, memberId);
        Profile findProfile = profileRepository.findById(createdProfile.getId()).get();

        // when
        profileService.updateProfile(findProfile.getId(), getUpdateProfileDto());
        Profile updateProfile = profileRepository.findById(findProfile.getId()).get();

        // then
        assertThat(updateProfile.getDesiredLevel()).isEqualTo(2);
        assertThat(updateProfile.getTechSkill()).isEqualTo(TechSkill.PYTHON);
        assertThat(updateProfile.getRegion()).isEqualTo(Region.BUSAN);
    }

    @Test
    @DisplayName("프로필 조회")
    @Transactional
    public void 프로필_조회() throws Exception {

        // given
        ProfileCreateDto profileCreateDto = getProfileDto();
        ProfileCreateDto createdProfile = profileService.writeProfile(profileCreateDto, memberRepository.save(new Member()).getId());

        // when
        Profile findProfile = profileRepository.findById(createdProfile.getId()).get();

        // then
        assertThat(findProfile.getDesiredLevel()).isEqualTo(3);
        assertThat(findProfile.getTechSkill()).isEqualTo(TechSkill.JAVA);
        assertThat(findProfile.getRegion()).isEqualTo(Region.SEOUL);
        assertThat(findProfile.getIsOffline()).isEqualTo(true);
        assertThat(findProfile.getIsPrivate()).isEqualTo(false);
    }

    private static ProfileCreateDto getProfileDto() {
        ProfileCreateDto profileCreateDto = ProfileCreateDto.builder()
                .desiredLevel(3)
                .techSkill(TechSkill.JAVA)
                .region(Region.SEOUL)
                .isOffline(true)
                .isPrivate(false)
                .startTime(LocalTime.now().minusHours(1))
                .endTime(LocalTime.now().plusHours(1))
                .build();
        return profileCreateDto;
    }

    private static ProfileUpdateDto getUpdateProfileDto() {
        ProfileUpdateDto profileUpdateDto = ProfileUpdateDto.builder()
                .desiredLevel(2)
                .techSkill(TechSkill.PYTHON)
                .region(Region.BUSAN)
                .isOffline(false)
                .isPrivate(true)
                .build();
        return profileUpdateDto;
    }
}
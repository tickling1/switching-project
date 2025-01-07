package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.domain.Region;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = StudyMatchingSiteApplication.class)
class ProfileRepositoryTest {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    @DisplayName("프로필 저장")
    public void 프로필_저장() throws Exception {
        // given
        Member member = new Member();
        Profile profile = new Profile();

        // when
        Member savedMember = memberRepository.save(member);
        Long profileId = profileRepository.save(profile).getId();
        Profile findProfile = profileRepository.findById(profileId).get();

        // then
        assertThat(profile).isEqualTo(findProfile);
    }

    @Test
    @DisplayName("내 프로필 조회")
    @Transactional
    public void 나의_프로필_조회() throws Exception {

        // given
        Member savedMember = memberRepository.save(new Member());
        Profile profile = Profile.builder()
                .region(Region.BUSAN)
                .build();
        profileRepository.save(profile);

        // when
        Profile findProfile = profileRepository.findProfileById(profile.getId(), savedMember.getId());

        // then
        Assertions.assertNotNull(findProfile);
        assertThat(findProfile).isEqualTo(profile);
        assertThat(findProfile.getRegion()).isEqualTo(Region.BUSAN);
    }
}
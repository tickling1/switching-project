package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileReadDto;
import com.switching.study_matching_site.dto.profile.ProfileUpdateDto;
import com.switching.study_matching_site.repository.MemberRepository;
import com.switching.study_matching_site.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final MemberRepository memberRepository;

    public ProfileCreateDto writeProfile(ProfileCreateDto profileCreateDto, Long memberId) {
        Profile profile = profileCreateDto.toProfile();
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Member member = findMember.get();
            member.addProfile(profile);
            Profile savedProfile = profileRepository.save(profile);
            ProfileCreateDto.fromProfile(savedProfile);
            return ProfileCreateDto.fromProfile(savedProfile);
        }
        return null;
    }

    public void updateProfile(Long profileId, ProfileUpdateDto profileUpdateDto) {
        Optional<Profile> findProfile = profileRepository.findById(profileId);
        if (findProfile.isPresent()) {
            Profile profile = findProfile.get();
            profile.setDesiredLevel(profileUpdateDto.getDesiredLevel());
            profile.setRegion(profileUpdateDto.getRegion());
            profile.setStartTime(profileUpdateDto.getStartTime());
            profile.setEndTime(profileUpdateDto.getEndTime());
            profile.setTechSkill(profileUpdateDto.getTechSkill());
            profile.setStudyGoal(profileUpdateDto.getStudyGoal());
            profile.setIsPrivate(profile.getIsPrivate());
            profile.setIsOffline(profile.getIsOffline());
        } else {
            throw new IllegalStateException("프로필이 없습니다. 먼저 작성해주세요!");
        }
    }

    @Transactional(readOnly = true)
    public ProfileReadDto readProfile(Long profileId, Long memberId) {
        Profile profile = profileRepository.findProfileById(profileId, memberId);
        return ProfileReadDto.fromProfile(profile);
    }
}

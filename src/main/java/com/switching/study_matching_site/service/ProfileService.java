package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileReadDto;
import com.switching.study_matching_site.dto.profile.ProfileUpdateDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
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
    private final SecurityUtil securityUtil;


    public void writeProfile(ProfileCreateDto profileCreateDto) {
        Profile profile = profileCreateDto.toProfile();
        Member findMember = securityUtil.getMemberByUserDetails();

        // 이미 프로필을 작성한 경우 에외 발생
        if (findMember.getProfile() == null) {
            findMember.addProfile(profile);
            Profile savedProfile = profileRepository.save(profile);
            ProfileCreateDto.fromProfile(savedProfile);
        } else {
            throw new InvalidValueException(ErrorCode.PROFILE_ALREADY_EXISTS);
        }
    }

    public void updateProfile(Long profileId, ProfileUpdateDto profileUpdateDto) {
        Optional<Profile> findProfile = profileRepository.findById(profileId);
        Long accessMemberId = securityUtil.getMemberIdByUserDetails();

        if (profileRepository.existsByIdAndMemberId(profileId, accessMemberId)) {
            if (findProfile.isPresent()) {
                Profile profile = findProfile.get();
                profile.setDesiredLevel(profileUpdateDto.getDesiredLevel());
                profile.setRegion(profileUpdateDto.getRegion());
                profile.setStartTime(profileUpdateDto.getStartTime());
                profile.setEndTime(profileUpdateDto.getEndTime());
                profile.setTechSkill(profileUpdateDto.getTechSkill());
                profile.setStudyGoal(profileUpdateDto.getStudyGoal());
                profile.setIsPrivate(profile.getIsPrivate());
                profile.setOfflineStatus(profile.getOfflineStatus());
            } else {
                throw new EntityNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
            }
        } else {
            throw new EntityNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public ProfileReadDto readProfile(Long profileId) {
        Optional<Profile> findProfile = profileRepository.findById(profileId);
        if (findProfile.isPresent()) {
            return ProfileReadDto.fromProfile(findProfile.get());
        } else {
            throw new EntityNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }
}

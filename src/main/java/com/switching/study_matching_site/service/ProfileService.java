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
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    // 이미 Security에서 회원 검증을 진행하는데 굳이 다시 해야할까?
    public ProfileCreateDto writeProfile(ProfileCreateDto profileCreateDto, Long memberId) {
        Profile profile = profileCreateDto.toProfile();
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Long accessMemberId = securityUtil.getMemberIdByUserDetails();

            // 실제 프로필을 작성하려고 하는 유저와 로그인 한 유저의 ID 값이 다르면 예외 발생
            if (!memberId.equals(accessMemberId)) {
                throw new InvalidValueException(ErrorCode.ACCESS_DENIED);
            }

            // 이미 프로필을 작성한 경우 에외 발생
            if (member.getProfile() == null) {
                member.addProfile(profile);
                Profile savedProfile = profileRepository.save(profile);
                ProfileCreateDto.fromProfile(savedProfile);
                return ProfileCreateDto.fromProfile(savedProfile);
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
                profile.setIsOffline(profile.getIsOffline());
            } else {
                throw new EntityNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
            }
        } else {
            throw new EntityNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public ProfileReadDto readProfile(Long profileId, Long memberId) {
        Profile profile = profileRepository.findProfileById(profileId, memberId);
        return ProfileReadDto.fromProfile(profile);
    }
}

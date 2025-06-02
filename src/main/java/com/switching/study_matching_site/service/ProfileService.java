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
import com.switching.study_matching_site.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final SecurityUtil securityUtil;

    /**
     * 프로필 작성
     * 이미 프로필이 작성된 상태에서 다시 한번 프로필을 작성하려 하면 예외가 터져야 함.
     */
    public void writeProfile(ProfileCreateDto profileCreateDto) {
        Profile profile = profileCreateDto.toProfile();
        Member findMember = securityUtil.getMemberByUserDetails();

        // 이미 프로필을 작성한 경우 에외 발생
        if (findMember.getProfile() == null) {
            // 연관관계 세팅
            findMember.addProfile(profile);
            Profile savedProfile = profileRepository.save(profile);
            ProfileCreateDto.fromProfile(savedProfile);
        } else {
            throw new InvalidValueException(ErrorCode.PROFILE_ALREADY_EXISTS);
        }
    }

    /**
     * 프로필 업데이트
     * 다른 상대방이 사용자의 프로필을 바꿀 수 없어야 함.
     *
     */
    public void updateProfile(ProfileUpdateDto profileUpdateDto) {
        Member accessMember = securityUtil.getMemberByUserDetails();
        if (accessMember.getProfile() == null) {
            throw new InvalidValueException(ErrorCode.PROFILE_NOT_FOUND);
        } else {
            Profile profile = accessMember.getProfile();
            if (profileUpdateDto.getDesiredLevel() != null) profile.setDesiredLevel(profileUpdateDto.getDesiredLevel());
            if (profileUpdateDto.getRegion() != null) profile.setRegion(profileUpdateDto.getRegion());
            if (profileUpdateDto.getStartTime() != null) profile.setStartTime(profileUpdateDto.getStartTime());
            if (profileUpdateDto.getEndTime() != null) profile.setEndTime(profileUpdateDto.getEndTime());
            if (profileUpdateDto.getTechSkill() != null) profile.setTechSkill(profileUpdateDto.getTechSkill());
            if (profileUpdateDto.getStudyGoal() != null) profile.setStudyGoal(profileUpdateDto.getStudyGoal());
            if (profileUpdateDto.getIsPrivate() != null) profile.setIsPrivate(profileUpdateDto.getIsPrivate());
            if (profileUpdateDto.getIsOffline() != null) profile.setOfflineStatus(profile.getOfflineStatus());
        }
    }

    /**
     * 프로필 보기
     * 상대방의 프로필을 확인하려는 사람의 프로필이 null 일 수도 있음.
     * 상대방이 자신의 프로필을 비공개 처리했다면 프로필을 보지 못함
     * 자신의 프로필은 비공개 처리여도 자신이 볼 수 있어야 함.
     * 정책상 프로필의 비공개 여부에 따라 매칭을 해줄 수 없음.
     *
     */
    @Transactional(readOnly = true)
    public ProfileReadDto readProfile(Long profileId) {
        Member member = securityUtil.getMemberByUserDetails();
        Profile findProfile = profileRepository.findById(profileId).orElseThrow(
                () -> new EntityNotFoundException(ErrorCode.PROFILE_NOT_FOUND));

        // 사용자의 프로필이 비공개 처리인지 아닌지 확인
        if (findProfile.getIsPrivate() == Boolean.TRUE) {
            // 비공개 처리일 경우 자신이 자신의 프로필을 보는 것인지 상대방이 보는 것인지 확인
            // null-safe
            if (member.getProfile() == null || !Objects.equals(member.getProfile().getId(), findProfile.getId())) {
                throw new InvalidValueException(ErrorCode.PROFILE_IS_PRIVATE);
            }
        }
        return ProfileReadDto.fromProfile(findProfile);
    }
}

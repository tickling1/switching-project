package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.dto.matching.ProfileCond;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.repository.ProfileRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RoomRepository roomRepository;
    private final ProfileRepository profileRepository;
    private final SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public Page<RoomInfoResponseDto> matchingRoomsList() {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        Optional<Profile> findProfile = profileRepository.findProfileByMemberId(memberId);

        if (findProfile.isPresent()) {
            Profile profile = findProfile.get();
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
            return roomRepository.matchingRoom(profileCond, pageRequest);

        } else {
            throw new EntityNotFoundException(ErrorCode.PROFILE_NOT_FOUND);
        }
    }


}

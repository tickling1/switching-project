package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.dto.room.RoomCreate;
import com.switching.study_matching_site.exception.*;
import com.switching.study_matching_site.repository.MemberRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;

    // 멤버Id, 방 정보가 필요함 - 방 생성
    public Long newParticipation(RoomCreate roomCreateDto) {
        Long memberId = isParticipation(securityUtil.getMemberIdByUserDetails());
        Room savedRoom = roomRepository.save(roomCreateDto.toEntity());
        Optional<Member> findMember = memberRepository.findById(memberId);

        if (findMember.isPresent()) {
            Participation participation = new Participation(savedRoom, RoleType.ADMIN, findMember.get());
            participation.setMember(findMember.get());
            Participation savedParticipation = participationRepository.save(participation);
            return savedParticipation.getId();
        } else {
            throw new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    // 룸 아이디 필요함 - 방 참여
    public Long participate(Long roomId) {
        Long memberId = isParticipation(securityUtil.getMemberIdByUserDetails());
        Optional<Room> findRoom = roomRepository.findById(roomId);
        Optional<Member> findMember = memberRepository.findById(memberId);

        if (findRoom.isPresent() && findMember.isPresent()) {
            if (findRoom.get().getCurrentCount() > findRoom.get().getMaxCount()) {
                throw new InvalidValueException(ErrorCode.ROOM_FULL);
            }
            Participation participation = new Participation(findRoom.get(), RoleType.USER, findMember.get());
            participation.setMember(findMember.get());
            Participation savedParticipation = participationRepository.save(participation);
            return savedParticipation.getId();
        }
        throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
    }

    // 멤버 아이디, 룸 아이디 필요함 - 방 퇴장
    public void leave(Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        Optional<Member> findMember = memberRepository.findById(securityUtil.getMemberIdByUserDetails());

        if (findRoom.isPresent() && findMember.isPresent()) {
            findRoom.get().setCurrentCount(findRoom.get().getCurrentCount() - 1);
            findMember.get().setEnterStatus(EnterStatus.OUT);

            Optional<Participation> participation = participationRepository.findByRoomAndMember(roomId,
                    securityUtil.getMemberIdByUserDetails());

            if (participation.isPresent()) {
                participation.get().setLeaveDate(LocalDateTime.now());
            } else {
                throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
            }

        } else {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }
    }

    // UUID 로 방참여
    public Long participateWithUUID(String uuid) {
        // 회원이 이미 방에 참여하고 있는지 확인
        isParticipation(securityUtil.getMemberIdByUserDetails());

        Optional<Room> findRoom = roomRepository.findRoomByUuid(uuid);
        Optional<Member> findMember = memberRepository.findById(securityUtil.getMemberIdByUserDetails());
        if (findRoom.isPresent() && findMember.isPresent()) {
            Participation participation = new Participation(findRoom.get(), RoleType.USER, findMember.get());
            participation.setMember(findMember.get());
            Participation savedParticipation = participationRepository.save(participation);
            return savedParticipation.getId();
        } else {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }
    }


    /**
     * 멤버가 이미 방에 참여 있는지 확인
     */
    private Long isParticipation(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            EnterStatus enterStatus = findMember.get().getEnterStatus();
            if (enterStatus.equals(EnterStatus.ENTER)) {
                throw new InvalidValueException(ErrorCode.ALREADY_PARTICIPATED);
            }
        }
        return memberId;
    }

}

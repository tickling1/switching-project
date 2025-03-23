package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.domain.type.EnterStatus;
import com.switching.study_matching_site.domain.type.RoleType;
import com.switching.study_matching_site.dto.room.RoomCreateDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
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
    private final SecurityUtil securityUtil;

    // 방 생성
    public void newRoomAndParticipation(RoomCreateDto roomCreateDto) {

        Member findMember = securityUtil.getMemberByUserDetails();
        isParticipation(findMember);
        Room savedRoom = roomRepository.save(roomCreateDto.toEntity());

        Participation participation = new Participation(savedRoom, RoleType.ADMIN, findMember);
        participation.setMember(findMember);
        participationRepository.save(participation);
    }

    // 방 참여
    public void participate(Long roomId) {

        Member findMember = securityUtil.getMemberByUserDetails();
        isParticipation(findMember);
        Optional<Room> findRoom = roomRepository.findById(roomId);

        if (findRoom.isPresent()) {
            if (findRoom.get().getCurrentCount() > findRoom.get().getMaxCount()) {
                throw new InvalidValueException(ErrorCode.ROOM_FULL);
            }

            Participation participation = new Participation(findRoom.get(), RoleType.USER, findMember);
            participation.setMember(findMember);
            participationRepository.save(participation);
        } else {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }
    }

    // 방 퇴장
    public void leave(Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        Member findMember = securityUtil.getMemberByUserDetails();

        if (findRoom.isPresent()) {
            findRoom.get().setCurrentCount(findRoom.get().getCurrentCount() - 1);
            findMember.setEnterStatus(EnterStatus.OUT);

            Optional<Participation> participation = participationRepository.findByRoomAndMember(roomId, findMember.getId());
            if (participation.isPresent()) {
                participation.get().setLeaveDate(LocalDateTime.now());
            } else {
                throw new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND);
            }

        } else {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }
    }

    // UUID 로 방참여
    public void participateWithUUID(String uuid) {
        Member findMember = securityUtil.getMemberByUserDetails();
        isParticipation(findMember);

        Optional<Room> findRoom = roomRepository.findRoomByUuid(uuid);
        if (findRoom.isPresent()) {
            Participation participation = new Participation(findRoom.get(), RoleType.USER, findMember);
            participation.setMember(findMember);
            participationRepository.save(participation);
        }
        throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
    }


    /**
     * 멤버가 이미 방에 참여 있는지 확인
     */
    private void isParticipation(Member member) {
        EnterStatus enterStatus = member.getEnterStatus();
        if (enterStatus.equals(EnterStatus.ENTER)) {
            throw new InvalidValueException(ErrorCode.ALREADY_PARTICIPATED);
        }
    }

}

package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.domain.type.EnterStatus;
import com.switching.study_matching_site.domain.type.RoleType;
import com.switching.study_matching_site.domain.type.RoomStatus;
import com.switching.study_matching_site.dto.room.RoomCreateDto;
import com.switching.study_matching_site.dto.room.RoomUpdateDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 방 생성, 방 수정, 방 삭제, 방 퇴장, 방 참여, UUID로 방 참여
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final ParticipationRepository participationRepository;
    private final RoomRepository roomRepository;
    private final SecurityUtil securityUtil;

    /**
     * 방 생성
     */
    public void newRoomAndParticipation(RoomCreateDto roomCreateDto) {

        Member findMember = securityUtil.getMemberByUserDetails();
        isParticipation(findMember);
        Room savedRoom = roomRepository.save(roomCreateDto.toEntity());

        Participation participation = new Participation(savedRoom, RoleType.ADMIN, findMember);
        participation.setMember(findMember);
        participationRepository.save(participation);
    }

    /**
     *  방 수정
     */
    public void updateRoom(Long roomId, RoomUpdateDto roomUpdateDto) {
        Long findMemberId = securityUtil.getMemberIdByUserDetails();

        // 회원이 수정하려는 방이 있는지 확인
        Room findRoom = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));

        // 회원이 해당 방에 참여 하고 있는지 확인
        Participation participation = participationRepository.findByRoomAndMember(roomId, findMemberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));

        // 방 권한이 관리자가 아니면 예외 발생
        validateAdminPermission(participation);

        // 위에 로직을 모두 통과하면 방 수정 성공
        updateRoom(findRoom, roomUpdateDto);
    }

    /**
     * 방 삭제
     * 방 모집 상태를 OFF 로 설정 후 삭제
     * 방 삭제 이후에는 안에 있는 모든 인원들의 MEMBER 상태가 EnterStatus 가 OUT이 되어 있어야 함. - 테스트 필요
     * 방장만 방을 삭제할 수 있고, 현재 남은 인원이 1명(방장)이여야 방을 삭제 가능
     */
    public void removeRoom(Long roomId) {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        Room findRoom = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));
        Participation participation = participationRepository.findByRoomAndMember(memberId, roomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));

        validateAdminPermission(participation);
        findRoom.setRoomStatus(RoomStatus.OFF);

        if (findRoom.getCurrentCount() == 1) {
            roomRepository.deleteById(roomId);
        } else {
            throw new InvalidValueException(ErrorCode.ROOM_FAILED_REMOVE);
        }
    }

    /**
     * 방 참여
     * 방을 참여한 후에는 멤버의 EnterStatus 가 Enter(참여) 으로 되어있어야 함. - isParticipation 메서드 로직을 타면 참여 상태로 변경
     * 동시성 이슈가 있을 수 있으므로 확인해볼 것
     */
    public void participateRoom(Long roomId) {
        Member findMember = securityUtil.getMemberByUserDetails();

        // 회원이 다른 방에 이미 참여 중인지 확인
        isParticipation(findMember);

        // 방이 없다면 예외 발생
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));

        // 현재 방 인원이 설정된 방에 인원보다 같거나 많을 경우 예외 발생
        if (room.getCurrentCount() >= room.getMaxCount()) throw new InvalidValueException(ErrorCode.ROOM_FULL);

        // 위에 검증 로직이 모두 통과한다면 멤버에게 유저 권한을 주고
        // EnterStatus(ENTER), participation_history(add) 추가, CurrentCount + 1 을 추가해줌
        Participation participation = new Participation(room, RoleType.USER, findMember);

        // 모든 로직이 정상적으로 종료되면 영속화
        participationRepository.save(participation);
    }

    /**
     * 방 퇴장
     * 1. 방장이 방을 퇴장했을 때, 방에 있는 랜덤한 인원한테 방장을 부여해야 함.
     * or
     * 2. 방장이 방을 삭제했을 때 모든 인원의 EnterStatus.OUT 을 구현하는 방법
     */
    public void leaveRoom(Long roomId) {
        Room findRoom = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));
        Member findMember = securityUtil.getMemberByUserDetails();

        findRoom.setCurrentCount(findRoom.getCurrentCount() - 1);
        findMember.setEnterStatus(EnterStatus.OUT);

        Participation participation = participationRepository.findByRoomAndMember(roomId, findMember.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));
        participation.setLeaveDate(LocalDateTime.now());

        // 방장일 경우 다음 사람에게 방장을 넘김.
        if (validateAdminPermission(participation)) {

            // 방의 참여기록을 가져오고
            List<Participation> history = findRoom.getParticipation_history();
            // 자료구조에 후보자들을 넣음(후보자 기준: 나간 기록이 없는 참여자들)
            ArrayList<Participation> candidate = new ArrayList<>();
            for (Participation p : history) {
                if (p.getLeaveDate() == null) {
                    candidate.add(p);
                }
            }
            // 첫번째 후보자에게 방장 부여
            if (!candidate.isEmpty()) {
                Participation participant = candidate.get(0);
                participant.setRoleType(RoleType.ADMIN);
            } else {
                // 방에 아무도 없으면 삭제(방장 없음 처리)
                roomRepository.delete(findRoom);
            }
        }
    }

    /**
     * UUID 로 방참여
     */
    public void participateWithUUID(String uuid) {
        Member findMember = securityUtil.getMemberByUserDetails();
        isParticipation(findMember);
        Room findRoom = roomRepository.findRoomByUuid(uuid).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));
        Participation participation = new Participation(findRoom, RoleType.USER, findMember);
        participation.setMember(findMember);
        participationRepository.save(participation);
    }


    /**
     * 멤버가 이미 방에 참여 있는지 확인
     * 참여하고 있지 않다면 ENTER(입장)
     */
    private void isParticipation(Member member) {
        EnterStatus enterStatus = member.getEnterStatus();
        if (enterStatus.equals(EnterStatus.ENTER)) {
            throw new InvalidValueException(ErrorCode.ALREADY_PARTICIPATED);
        }
    }

    /**
     * 방장 권한이 있는지 확인
     */
    private boolean validateAdminPermission(Participation participation) {
        if (participation.getRoleType() == RoleType.ADMIN) {
            return true;
        } else {
            throw new InvalidValueException(ErrorCode.NOT_ROOM_ADMIN);
        }
    }

    private void updateRoom(Room room, RoomUpdateDto dto) {
        if (dto.getRoomTitle() != null) room.setRoomTitle(dto.getRoomTitle());
        if (dto.getMaxCount() != null) room.setMaxCount(dto.getMaxCount());
        if (dto.getStartTime() != null) room.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) room.setEndTime(dto.getEndTime());
        if (dto.getOfflineStatus() != null) room.setOfflineStatus(dto.getOfflineStatus());
        if (dto.getProjectGoal() != null) room.setProjectGoal(dto.getProjectGoal());
        if (dto.getProjectLevel() != null) room.setProjectLevel(dto.getProjectLevel());
        if (dto.getProjectRegion() != null) room.setProjectRegion(dto.getProjectRegion());
        if (dto.getTechSkill() != null) room.setTechSkill(dto.getTechSkill());
    }
}

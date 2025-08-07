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
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.dialect.lock.PessimisticEntityLockException;
import org.hibernate.exception.LockTimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionSynchronization;

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
    private final EntityManager em;

    /**
     * 방 생성
     */
    public void newRoomAndParticipation(RoomCreateDto roomCreateDto) {
        Member findMember = securityUtil.getMemberByUserDetails();
        isParticipationRoom(findMember);
        Room savedRoom = roomRepository.save(roomCreateDto.toEntity());

        // 연관 관계 메서드
        Participation participation = new Participation(savedRoom, RoleType.ADMIN, findMember);
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
        updateRoomDetail(findRoom, roomUpdateDto);
    }

    private void updateRoomDetail(Room room, RoomUpdateDto dto) {
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

    /**
     * 방 삭제
     * 방 모집 상태를 OFF 로 설정 후 삭제
     * 방 삭제 이후에는 안에 있는 모든 인원들의 MEMBER 상태가 EnterStatus 가 OUT이 되어 있어야 함. - 테스트 필요
     * 방장만 방을 삭제할 수 있고, 현재 남은 인원이 1명(방장)이여야 방을 삭제 가능
     */
    public void removeRoom(Long roomId) {
        Member member = securityUtil.getMemberByUserDetails();
        Room findRoom = roomRepository.findById(roomId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));

        // 해당 방에 참여하는 것이 맞는지 확인
        Participation participation = participationRepository.findByRoomAndMember(roomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));

        // 해당 방의 Admin 인지 확인
        validateAdminPermission(participation);
        // 방을 나간 시간과 회원들의 방 입장 상태 정리
        findRoom.clearParticipation();
        // 사용자(방장) 의 상태를 나감 상태로 변경
        member.setEnterStatus(EnterStatus.OUT);
        // 방 상태를 나감 표시하고 SOFT DELETE
        findRoom.setRoomStatus(RoomStatus.OFF);
    }

    /**
     * 방 참여 (synchronized) 로 해결
     * 이미 다른 방에 참여 하고 있다면 예외를 터트려야 함.
     * 들어가려는 방이 존재하지 않다면 예외를 터트려야 함.
     * 방을 참여한 후에는 멤버의 EnterStatus 가 Enter(참여) 으로 되어있어야 함.
     * (Participation을 생성하면 Enter 상태로 변환)
     */
    /*@Transactional
    public synchronized void participateRoomWithSynchronized(Long roomId) {
        Member findMember = securityUtil.getMemberByUserDetails();
        // 회원이 다른 방에 이미 참여 중인지 확인
        isParticipationRoom(findMember);

        // 방이 없다면 예외 발생
        // + 방이 없고, 방 상태가 OFF 여야함.
        Room room = roomRepository.findRoomIdActivity2(roomId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));

        // 현재 방 인원이 설정된 방에 인원보다 같거나 많을 경우 예외 발생
        if (room.getCurrentCount() + 1 > room.getMaxCount()) throw new InvalidValueException(ErrorCode.ROOM_FULL);

        // 위에 검증 로직이 모두 통과한다면 멤버에게 유저 권한을 주고
        // EnterStatus(ENTER), participation_history(add) 추가, CurrentCount + 1 을 추가해줌
        Participation participation = new Participation(room, RoleType.USER, findMember);

        // 모든 로직이 정상적으로 종료되면 영속화
        participationRepository.save(participation);
        em.flush();
    }*/

    /**
     * JPA 비관적 락으로 동시성 문제 해결
     */
    public void participateRoom(Long roomId) throws PessimisticEntityLockException, LockTimeoutException {
        Member findMember = securityUtil.getMemberByUserDetails();
        // 회원이 다른 방에 이미 참여 중인지 확인
        isParticipationRoom(findMember);

        // 방이 없다면 예외 발생
        // + 방이 없고, 방 상태가 OFF 여야함.
        Room room = roomRepository.findRoomIdActivity(roomId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));

        // 현재 방 인원이 설정된 방에 인원보다 같거나 많을 경우 예외 발생
        if (room.getCurrentCount() + 1 > room.getMaxCount()) {
            throw new InvalidValueException(ErrorCode.ROOM_FULL);
        }

        // 위에 검증 로직이 모두 통과한다면 멤버에게 유저 권한을 주고
        // EnterStatus(ENTER), participation_history(add) 추가, CurrentCount + 1 을 추가해줌
        Participation participation = new Participation(room, RoleType.USER, findMember);

        // 모든 로직이 정상적으로 종료되면 영속화
        participationRepository.save(participation);
    }

    /**
     * 방 퇴장
     * 방장이 방을 퇴장했을 때, 방에 있는 랜덤한 인원한테 방장을 부여해야 함.
     * 방장이든 유저든 나간 후에는 방 입장 상태가 OUT 상태로 바뀌어야하고 나간 시간이 기록되어야 함.
     * 동시성
     */
    public void leaveRoom(Long roomId) throws PessimisticEntityLockException, LockTimeoutException {
        Member member = securityUtil.getMemberByUserDetails();
        Room findRoom = roomRepository.findByIdWithLock(roomId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));

        Participation participation = participationRepository.findByRoomAndMemberWithLock(roomId, member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));

        // 퇴장 처리
        participation.setLeaveDate(LocalDateTime.now());
        member.setEnterStatus(EnterStatus.OUT);
        findRoom.setCurrentCount(findRoom.getCurrentCount() - 1);

        // 방장일 경우 → 위임 처리
        if (participation.getRoleType() == RoleType.ADMIN) {
            // 위임 대상 후보자 조회 (나가지 않았고, 자신이 아닌 참여자)
            List<Participation> candidates = participationRepository
                    .findHandOverCandidates(findRoom.getId(), member.getId());
            if (!candidates.isEmpty()) {
                Participation nextAdmin = candidates.get(0);
                nextAdmin.setRoleType(RoleType.ADMIN);
            } else {
                // 아무도 없다면 방 종료 처리
                findRoom.setRoomStatus(RoomStatus.OFF);
            }
        }
    }

    /**
     * UUID 로 방참여
     */
    public void participateWithUUID(String uuid) throws PessimisticEntityLockException, LockTimeoutException{
        Member findMember = securityUtil.getMemberByUserDetails();
        isParticipationRoom(findMember);

        Room findRoom = roomRepository.findRoomByUuid(uuid).orElseThrow(() -> new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND));

        // 현재 방 인원이 설정된 방에 인원보다 같거나 많을 경우 예외 발생
        if (findRoom.getCurrentCount() + 1 > findRoom.getMaxCount()) {
            throw new InvalidValueException(ErrorCode.ROOM_FULL);
        }

        Participation participation = new Participation(findRoom, RoleType.USER, findMember);
        participation.setMember(findMember);
        participationRepository.save(participation);
    }


    /**
     * 멤버가 이미 방에 참여 있는지 확인
     * 참여하고 있지 않다면 ENTER(입장)
     */
    private void isParticipationRoom(Member member) {
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
}

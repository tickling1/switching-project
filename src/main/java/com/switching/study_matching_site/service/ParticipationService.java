package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.dto.room.RoomCreate;
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

    // 멤버Id, 방 정보가 필요함 - 방 생성
    public Long newParticipation(Long memberId, RoomCreate roomCreateDto) {
        isParticipation(memberId);
        Room savedRoom = roomRepository.save(roomCreateDto.toEntity());
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            Participation participation = new Participation(savedRoom, RoleType.ADMIN, findMember.get());
            participation.setMember(findMember.get());
            Participation savedParticipation = participationRepository.save(participation);
            return savedParticipation.getId();
        }
        return null;
    }

    // 멤버 아이디, 룸 아이디 필요함 - 방 참여
    public Long participate(Long memberId, Long roomId) {
        isParticipation(memberId);
        Optional<Room> findRoom = roomRepository.findById(roomId);
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findRoom.isPresent() && findMember.isPresent()) {
            if (findRoom.get().getCurrentCount() > findRoom.get().getMaxCount()) {
                throw new IllegalStateException("현재 방 인원이 가득찼습니다.");
            }
            Participation participation = new Participation(findRoom.get(), RoleType.USER, findMember.get());
            participation.setMember(findMember.get());
            Participation savedParticipation = participationRepository.save(participation);
            return savedParticipation.getId();
        }
        throw new IllegalStateException("참여하려는 방이 존재하지 않습니다.");
    }

    // 멤버 아이디, 룸 아이디 필요함 - 방 퇴장
    public void leave(Long memberId, Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        Optional<Member> findMember = memberRepository.findById(memberId);

        if (findRoom.isPresent() && findMember.isPresent()) {
            findRoom.get().setCurrentCount(findRoom.get().getCurrentCount() - 1);
            findMember.get().setEnterStatus(EnterStatus.OUT);

            Optional<Participation> participation = participationRepository.findByRoomAndMember(roomId, memberId);
            if (participation.isPresent()) {
                participation.get().setLeaveDate(LocalDateTime.now());
            } else {
                throw new IllegalStateException("참여 중인 방이 없습니다.");
            }
        } else {
            throw new IllegalStateException("퇴장하려는 방이 없습니다.");
        }
    }

    // UUID 로 방참여
    public Long participateWithUUID(Long memberId, String uuid) {
        isParticipation(memberId);
        Optional<Room> findRoom = roomRepository.findRoomByUuid(uuid);
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findRoom.isPresent() && findMember.isPresent()) {
            Participation participation = new Participation(findRoom.get(), RoleType.USER, findMember.get());
            participation.setMember(findMember.get());
            Participation savedParticipation = participationRepository.save(participation);
            return savedParticipation.getId();
        }
        throw new IllegalStateException("참여하려는 방이 존재하지 않습니다.");
    }


    /**
     * 멤버가 이미 방에 참여 있는지 확인
     */
    private void isParticipation(Long memberId) {
        Optional<Member> findMember = memberRepository.findById(memberId);
        if (findMember.isPresent()) {
            EnterStatus enterStatus = findMember.get().getEnterStatus();
            if (enterStatus.equals(EnterStatus.ENTER)) {
                throw new IllegalStateException("이미 참여중인 방이 있습니다.");
            }
        }
    }

}

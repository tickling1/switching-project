package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.RoomStatus;
import com.switching.study_matching_site.dto.room.RoomCreate;
import com.switching.study_matching_site.dto.room.RoomDetail;
import com.switching.study_matching_site.dto.room.RoomInfo;
import com.switching.study_matching_site.dto.room.RoomUpdate;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    // 스터디 방 생성
    public Long createRoom(RoomCreate roomCreateDto) {
        Room entity = roomCreateDto.toEntity();
        // 방 UUID 설정
        entity.setUuid(UUID.randomUUID().toString());
        Room savedRoom = roomRepository.save(entity);
        return savedRoom.getId();
    }

    // 스터디 방 상세 조회
    public RoomDetail findRoomById(Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (findRoom.isPresent()) {
            return RoomDetail.fromEntity(findRoom.get());
        }
        return null;
    }

    // 스터디 방 수정
    public void updateRoom(Long roomId, RoomUpdate roomUpdateDto) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (findRoom.isPresent()) {
            Room room = findRoom.get();
            room.setRoomTitle(roomUpdateDto.getRoomTitle());
            room.setMaxCount(roomUpdateDto.getMaxCount());
            room.setStartTime(roomUpdateDto.getStartTime());
            room.setEndTime(roomUpdateDto.getEndTime());
            room.setOfflineStatus(roomUpdateDto.getOfflineStatus());
            room.setProjectGoal(roomUpdateDto.getProjectGoal());
            room.setProjectLevel(roomUpdateDto.getProjectLevel());
            room.setProjectRegion(roomUpdateDto.getProjectRegion());
            room.setTechSkill(roomUpdateDto.getTechSkill());
        }
    }

    // 스터디 방 전체 목록
    @Transactional(readOnly = true)
    public List<RoomInfo> roomInfoList() {
        List<Room> roomList = roomRepository.findAll();
        List<RoomInfo> roomInfoList = new ArrayList<>();

        for (Room room : roomList) {
            roomInfoList.add(RoomInfo.fromEntity(room));
        }
        return roomInfoList;
    }

    // 스터디 방 삭제
    public void removeRoom(Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (findRoom.isPresent()) {
            findRoom.get().setRoomStatus(RoomStatus.OFF);
            if (findRoom.get().getCurrentCount() == 1) {
                roomRepository.deleteById(roomId);
            } else {
                throw new IllegalStateException("아직 방 인원이 남아있습니다.");
            }
        }
    }
}

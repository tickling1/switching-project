package com.switching.study_matching_site.service;


import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.dto.condition.RoomSearchCond;
import com.switching.study_matching_site.dto.room.*;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomSearchService {

    private final RoomRepository roomRepository;

    /**
     * 방 정보 상세 조회
     */
    @Transactional(readOnly = true)
    public RoomDetailDto findRoomById(Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);
        if (findRoom.isPresent()) {
            return RoomDetailDto.fromEntity(findRoom.get());
        }
        throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
    }

    /**
     * 방 목록 조건 조회
     */
    @Transactional(readOnly = true)
    public Page<RoomInfoResponseDto> roomSearchCondList(RoomSearchCond roomSearchCond) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        return roomRepository.searchRoom(roomSearchCond, pageRequest);
    }

    /**
     * 방 전체 조회
     */
    @Transactional(readOnly = true)
    public PageResponseDto<RoomInfoResponseDto> roomInfoList() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<Room> roomPage = roomRepository.findAll(pageRequest);

        // Room -> RoomInfoResponseDto 변환
        List<RoomInfoResponseDto> content = roomPage.getContent().stream()
                .map(RoomInfoResponseDto::fromEntity)
                .toList();

        // PageResponseDto 생성
        return new PageResponseDto<>(content, roomPage.getTotalElements(), roomPage.getTotalPages());
    }
}

package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.dto.condition.RoomSearchCond;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {

    Page<RoomInfoResponseDto> searchRoom(RoomSearchCond condition, Pageable pageable);
}

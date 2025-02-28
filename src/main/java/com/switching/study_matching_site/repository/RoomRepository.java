package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom{

    Optional<Room> findRoomByUuid(String uuid);
}

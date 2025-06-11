package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Room;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom{

    Optional<Room> findRoomByUuid(String uuid);

    @Query("SELECT r FROM Room r WHERE r.id =:roomId AND r.roomStatus='ON'")
    Optional<Room> findRoomIdActivity(@Parameter Long roomId);
}

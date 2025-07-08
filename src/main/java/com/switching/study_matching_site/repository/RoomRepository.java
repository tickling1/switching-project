package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Room;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom{

    Optional<Room> findRoomByUuid(String uuid);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT r FROM Room r WHERE r.id =:roomId AND r.roomStatus='ON'")
    Optional<Room> findRoomIdActivity(@Parameter Long roomId);
}

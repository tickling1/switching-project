package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.dto.condition.RoomSearchCond;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoomRepositoryImplTest {

    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private EntityManager em;

    @Test
    void searchTest() {

//        for (int i = 0; i < 100; i++) {
//            Team selectedTeam = i % 2 == 0 ? teamA : teamB;
//            em.persist(new Member("member" + i, i, selectedTeam));
//        }

        for (int i = 0; i < 100; i++) {
            Room room = new Room("테스트 방" + i, RoomStatus.ON, 3, 10,
                    LocalTime.now(), LocalTime.now(), Goal.STUDY, TechSkill.JAVA, 3, Region.SEOUL, OfflineStatus.OFFLINE);
            roomRepository.save(room);
        }
        em.flush();
        em.clear();

        RoomSearchCond condition = new RoomSearchCond();
        condition.setRoomName("테스트");
        condition.setGoal(Goal.STUDY);
        condition.setTechSkill(TechSkill.JAVA);
        condition.setRegion(Region.SEOUL);
        condition.setOfflineStatus(OfflineStatus.OFFLINE);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<RoomInfoResponseDto> roomInfoResponseDtos = roomRepository.searchRoom(condition, pageRequest);
        System.out.println(roomInfoResponseDtos);
    }


}
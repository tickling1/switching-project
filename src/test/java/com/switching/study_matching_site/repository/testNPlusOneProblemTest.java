package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.domain.Room;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class testNPlusOneProblemTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private EntityManager em;

    @Test
    void testNPlusOneProblem() {
        // 1. 방(Room) 생성
        Room room = new Room();
        room.setRoomTitle("Test Room");
        roomRepository.save(room);
        // 2. 참여자(Panticipation) 10명 생성 및 저장

        for (int i= 1; i <= 10; i++) {
            // 2.1 회원(Member) 생성
            Member member = new Member();
            member.setUsername ("member" + i);
            memberRepository.save (member);


            // 2.2 참여(Participation) 생성
            Participation participation = new Participation();
            participation.setRoom(room); // 해당 방에 참여
            participation.setMember(member); // 회원 설정
            participationRepository.save(participation);
        }

        em.flush();
        em.clear();

        System.out.println(". ----------------------------");

        // 3. ROOm을 조회하고 연관된 Pantacapataon들을 조회하여 N+1 문제를 발생시킨다.!
        // 여기서 room.getParticipation_history()를 반복문을 통해 접근
        Room findRoom = roomRepository.findById(room.getId()).orElseThrow() ;
        System.out.println(". ----------------------------");


        // N+1 문제 발생: Room을 가져오고, 연관된 Pacticration들을 반복문을 통해 조회
        for (Participation participation : findRoom.getParticipation_history()) {
            System.out.println("Participation ID: " + participation.getId() + ", Member ID: "+ participation.getMember().getId());
        }
    }
}

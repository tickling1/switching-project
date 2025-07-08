package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.RoleType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
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
        room.setCurrentCount(0);
        roomRepository.save(room);

        for (int i = 0; i < 5; i++) {
            Member member = new Member();
            member.setUsername ("member" + i);
            memberRepository.save (member);

            Participation participation = new Participation(room, RoleType.ADMIN, member);
            participationRepository.save(participation);
        }

        em.flush();
        em.clear();

        System.out.println(". ----------------------------");

        Room findRoom = roomRepository.findById(room.getId()).orElseThrow() ;
        System.out.println(". ----------------------------");
        System.out.println("findRoom = " + findRoom.getParticipation_history().getClass());
        System.out.println(". ----------------------------");

        // 컬렉션 LAZY 일 경우, findRoom.getParticipation_history() 에서 where r.id = ? 이라는 쿼리가 나감
        for (Participation p : findRoom.getParticipation_history()) {
            System.out.println("username = " + p.getJoinDate());
        }
    }

    @Test
    void nPlusOneProblem() {
        // Room 1개
        Room room = new Room();
        room.setCurrentCount(0);
        room.setRoomTitle("Test Room");
        roomRepository.save(room);

        // Member 5명 & Participation 5개
        for (int i = 0; i < 5; i++) {
            Member member = new Member();
            member.setUsername("member" + i);
            memberRepository.save(member);

            Participation participation = new Participation(room, RoleType.USER, member);
            participationRepository.save(participation);
        }

        em.flush();
        em.clear();
        System.out.println("----- 영속성 컨텍스트 정리 -----");
        // 쿼리 1번: Room 조회
        Room findRoom = roomRepository.findById(room.getId()).orElseThrow();

        // 쿼리 1번: Participation 리스트 조회 (LAZY 로딩 때문에 아직 쿼리 안 나감)
        // 처음에 Room을 조회할 때는 participation_history는 초기화되지 않고 프록시 객체(PersistentBag 등)로 남아 있음.
        // 하지만 프록시가 실제로 사용되는 시점, 이런 식으로 컬렉션 내부에 접근하는 순간, Hibernate가 해당 Room의 Participation 리스트를 한 번의 쿼리로 DB에서 조회함
        List<Participation> list = findRoom.getParticipation_history();

        System.out.println("----- N+1 문제 발생 구간 -----");

        // 여기서 N = Participation 수 만큼 추가 쿼리 발생(방에서 참여하고 있는 혹은 방에서 참여했던 회원들의 멤버를 가져올 떄)
        for (Participation p : list) {
            System.out.println("username = " + p.getMember().getUsername());  // ← 여기서 N개의 쿼리 발생
        }
    }
}

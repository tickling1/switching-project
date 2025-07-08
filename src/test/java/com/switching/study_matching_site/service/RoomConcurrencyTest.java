package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.*;
import com.switching.study_matching_site.dto.member.CustomUserDetails;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.MemberRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RoomConcurrencyTest {

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MemberRepository memberRepository;

    private static final int THREAD_COUNT = 10;

    private Room testRoom;


    @BeforeEach
    void setUp() {
        testRoom = roomRepository.save(new Room(
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
                5,
                LocalTime.now(),
                LocalTime.now().plusHours(3).withNano(0),
                Goal.STUDY,
                TechSkill.JAVA,
                3,
                Region.SEOUL,
                OfflineStatus.OFFLINE
        ));
    }

    @Test
    @Rollback(value = false)
    void 동시에_여러명이_입장하면_최대인원만큼만_입장된다() throws InterruptedException, ExecutionException {
        ExecutorService es = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 1; i <= THREAD_COUNT; i++) {
            final int memberId = i;

            tasks.add(() -> {
                Member member = memberRepository.save(new Member(
                        "loginId" + memberId,
                        "wwwww",
                        "testMember" + memberId,
                        LocalDate.now(),
                        "test" + memberId + "@gmail.com",
                        "010-0000-000" + memberId,
                        EnterStatus.OUT
                ));

                // 인증 정보 설정
                setAuthentication(member);

                try {
                    System.out.println(memberId + "번 회원 참여 시도");
                    roomService.participateRoom(testRoom.getId());
                    System.out.println(memberId + "번 회원 참여 성공");
                    return true;
                } catch (InvalidValueException e) {
                    System.out.println(memberId + "번 회원 입장 실패: " + e.getMessage());
                }
                return false;
            });
        }

        // 모든 작업 실행 및 완료 대기
        List<Future<Boolean>> futures = es.invokeAll(tasks);

        for (Future<Boolean> future : futures) {
            Boolean result = future.get();
            System.out.println(result);
        }
        es.shutdown();
        es.awaitTermination(10, TimeUnit.SECONDS);

        Room result = roomRepository.findById(testRoom.getId()).orElseThrow();
        System.out.println("최종 인원 수 = " + result.getCurrentCount());
        assertThat(result.getCurrentCount()).isEqualTo(5);
    }


    private void setAuthentication(Member member) {
        UserDetails userDetails = new CustomUserDetails(member); // 직접 구현한 CustomUserDetails
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
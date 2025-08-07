package com.switching.study_matching_site;

import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.*;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.room.RoomCreateDto;
import com.switching.study_matching_site.jwt.JWTUtil;
import com.switching.study_matching_site.repository.FriendRequestRepository;
import com.switching.study_matching_site.repository.MemberRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import com.switching.study_matching_site.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
// @Profile("test")
@Profile("dev")
@RequiredArgsConstructor
public class DevInitializer implements CommandLineRunner {

    private final MemberService memberService;
    private final ParticipationRepository participationRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;

    /**
     * 개발 환경에서 테스트용 데이터 추가
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {

        Room room = createRoomWithJava();
        roomRepository.save(room);

        Room room2 = createRoomWithPython();
        roomRepository.save(room2);

        // 초기 데이터 세팅
        for (int i = 1; i <= 25; i++) {

            MemberCreateDto memberDto = new MemberCreateDto(
                    "loginId" + i,
                    "wwwww",
                    "testMember" + i,
                    LocalDate.now(),
                    "test" + i + "@gmail.com",
                    "010-0000-000" + i,
                    EnterStatus.OUT
            );

            Long memberId = memberService.joinMember(memberDto);
            Member member = memberRepository.findById(memberId).get();

            if (i > 10) {
                continue;
            }

            if (i % 2 == 0) {
                Participation participation = new Participation(room, RoleType.USER, member);
                participationRepository.save(participation);
            } else {
                Participation participation = new Participation(room2, RoleType.USER, member);
                participationRepository.save(participation);
            }
        }

        // N + 1 문제 확인
        for (int i = 2; i <= 21; i++) {

            // if (i >= 13) {
                // 친구 신청만
                Member member = memberRepository.findById(1L).get();
                Member otherMember = memberRepository.findById(Long.valueOf(i)).get();
                FriendRequest friendRequest = new FriendRequest(member, otherMember);

                friendRequestRepository.save(friendRequest);
            //}
            /*else {
                // 친구 관계 설정
                Member member = memberRepository.findById(1L).get();
                Member otherMember = memberRepository.findById(Long.valueOf(i)).get();
                FriendRequest friendRequest = new FriendRequest(member, otherMember);
                friendRequest.setStatus(RequestStatus.ACCEPTED);
                friendRequestRepository.save(friendRequest);
            }*/
        }
    }

    private static Room createRoomWithJava() {
        Room room = new Room(
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
                6,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.STUDY,
                TechSkill.JAVA,
                3,
                Region.SEOUL,
                OfflineStatus.OFFLINE
        );
        return room;
    }

    private static Room createRoomWithPython() {
        Room room = new Room(
                "파이썬 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
                8,
                LocalTime.now(),
                LocalTime.now().plusHours(2),
                Goal.STUDY,
                TechSkill.PYTHON,
                2,
                Region.INCHEON,
                OfflineStatus.OFFLINE
        );
        return room;
    }
}

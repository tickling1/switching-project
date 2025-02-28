package com.switching.study_matching_site.service;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.EnterStatus;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.room.RoomCreate;
import com.switching.study_matching_site.repository.ParticipationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.*;


import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest(classes = StudyMatchingSiteApplication.class)
@Transactional
class ParticipationServiceTest {

    @Autowired
    private ParticipationService participationService;
    @Autowired
    private ParticipationRepository participationRepository;
    @Autowired
    private MemberService memberService;


    @Test
    @DisplayName("참여 기록 생성")
    @Rollback(value = false)
    public void 참여_기록_생성() throws Exception {
        // given
        Long memberId = memberService.joinMember(getMemberDto());
        Long memberId2 = memberService.joinMember(getMemberDto2());

        // when
        Long participationId = participationService.newParticipation(memberId, getCreateRoomDto());
        participationService.participate(memberId2, participationId);
        Optional<Participation> findParticipation = participationRepository.findById(participationId);

        // then
        assertThat(findParticipation.get().getRoom().getMaxCount()).isEqualTo(6);
        assertThat(findParticipation.get().getRoom().getCurrentCount()).isEqualTo(2);
    }

    private static MemberCreateDto getMemberDto() {
        return MemberCreateDto.builder()
                .loginId("kqk1ds")
                .password("ssambbong123")
                .username("김승우")
                .email("xuni1300@gmail.com")
                .phoneNumber("010-9690-0126")
                .birthDate(LocalDate.now())
                .enterStatus(EnterStatus.OUT)
                .build();
    }

    private static MemberCreateDto getMemberDto2() {
        return MemberCreateDto.builder()
                .loginId("kqk1ds2")
                .password("ssam123")
                .username("햄찌")
                .email("jjj0395@gmail.com")
                .phoneNumber("010-1234-0126")
                .enterStatus(EnterStatus.OUT)
                .birthDate(LocalDate.now())
                .build();
    }

    private static RoomCreate getCreateRoomDto() {
        return RoomCreate.builder()
                .roomTitle("승우가 스터디원을 모집합니다.")
                .maxCount(6)
                .build();
    }

}
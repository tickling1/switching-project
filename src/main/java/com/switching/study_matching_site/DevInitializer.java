package com.switching.study_matching_site;

import com.switching.study_matching_site.service.MemberService;
import com.switching.study_matching_site.service.RoomService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevInitializer {

    private final MemberService memberService;
    private final RoomService roomService;

    /**
     * 개발 환경에서 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        System.out.println("Init method executed");
        memberService.initData(); // 트랜잭션 내에서 실행
        roomService.initData();
    }
}

package com.switching.study_matching_site;

import com.switching.study_matching_site.service.MemberService;
import com.switching.study_matching_site.service.RoomService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudyMatchingSiteApplication {

	private final MemberService memberService;
	private final RoomService roomService;

	public StudyMatchingSiteApplication(MemberService memberService, RoomService roomService) {
		this.memberService = memberService;
		this.roomService = roomService;
	}

	public static void main(String[] args) {
		SpringApplication.run(StudyMatchingSiteApplication.class, args);
	}

	/**
	 * 테스트용 데이터 추가
	 */
	@PostConstruct
	@Profile("dev")
	public void init() {
		memberService.initData(); // 트랜잭션 내에서 실행
		roomService.initData();
	}
}

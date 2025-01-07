package com.switching.study_matching_site;

import com.switching.study_matching_site.service.MemberService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("dev")
public class StudyMatchingSiteApplication {

	private final MemberService memberService;

	public StudyMatchingSiteApplication(MemberService memberService) {
		this.memberService = memberService;
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
	}
}

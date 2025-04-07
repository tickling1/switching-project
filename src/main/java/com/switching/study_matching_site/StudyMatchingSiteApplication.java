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

	public static void main(String[] args) {
		SpringApplication.run(StudyMatchingSiteApplication.class, args);
	}
}

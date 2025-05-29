package com.switching.study_matching_site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StudyMatchingSiteApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudyMatchingSiteApplication.class, args);
	}
}

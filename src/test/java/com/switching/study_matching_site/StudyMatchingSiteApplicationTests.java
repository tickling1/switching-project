package com.switching.study_matching_site;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Profile("dev")
@ActiveProfiles("dev")
class StudyMatchingSiteApplicationTests {

	@Test
	void contextLoads() {
	}

}

package com.switching.study_matching_site;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRateLimiterTest {

    private UserRateLimiter userRateLimiter;

    @BeforeEach
    void setUp() {
        userRateLimiter = new UserRateLimiter();
    }

    @Test
    void testRealTimeRateLimiting() throws InterruptedException {
        String userId = "user1";

        // 1초 동안 5번 요청: 모두 성공
        for (int i = 0; i < 5; i++) {
            assertTrue(userRateLimiter.tryConsume(userId), "토큰 소모 가능");
        }

        // 6번째 요청: 즉시 실패
        assertFalse(userRateLimiter.tryConsume(userId), "토큰 부족으로 실패");

        // 1초 대기 후 다시 요청: 리필되어 성공
        Thread.sleep(1000);

        assertTrue(userRateLimiter.tryConsume(userId), "1초 후 토큰이 리필되어 성공");
    }

}
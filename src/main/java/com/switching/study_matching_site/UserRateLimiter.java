package com.switching.study_matching_site;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserRateLimiter {

    private final ConcurrentHashMap<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Bandwidth 생성
        Bandwidth limit = Bandwidth.builder()
                .capacity(5)
                .refillGreedy(5, Duration.ofSeconds(1))
                .build();

        // 버킷 생성
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    public Bucket resolveBucket(String userId) {
        // 사용자별 버킷 생성 또는 가져오기
        return userBuckets.computeIfAbsent(userId, id -> createNewBucket());
    }

    public boolean tryConsume(String userId) {
        // 토큰 1개 사용 시도
        return resolveBucket(userId).tryConsume(1);
    }
}

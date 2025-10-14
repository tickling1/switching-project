package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.EmptyRegion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmptyRegionRepository extends JpaRepository<EmptyRegion,Long > {


    // 특정 geohash로 조회
    Optional<EmptyRegion> findByGeohash(String geohash);

    // 특정 geohash 삭제
    void deleteByGeohash(String geohash);

    // lastCheckedAt이 기준 시점보다 이전인 것들 조회 (30일 이상 지난 경우 등)
    List<EmptyRegion> findByLastCheckedAtBefore(LocalDateTime threshold);
}

package com.switching.study_matching_site.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmptyRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String geohash; // Geohash 단위로 관리
    private Boolean isEmpty; // 빈 지역 여부, API 호출 결과 없음
    private LocalDateTime lastCheckedAt; // 마지막 확인 시점 (최종 확인된 시간이 얼마되지 않았다면 → 재호출 제한)
}



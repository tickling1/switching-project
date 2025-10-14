package com.switching.study_matching_site.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String placeName;
    private String address;          // address_name
    private String roadAddress;      // road_address_name
    private String phone;
    private Double lat;              // y
    private Double lng;              // x
    private String placeUrl;         // place_url
    private String source;           // "kakao"
    private String geohash;          // geohash

    @Column(nullable = false)
    private Boolean isActive = true; // 현재 운영 중인지?
    private LocalDateTime lastCheckedAt; // 마지막 갱신 확인 시간


    @CreationTimestamp
    private LocalDateTime createdAt;
}

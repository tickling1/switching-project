package com.switching.study_matching_site.domain;

import com.switching.study_matching_site.domain.type.Goal;
import com.switching.study_matching_site.domain.type.OfflineStatus;
import com.switching.study_matching_site.domain.type.Region;
import com.switching.study_matching_site.domain.type.TechSkill;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROFILE_ID")
    private Long id;

    @Column(name = "IS_OFFLINE")
    private OfflineStatus offlineStatus;

    @Enumerated(EnumType.STRING)
    private TechSkill techSkill; // JAVA, PYTHON, KOTLIN, C, JAVASCRIPT

    @Column(name = "DESIRED_LEVEL")
    private Integer desiredLevel; // 1 ~ 3

    @Enumerated(EnumType.STRING)
    @Column(name = "STUDY_GOAL")
    private Goal studyGoal; // 공부, 포트폴리오, 실력향상, 창업

    @Column(name = "START_TIME")
    private LocalTime startTime;

    @Column(name = "END_TIME")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    private Region region; // 특별시, 광역시, 도 단위

    @Column(name = "IS_PRIVATE")
    private Boolean isPrivate; // true면 비공개, false면 공개

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "profile")
    private Member member;

}

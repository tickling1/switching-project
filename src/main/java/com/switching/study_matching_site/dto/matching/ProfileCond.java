package com.switching.study_matching_site.dto.matching;

import com.switching.study_matching_site.domain.type.Goal;
import com.switching.study_matching_site.domain.type.OfflineStatus;
import com.switching.study_matching_site.domain.type.Region;
import com.switching.study_matching_site.domain.type.TechSkill;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ProfileCond {

    private OfflineStatus isOfflineStatus; // true면 오프라인, false면 온라인
    private TechSkill techSkill; // JAVA, PYTHON, KOTLIN, C, JAVASCRIPT
    private Integer desiredLevel; // 1 ~ 3
    private Goal studyGoal; // 공부, 포트폴리오, 실력향상, 창업
    private LocalTime startTime;
    private LocalTime endTime;
    private Region region; // 특별시, 광역시, 도 단위

    public ProfileCond(OfflineStatus isOfflineStatus, TechSkill techSkill, Integer desiredLevel, Goal studyGoal, LocalTime startTime, LocalTime endTime, Region region) {
        this.isOfflineStatus = isOfflineStatus;
        this.techSkill = techSkill;
        this.desiredLevel = desiredLevel;
        this.studyGoal = studyGoal;
        this.startTime = startTime;
        this.endTime = endTime;
        this.region = region;
    }
}

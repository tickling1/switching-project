package com.switching.study_matching_site.dto.profile;

import com.switching.study_matching_site.domain.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileReadDto {


    private OfflineStatus isOffline; // true면 오프라인, false면 온라인

    private TechSkill techSkill; // JAVA, PYTHON, KOTLIN, C, JAVASCRIPT

    private Integer desiredLevel; // 1 ~ 3

    private Goal studyGoal; // 공부, 포트폴리오, 실력향상, 창업

    private LocalTime startTime;

    private LocalTime endTime;

    private Region region; // 특별시, 광역시, 도 단위

    private Boolean isPrivate; // true면 비공개, false면 공개

    public Profile toProfile() {
        Profile entity = new Profile();
        entity.setOfflineStatus(this.isOffline);
        entity.setTechSkill(this.techSkill);
        entity.setDesiredLevel(this.desiredLevel);
        entity.setStudyGoal(this.studyGoal);
        entity.setStartTime(this.startTime);
        entity.setEndTime(this.endTime);
        entity.setRegion(this.region);
        entity.setIsPrivate(this.isPrivate);
        return entity;
    }

    public static ProfileReadDto fromProfile(Profile profile) {

        return new ProfileReadDto(
                profile.getOfflineStatus(),
                profile.getTechSkill(),
                profile.getDesiredLevel(),
                profile.getStudyGoal(),
                profile.getStartTime(),
                profile.getEndTime(),
                profile.getRegion(),
                profile.getIsPrivate()
        );
    }

    @Override
    public String toString() {
        return "ProfileReadDto{" +
                "isOffline=" + isOffline +
                ", techSkill=" + techSkill +
                ", desiredLevel=" + desiredLevel +
                ", studyGoal=" + studyGoal +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", region=" + region +
                ", isPrivate=" + isPrivate +
                '}';
    }
}

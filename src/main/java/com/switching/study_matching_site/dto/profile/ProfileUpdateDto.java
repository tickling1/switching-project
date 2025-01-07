package com.switching.study_matching_site.dto.profile;

import com.switching.study_matching_site.annotation.ValidRegion;
import com.switching.study_matching_site.domain.Goal;
import com.switching.study_matching_site.domain.Profile;
import com.switching.study_matching_site.domain.Region;
import com.switching.study_matching_site.domain.TechSkill;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateDto {

    private Long id;

    private Boolean isOffline; // true면 오프라인, false면 온라인

    @Pattern(regexp = "공부|포트폴리오|실력향상|창업",
            message = "목표는 공부, 포트폴리오, 실력향상, 창업 중 하나여야 합니다.")
    private TechSkill techSkill; // JAVA, PYTHON, KOTLIN, C, JAVASCRIPT

    private Integer desiredLevel; // 1 ~ 3

    @Pattern(regexp = "공부|포트폴리오|실력향상|창업",
            message = "목표는 공부, 포트폴리오, 실력향상, 창업 중 하나여야 합니다.")
    private Goal studyGoal; // 공부, 포트폴리오, 실력향상, 창업

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @ValidRegion(message = "유효한 지역을 선택해야 합니다.")
    private Region region; // 특별시, 광역시, 도 단위

    private Boolean isPrivate; // true면 비공개, false면 공개

    public Profile toProfile() {
        Profile entity = new Profile();
        entity.setIsOffline(this.isOffline);
        entity.setTechSkill(this.techSkill);
        entity.setDesiredLevel(this.desiredLevel);
        entity.setStudyGoal(this.studyGoal);
        entity.setStartTime(this.startTime);
        entity.setEndTime(this.endTime);
        entity.setRegion(this.region);
        entity.setIsPrivate(this.isPrivate);
        return entity;
    }

    public static ProfileUpdateDto fromProfile(Profile profile) {

        return new ProfileUpdateDto(
                profile.getId(),
                profile.getIsOffline(),
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
        return "ProfileUpdateDto{" +
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

package com.switching.study_matching_site.dto.profile;

import com.switching.study_matching_site.annotation.ValidRegion;
import com.switching.study_matching_site.domain.*;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "프로필 생성 요청 DTO")
public class ProfileCreateDto {


    //@NotNull
    @Schema(description = "오프라인 여부", example = "OFFLINE, ONLINEb")
    private OfflineStatus isOffline; // true면 오프라인, false면 온라인

    @Schema(description = "스터디 사용 기술", example = "JAVA, PYTHON, KOTLIN, C, JAVASCRIPT")
    // @Pattern(regexp = "JAVA|PYTHON|KOTLIN|C|JAVASCRIPT", message = "언어는 JAVA, PYTHON, KOTLIN, C, JAVASCRIPT 중 하나여야 합니다.")
    private TechSkill techSkill; // JAVA, PYTHON, KOTLIN, C, JAVASCRIPT

    //@NotNull
    @Schema(description = "희망하는 프로젝트 수준")
    private Integer desiredLevel; // 1 ~ 3

    @Schema(description = "스터디 목적", example = "STUDY, PORTFOLIO, IMPROVE, STARTUP")
    // @Pattern(regexp = "공부|포트폴리오|실력향상|창업", message = "목표는 공부, 포트폴리오, 실력향상, 창업 중 하나여야 합니다.")

    private Goal studyGoal; // 공부, 포트폴리오, 실력향상, 창업

    @Schema(description = "스터디 시작 시간")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Schema(description = "스터디 종료 시간")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "스터디 가능 지역", example = "SEOUL")
    @ValidRegion(message = "유효한 지역을 선택해야 합니다.")
    private Region region; // 특별시, 광역시, 도 단위

    @Schema(description = "프로필 공개 여부")
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

    public static ProfileCreateDto fromProfile(Profile profile) {

        return new ProfileCreateDto(
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
        return "ProfileDto{" +
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

package com.switching.study_matching_site.dto.condition;

import com.switching.study_matching_site.domain.type.Goal;
import com.switching.study_matching_site.domain.type.OfflineStatus;
import com.switching.study_matching_site.domain.type.Region;
import com.switching.study_matching_site.domain.type.TechSkill;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "스터디 방 검색 조건 (필터링)")
public class RoomSearchCond {

    @Schema(description = "스터디 방 제목 검색어", example = "자바 스터디")
    private String roomName;

    @Schema(description = "기술 스택", example = "JAVA",
            allowableValues = {"JAVA", "PYTHON", "KOTLIN", "C", "JAVASCRIPT"})
    private TechSkill techSkill;

    @Schema(description = "스터디 목적", example = "STUDY",
            allowableValues = {"STUDY", "PORTFOLIO", "IMPROVE", "STARTUP"})
    private Goal goal;

    @Schema(description = "활동 지역", example = "SEOUL")
    private Region region;

    @Schema(description = "온/오프라인 여부", example = "OFFLINE",
            allowableValues = {"OFFLINE", "ONLINE"})
    private OfflineStatus offlineStatus;

    public RoomSearchCond(String roomName, TechSkill techSkill, Goal goal, Region region, OfflineStatus offlineStatus) {
        this.roomName = roomName;
        this.techSkill = techSkill;
        this.goal = goal;
        this.region = region;
        this.offlineStatus = offlineStatus;
    }

    public RoomSearchCond() {
    }
}

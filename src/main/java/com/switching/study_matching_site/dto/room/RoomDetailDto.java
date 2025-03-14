package com.switching.study_matching_site.dto.room;

import com.switching.study_matching_site.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalTime;

@Builder
@Schema(name = "방 상세 정보 응답 DTO")
public class RoomDetailDto {

    private Long id;

    @Schema(name = "방 제목")
    private String roomTitle;

    @Schema(name = "방 UUID")
    private String uuid;

    @Schema(name = "방 상태")
    private RoomStatus roomStatus;

    @Schema(name = "현재 인원 수")
    private Integer currentCount;

    @Schema(name = "최대 인원 수")
    private Integer maxCount;
    
    @Schema(name = "스터디 시작 시간")
    private LocalTime startTime;

    @Schema(name = "스터디 종료 시간")
    private LocalTime endTime;

    @Schema(description = "스터디 목적", example = "STUDY, PORTFOLIO, IMPROVE, STARTUP")
    private Goal projectGoal;

    @Schema(description = "스터디 사용 기술", example = "JAVA, PYTHON, KOTLIN, C, JAVASCRIPT")
    private TechSkill techSkill;

    @Schema(description = "프로젝트 수준")
    private Integer projectLevel;

    @Schema(description = "스터디 가능 지역", example = "SEOUL")
    private Region projectRegion;

    @Schema(description = "오프라인 여부", example = "OFFLINE, ONLINEb")
    private OfflineStatus offlineStatus;

    public static RoomDetailDto fromEntity(Room room) {
        return RoomDetailDto.builder()
                .id(room.getId())
                .roomTitle(room.getRoomTitle())
                .uuid(room.getUuid())
                .roomStatus(room.getRoomStatus())
                .currentCount(room.getCurrentCount())
                .maxCount(room.getMaxCount())
                .startTime(room.getStartTime())
                .endTime(room.getEndTime())
                .projectGoal(room.getProjectGoal())
                .techSkill(room.getTechSkill())
                .projectLevel(room.getProjectLevel())
                .projectRegion(room.getProjectRegion())
                .offlineStatus(room.getOfflineStatus())
                .build();
    }

    @Override
    public String toString() {
        return "RoomDetail{" +
                "projectRegion=" + projectRegion +
                ", id=" + id +
                ", roomTitle='" + roomTitle + '\'' +
                ", uuid='" + uuid + '\'' +
                ", roomStatus=" + roomStatus +
                ", currentCount=" + currentCount +
                ", maxCount=" + maxCount +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", projectGoal=" + projectGoal +
                ", techSkill=" + techSkill +
                ", projectLevel=" + projectLevel +
                ", offlineStatus=" + offlineStatus +
                '}';
    }
}

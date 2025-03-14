package com.switching.study_matching_site.dto.room;

import com.switching.study_matching_site.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalTime;
import java.util.UUID;

@Builder
@Schema(name = "방 생성 요청 DTO")
public class RoomCreateDto {

    @Schema(description = "방 제목")
    private String roomTitle;

    @Schema(description = "방 상태", defaultValue = "ON")
    private RoomStatus roomStatus;
    
    @Schema(description = "현재 인원 수", defaultValue = "0")
    private Integer currentCount;
    
    @Schema(description = "최대 인원 수")
    private Integer maxCount;

    @Schema(description = "스터디 시작 시간")
    private LocalTime startTime;

    @Schema(description = "스터디 종료 시간")
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

    /**
     * DTO -> ENTITY 변환 메소드
     */
    public Room toEntity() {
        Room room = new Room();
        room.setRoomTitle(roomTitle);
        room.setRoomStatus(RoomStatus.ON);
        room.setCurrentCount(0);
        room.setUuid(UUID.randomUUID().toString());
        room.setMaxCount(maxCount);
        room.setStartTime(startTime);
        room.setEndTime(endTime);
        room.setProjectGoal(projectGoal);
        room.setTechSkill(techSkill);
        room.setProjectLevel(projectLevel);
        room.setProjectRegion(projectRegion);
        room.setOfflineStatus(offlineStatus);
        return room;
    }
}

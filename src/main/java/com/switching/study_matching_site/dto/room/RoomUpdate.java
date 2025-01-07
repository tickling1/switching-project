package com.switching.study_matching_site.dto.room;

import com.switching.study_matching_site.domain.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@Builder
@Getter
@Setter
public class RoomUpdate {

    private String roomTitle;
    private Integer maxCount;
    private LocalTime startTime;
    private LocalTime endTime;
    private Goal projectGoal;
    private TechSkill techSkill;
    private Integer projectLevel;
    private Region projectRegion;
    private OfflineStatus offlineStatus;

    /**
     * DTO -> ENTITY 변환 메소드
     */
    public Room toEntity() {
        Room room = new Room();
        room.setRoomTitle(roomTitle);
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

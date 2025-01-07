package com.switching.study_matching_site.dto.room;

import com.switching.study_matching_site.domain.*;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public class RoomDetail {

    private Long id;

    private String roomTitle;

    private String uuid;

    private RoomStatus roomStatus;

    private Integer currentCount;

    private Integer maxCount;

    private LocalTime startTime;

    private LocalTime endTime;

    private Goal projectGoal;

    private TechSkill techSkill;

    private Integer projectLevel;

    private Region projectRegion;

    private OfflineStatus offlineStatus;

    public static RoomDetail fromEntity(Room room) {
        return RoomDetail.builder()
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

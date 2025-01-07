package com.switching.study_matching_site.dto.room;

import com.switching.study_matching_site.domain.Room;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
public class RoomInfo {

    private String roomTitle;

    private Integer currentCount;

    private Integer maxCount;

    @Builder.Default
    private List<RoomInfo> roomInfoList = new ArrayList<>();

    public static RoomInfo fromEntity(Room room) {
        return RoomInfo.builder()
                .roomTitle(room.getRoomTitle())
                .currentCount(room.getCurrentCount())
                .maxCount(room.getMaxCount())
                .build();
    }

    @Override
    public String toString() {
        return "RoomInfo{" +
                "roomTitle='" + roomTitle + '\'' +
                ", currentCount=" + currentCount +
                ", maxCount=" + maxCount +
                ", roomInfoList=" + roomInfoList +
                '}';
    }
}

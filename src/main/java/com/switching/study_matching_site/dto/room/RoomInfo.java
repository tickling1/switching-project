package com.switching.study_matching_site.dto.room;

import com.switching.study_matching_site.domain.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Builder
@Schema(name = "방 목록 응답 DTO")
public class RoomInfo {

    @Schema(description = "방 제목")
    private String roomTitle;

    @Schema(description = "방 현재 인원")
    private Integer currentCount;

    @Schema(description = "방 최대 인원")
    private Integer maxCount;

    @Builder.Default
    @Schema(description = "방 목록")
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

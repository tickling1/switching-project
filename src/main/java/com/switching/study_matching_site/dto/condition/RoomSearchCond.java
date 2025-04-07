package com.switching.study_matching_site.dto.condition;

import com.switching.study_matching_site.domain.type.Goal;
import com.switching.study_matching_site.domain.type.OfflineStatus;
import com.switching.study_matching_site.domain.type.Region;
import com.switching.study_matching_site.domain.type.TechSkill;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoomSearchCond {
    private String roomName;
    private TechSkill techSkill;
    private Goal goal;
    private Region region;
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

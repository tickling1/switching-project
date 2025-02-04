package com.switching.study_matching_site.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Room extends BaseTimeEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ROOM_ID")
    private Long id;

    @Column(name = "ROOM_TITLE")
    private String roomTitle;

    @Column(name = "ROOM_UUID", unique = true)
    private String uuid;

    @Column(name = "ROOM_STATUS")
    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @Column(name = "CURRENT_COUNT")
    private Integer currentCount;

    @Column(name = "MAX_COUNT")
    private Integer maxCount;

    @Column(name = "START_TIME")
    private LocalTime startTime;

    @Column(name = "END_TIME")
    private LocalTime endTime;

    @Column(name = "PROJECT_GOAL")
    @Enumerated(EnumType.STRING)
    private Goal projectGoal;

    @Column(name = "PROJECT_SKILL")
    @Enumerated(EnumType.STRING)
    private TechSkill techSkill;

    @Column(name = "PROJECT_LEVEL")
    private Integer projectLevel;

    @Column(name = "PROJECT_REGION")
    @Enumerated(EnumType.STRING)
    private Region projectRegion;

    @Column(name = "OFFLINE_STATUS")
    @Enumerated(EnumType.STRING)
    private OfflineStatus offlineStatus; // true면 오프라인 false면 온라인

    @OneToMany(mappedBy = "room")
    private List<Participation> participation_history = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notice> notice_history = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chat_history = new ArrayList<>();

    public Room(String roomTitle,
                RoomStatus roomStatus,
                Integer currentCount,
                Integer maxCount,
                LocalTime startTime,
                LocalTime endTime,
                Goal projectGoal,
                TechSkill techSkill,
                Integer projectLevel,
                Region projectRegion,
                OfflineStatus offlineStatus) {
        this.roomTitle = roomTitle;
        this.roomStatus = roomStatus;
        this.currentCount = currentCount;
        this.maxCount = maxCount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.projectGoal = projectGoal;
        this.techSkill = techSkill;
        this.projectLevel = projectLevel;
        this.projectRegion = projectRegion;
        this.offlineStatus = offlineStatus;
    }

    public Room() {

    }
}

package com.switching.study_matching_site.domain;

import com.switching.study_matching_site.domain.type.EnterStatus;
import com.switching.study_matching_site.domain.type.RoleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Participation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTICIPATION_ID")
    private Long id;

    @Column(name = "ROLE_TYPE")
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "JOIN_DATE")
    private LocalDateTime joinDate;

    @Column(name = "LEAVE_DATE")
    private LocalDateTime leaveDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    // 방 생성 or 방 참여(편의 메서드)
    public Participation(Room room, RoleType roleType, Member member) {
        this.roleType = roleType;
        this.joinDate = LocalDateTime.now();
        this.leaveDate = null;
        this.room = room;
        this.member = member;

        member.setEnterStatus(EnterStatus.ENTER);
        room.getParticipation_history().add(this);
        room.setCurrentCount(room.getCurrentCount() + 1);
    }

    public Participation() {
    }
    
}

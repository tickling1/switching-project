package com.switching.study_matching_site.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Notice {

    @Id
    @GeneratedValue
    @Column(name = "NOTICE_ID")
    private Long id;

    @Column(name = "NOTICE_TITLE")
    private String noticeTitle;

    @Lob
    @Column(name = "NOTICE_CONTENT")
    private String noticeContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    private LocalDateTime createdAt;
}

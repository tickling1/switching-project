package com.switching.study_matching_site.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Chat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHAT_ID")
    private Long id;

    @Column(name = "CHAT_CONTENT")
    private String chatContent;

    @Column(name = "CHAT_DATE")
    private LocalDateTime chatDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_ID")
    private Room room;

    private String writer;
}

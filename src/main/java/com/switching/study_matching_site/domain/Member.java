package com.switching.study_matching_site.domain;

import com.switching.study_matching_site.domain.type.EnterStatus;
import com.switching.study_matching_site.domain.type.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long id;

    @Column(name = "LOGIN_ID")
    private String loginId;

    private String password;

    private String username;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "ENTER_STATUS")
    @Enumerated(EnumType.STRING)
    private EnterStatus enterStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROFILE_ID")
    private Profile profile;

    @OneToMany(mappedBy = "member")
    private List<Participation> participation_history = new ArrayList<>();

    // 내가 보낸 친구 요청 목록
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequest> sentRequests = new ArrayList<>();

    // 내가 받은 친구 요청 목록
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FriendRequest> receivedRequests = new ArrayList<>();

    // 수락된 친구 요청 목록
    @OneToMany(mappedBy = "sender")
    private List<FriendRequest> friends = new ArrayList<>();

    // 방 만들기
    public void participate(Participation participation) {
        participation.setMember(this);
        participation_history.add(participation);
        this.enterStatus = EnterStatus.ENTER;
    }

    // 프로필 추가 (편의 메서드)
    public void addProfile(Profile profile) {
        this.profile = profile;
        profile.setMember(this);
    }

    public void addSentRequest(FriendRequest request) {
        sentRequests.add(request);
        request.setSender(this);
        //
    }

    public void addReceivedRequest(FriendRequest request) {
        receivedRequests.add(request);
        request.setReceiver(this);
    }

    public void removeSentRequest(FriendRequest request) {
        sentRequests.remove(request);
        request.setSender(null); // 연관관계 끊기
    }

    public void removeReceivedRequest(FriendRequest request) {
        receivedRequests.remove(request);
        request.setReceiver(null); // 연관관계 끊기
    }

}

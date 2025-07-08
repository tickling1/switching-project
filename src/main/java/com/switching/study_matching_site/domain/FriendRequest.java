package com.switching.study_matching_site.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.switching.study_matching_site.domain.type.RequestStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FriendRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ID")
    private Member sender;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVER_ID")
    private Member receiver;

    @Column(name = "REQUEST_STATUS")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;


    public FriendRequest(Member sender, Member receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = RequestStatus.PENDING;
    }

    // 친구 요청 수락
    public void accept() {
        this.status = RequestStatus.ACCEPTED;
    }

    // 친구 요청 거절
    public void reject() {
        this.status = RequestStatus.REJECTED;
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "sender=" + sender +
                ", receiver=" + receiver +
                ", status=" + status +
                '}';
    }
}

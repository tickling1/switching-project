package com.switching.study_matching_site.dto.friend;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FriendRequestReceiverDto {

    private Long requestId;
    private Long receiverId;
    private String receiverUsername;

    @QueryProjection
    public FriendRequestReceiverDto(Long requestId, Long receiverId, String receiverUsername) {
        this.requestId = requestId;
        this.receiverId = receiverId;
        this.receiverUsername = receiverUsername;
    }

    @Override
    public String toString() {
        return "FriendRequestReceiverDto{" +
                "requestId=" + requestId +
                ", receiverId=" + receiverId +
                ", receiverUsername='" + receiverUsername + '\'' +
                '}';
    }


}

package com.switching.study_matching_site.dto.friend;

import com.switching.study_matching_site.domain.type.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "친구 신청 응답 DTO")
public class FriendRequestResponse {
    private Long requestId;
    private Long receiverId;
    private RequestStatus status; // "PENDING"

    @Override
    public String toString() {
        return "FriendRequestResponse{" +
                "requestId=" + requestId +
                ", receiverId=" + receiverId +
                ", status=" + status +
                '}';
    }
}

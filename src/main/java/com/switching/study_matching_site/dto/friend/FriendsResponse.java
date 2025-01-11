package com.switching.study_matching_site.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@Getter
@Setter
@Schema(name = "친구 요청 응답 DTO")
public class FriendsResponse {

    @Builder.Default
    @Schema(name = "친구 이름 목록")
    private List<String> friendNames = new ArrayList<>();

    // 생성자
    public FriendsResponse(List<String> friendNames) {
        this.friendNames = friendNames;
    }

    @Override
    public String toString() {
        return "FriendsResponse{" +
                "friendNames=" + friendNames +
                '}';
    }
}

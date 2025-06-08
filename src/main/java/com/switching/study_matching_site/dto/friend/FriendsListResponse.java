package com.switching.study_matching_site.dto.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Schema(name = "친구 목록 요청 응답 DTO")
public class FriendsListResponse {

    @Schema(name = "친구 이름 목록")
    private Map<Long, String> friends = new HashMap<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FriendsListResponse{\n");
        int size = friends.size();
        int i = 0;
        for (Map.Entry<Long, String> entry : friends.entrySet()) {
            sb.append("[friendID:").append(entry.getKey())
                    .append(", username:").append(entry.getValue())
                    .append("]");
            i++;
            if (i < size) {
                sb.append(",\n"); // 마지막 항목이 아니면 콤마+줄바꿈 추가
            }
        }
        sb.append("\n}");
        return sb.toString();
    }
}

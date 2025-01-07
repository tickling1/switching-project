package com.switching.study_matching_site.dto.friend;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@Getter
@Setter
public class FriendsResponse {

    @Builder.Default
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

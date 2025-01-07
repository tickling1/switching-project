package com.switching.study_matching_site.dto.member;

import com.switching.study_matching_site.domain.EnterStatus;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberReadDto {

    private String username;

    private LocalDate birthDate;

    private String email;

    private String phoneNumber;

    private EnterStatus enterStatus;

    private Profile profile;

    @Builder.Default
    private List<FriendRequest> friendRequests = new ArrayList<>();

    public static MemberReadDto fromEntity(Member entity) {

        return MemberReadDto.builder()
                .birthDate(entity.getBirthDate())
                .email(entity.getEmail())
                .friendRequests(entity.getFriends())
                .username(entity.getUsername())
                .phoneNumber(entity.getPhoneNumber())
                .profile(entity.getProfile())
                .build();

    }

    @Override
    public String toString() {
        return "MemberReadDto{" +
                "username='" + username + '\'' +
                ", birthDate=" + birthDate +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", enterStatus=" + enterStatus +
                '}';
    }
}

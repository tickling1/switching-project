package com.switching.study_matching_site.dto.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.switching.study_matching_site.domain.EnterStatus;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Profile;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "회원 정보 응답 DTO")
public class MemberReadDto {

    @Schema(description = "회원 이름")
    private String username;
    
    @Schema(description = "회원 생년월일", example = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "회원 이메일", example = "example@gmail.com")
    private String email;
    
    @Schema(description = "회원 핸드폰 번호")
    private String phoneNumber;

    @Schema(description = "회원 프로필")
    private Profile profile;

    @Builder.Default
    @JsonIgnore
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
                ", phoneNumber='" + phoneNumber + '}';
    }
}

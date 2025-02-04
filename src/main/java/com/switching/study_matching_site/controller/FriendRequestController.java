package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.dto.friend.FriendsResponse;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FRIEND-REQUEST", description = "친구 요청 API")
@RestController
@AllArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    // 내 친구 보기
    @Operation(summary = "내 친구 보기", description = "내 친구목록을 가져옵니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "친구 이름 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendsResponse.class)
                            )
                    )
            })
    @GetMapping("/members/{memberId}/friends")
    public String getFriendList(@Parameter(description = "members의 id", in = ParameterIn.PATH)
                                    @PathVariable Long memberId) {
        FriendsResponse friendsResponse = friendRequestService.myFriends(memberId);
        return friendsResponse.toString();
    }

    // 친구 신청 보내기
    @Operation(summary = "친구 신청 보내기", description = "친구 신청을 보냅니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "친구 신청 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendsResponse.class)
                            )
                    )
            })
    @Parameters({
            @Parameter(name = "senderId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "receiverId", description = "보내는 member의 id", in = ParameterIn.PATH)
    })
    @PostMapping("/members/{memberId}/{receiverId}/request")
    public String addFriendRequest(@PathVariable Long memberId, @PathVariable Long receiverId ) {
        return friendRequestService.requestFriend(memberId, receiverId);
    }

    // 친구 신청 받기
    @Operation(summary = "친구 신청 받기", description = "친구 신청을 받습니다.")
    @Parameters({
            @Parameter(name = "senderId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "receiverId", description = "보내는 member의 id", in = ParameterIn.PATH)
    })
    @PostMapping("/members/{senderId}/{receiverId}/accept")
    public String addFriend(@PathVariable Long senderId, @PathVariable Long receiverId) {
        friendRequestService.acceptFriend(senderId, receiverId);
        return "친구 상태입니다.";
    }

    // 친구 삭제
    @Operation(summary = "친구 삭제하기", description = "친구를 관계를 끊습니다.")
    @Parameters({
            @Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "friendId", description = "친구의 id", in = ParameterIn.PATH)
    })
    @PostMapping("/members/{memberId}/friends/{friendId}")
    public String removeFriend(@PathVariable Long memberId, @PathVariable Long friendId) {
        friendRequestService.deleteFriend(memberId, friendId);
        return "친구 삭제 완료";
    }
    
    // 내 신청 목록 보기
    @Operation(summary = "친구 신청 목록 보기", description = "내가 신청한 목록을 보여줍니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "친구 요청 목록 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendsResponse.class)
                            )
                    )
            })
    @GetMapping("/members/{memberId}/friends/request")
    public String getFriendRequests(@Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH)
                                        @PathVariable Long memberId) {
        FriendsResponse friendsResponse = friendRequestService.myRequests(memberId);
        return friendsResponse.toString();
    }

    // 내가 받은 친구 요청 목록 보기
    @Operation(summary = "친구 요청 목록 보기", description = "상대방이 나에게 친구를 신청한 목록을 보여줍니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "친구 요청 목록 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendsResponse.class)
                            )
                    )
            })
    @GetMapping("/members/{memberId}/friends/receive")
    public String getFriendReceive(@Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH)
                                       @PathVariable Long memberId) {
        FriendsResponse friendsResponse = friendRequestService.myReceived(memberId);
        return friendsResponse.toString();
    }
}

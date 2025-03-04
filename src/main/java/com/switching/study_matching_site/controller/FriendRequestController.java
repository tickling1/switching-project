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
    @GetMapping("/friends")
    public String getFriendList() {
        FriendsResponse friendsResponse = friendRequestService.myFriends();
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
    @Parameter(name = "receiverId", description = "보내는 member의 id", in = ParameterIn.PATH)
    @PostMapping("/friends/{receiverId}/request")
    public String addFriendRequest(@PathVariable Long receiverId ) {
        return friendRequestService.requestFriend(receiverId);
    }

    // 친구 신청 받기
    @Parameter(name = "receiverId", description = "보내는 member의 id", in = ParameterIn.PATH)
    @PostMapping("/friends/{receiverId}/accept")
    public String addFriend( @PathVariable Long receiverId) {
        friendRequestService.acceptFriend(receiverId);
        return "친구 상태입니다.";
    }

    // 친구 삭제
    @Parameter(name = "friendId", description = "친구의 id", in = ParameterIn.PATH)
    @PostMapping("/friends/{friendId}")
    public String removeFriend( @PathVariable Long friendId) {
        friendRequestService.deleteFriend(friendId);
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
    @GetMapping("/friends/requests")
    public String getFriendRequests() {
        FriendsResponse friendsResponse = friendRequestService.myRequests();
        return friendsResponse.toString();
    }

    // 내가 받은 친구 요청 목록 보기
    @GetMapping("/friends/receives")
    public String getFriendReceive() {
        FriendsResponse friendsResponse = friendRequestService.myReceived();
        return friendsResponse.toString();
    }
}

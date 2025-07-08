package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.friend.FriendRequestDto;
import com.switching.study_matching_site.dto.friend.FriendRequestReceiverDto;
import com.switching.study_matching_site.dto.friend.FriendRequestResponse;
import com.switching.study_matching_site.dto.friend.FriendsListResponse;
import com.switching.study_matching_site.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FRIEND-REQUEST", description = "친구 요청 API")
@RestController
@AllArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    // 친구 신청 보내기
    @Operation(summary = "친구 신청 보내기", description = "친구 신청을 보냅니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "친구 신청 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendsListResponse.class)
                            )
                    )
            })
    @Parameter(name = "receiverId", description = "친구 신청할 회원의 ID", in = ParameterIn.PATH)
    @PostMapping("/friends/{receiverId}/request")
    public ResponseEntity<FriendRequestResponse> addFriendRequest(@PathVariable Long receiverId ) {
        FriendRequestResponse response = friendRequestService.requestFriend(receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 친구 신청 받기
    @Parameter(name = "senderId", description = "보낸 회원의 ID", in = ParameterIn.PATH)
    @PostMapping("/friends/{senderId}/accept")
    public ResponseEntity<String> addFriend( @PathVariable Long senderId) {
        friendRequestService.acceptFriend(senderId);
        return ResponseEntity.ok("친구 요청을 수락했습니다.");
    }

    // 친구 거절
    @Parameter(name = "senderId", description = "보낸 회원의 ID", in = ParameterIn.PATH)
    @DeleteMapping("/friends/{senderId}/reject")
    public ResponseEntity<String> rejectFriend( @PathVariable Long senderId) {
        friendRequestService.rejectFriend(senderId);
        return ResponseEntity.ok("친구 거절을 완료했습니다.");
    }

    // 친구 삭제
    @Parameter(name = "removeMemberId", description = "삭제하고 싶은 친구의 ID", in = ParameterIn.PATH)
    @DeleteMapping("/friends/{removeMemberId}")
    public ResponseEntity<String> removeFriend( @PathVariable Long removeMemberId) {
        friendRequestService.deleteFriend(removeMemberId);
        return ResponseEntity.ok("친구가 삭제되었습니다.");
    }

    // 내 친구 보기
    @Operation(summary = "내 친구 보기", description = "내 친구목록을 가져옵니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "친구 이름 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendsListResponse.class)
                            )
                    )
            })
    @GetMapping("/friends")
    public ResponseEntity<FriendsListResponse> getFriendList() {
        FriendsListResponse friendsListResponse = friendRequestService.myFriends();
        return ResponseEntity.status(HttpStatus.OK).body(friendsListResponse);
    }

    // 내 신청 목록 보기
    @Operation(summary = "친구 신청 목록 보기", description = "내가 신청한 목록을 보여줍니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "친구 요청 목록 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FriendsListResponse.class)
                            )
                    )
            })
    @GetMapping("/friends/requests")
    public ResponseEntity<FriendsListResponse> getFriendRequests() {
        FriendsListResponse friendsListResponse = friendRequestService.myRequests();
        return ResponseEntity.status(HttpStatus.OK).body(friendsListResponse);
    }

    @GetMapping("/friends/requests2")
    public ResponseEntity<List<FriendRequestReceiverDto>> getFriendRequests2() {
        List<FriendRequestReceiverDto> friendRequestReceiverDtos = friendRequestService.myRequests2();
        return ResponseEntity.status(HttpStatus.OK).body(friendRequestReceiverDtos);
    }

    // 내가 받은 친구 요청 목록 보기
    @GetMapping("/friends/receives")
    public ResponseEntity<FriendsListResponse> getFriendReceive() {
        FriendsListResponse friendsListResponse = friendRequestService.myReceived();
        return ResponseEntity.status(HttpStatus.OK).body(friendsListResponse);

    }
}

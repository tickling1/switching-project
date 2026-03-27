package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.friend.FriendRequestReceiverDto;
import com.switching.study_matching_site.dto.friend.FriendRequestResponse;
import com.switching.study_matching_site.dto.friend.FriendsListResponse;
import com.switching.study_matching_site.service.FriendRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "친구 신청 보내기", description = "상대방의 ID를 받아 친구 요청을 전송합니다. 이미 신청했거나 친구인 경우 예외가 발생할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "신청 성공", content = @Content(schema = @Schema(implementation = FriendRequestResponse.class))),
            @ApiResponse(responseCode = "400", description = "자기 자신에게 신청하거나 이미 처리된 요청인 경우")
    })
    @PostMapping("/friends/{receiverId}/request")
    public ResponseEntity<FriendRequestResponse> addFriendRequest(@PathVariable Long receiverId ) {
        FriendRequestResponse response = friendRequestService.requestFriend(receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 친구 신청 수락
    @Operation(summary = "친구 신청 수락", description = "나에게 온 친구 요청을 수락하여 정식 친구 상태로 변경합니다.")
    @PostMapping("/friends/{senderId}/accept")
    public ResponseEntity<String> addFriend( @PathVariable Long senderId) {
        friendRequestService.acceptFriend(senderId);
        return ResponseEntity.ok("친구 요청을 수락했습니다.");
    }

    // 친구 거절
    @Operation(summary = "친구 신청 거절", description = "나에게 온 친구 요청을 거절하고 요청 데이터를 삭제합니다.")    @DeleteMapping("/friends/{senderId}/reject")
    public ResponseEntity<String> rejectFriend( @PathVariable Long senderId) {
        friendRequestService.rejectFriend(senderId);
        return ResponseEntity.ok("친구 거절을 완료했습니다.");
    }

    // 친구 삭제
    @Operation(summary = "친구 삭제 (끊기)", description = "기존에 맺어진 친구 관계를 해제합니다.")    @DeleteMapping("/friends/{removeMemberId}")
    public ResponseEntity<String> removeFriend( @PathVariable Long removeMemberId) {
        friendRequestService.deleteFriend(removeMemberId);
        return ResponseEntity.ok("친구가 삭제되었습니다.");
    }

    // 내 친구 보기
    @Operation(summary = "내 친구 목록 조회", description = "현재 나와 친구 상태인 회원들의 목록을 반환합니다.",            responses = {
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
    @Operation(summary = "내가 보낸 친구 요청 목록", description = "현재 내가 상대방에게 보냈으나, 아직 수락되지 않은 요청 리스트를 보여줍니다.",            responses = {
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

    @Operation(summary = "내가 보낸 친구 요청 상세 목록", description = "보낸 요청 목록을 DTO 형태로 상세하게 조회합니다.")
    @GetMapping("/friends/requests/detail")
    public ResponseEntity<List<FriendRequestReceiverDto>> getFriendRequests2() {
        List<FriendRequestReceiverDto> friendRequestReceiverDtos = friendRequestService.myRequests2();
        return ResponseEntity.status(HttpStatus.OK).body(friendRequestReceiverDtos);
    }

    // 내가 받은 친구 요청 목록 보기
    @Operation(summary = "나에게 온 친구 요청 목록", description = "다른 사람이 나에게 보낸 친구 신청 리스트입니다. 여기서 수락/거절을 결정할 수 있습니다.")
    @GetMapping("/friends/receives")
    public ResponseEntity<FriendsListResponse> getFriendReceive() {
        FriendsListResponse friendsListResponse = friendRequestService.myReceived();
        return ResponseEntity.status(HttpStatus.OK).body(friendsListResponse);

    }
}

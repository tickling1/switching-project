package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.dto.friend.FriendsResponse;
import com.switching.study_matching_site.service.FriendRequestService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studywithmatching.com")
@AllArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    // 내 친구 보기
    @GetMapping("/members/{memberId}/friends")
    public String getFriendList(@PathVariable Long memberId) {
        FriendsResponse friendsResponse = friendRequestService.myFriends(memberId);
        return friendsResponse.toString();
    }

    // 친구 신청 보내기
    @PostMapping("/members/{memberId}/{receiverId}/request")
    public String addFriendRequest(@PathVariable Long memberId, @PathVariable Long receiverId ) {
        return friendRequestService.requestFriend(memberId, receiverId);
    }

    // 친구 신청 받기
    @PostMapping("/members/{senderId}/{receiverId}/accept")
    public String addFriend(@PathVariable Long senderId, @PathVariable Long receiverId) {
        friendRequestService.acceptFriend(senderId, receiverId);
        return "친구 상태입니다.";
    }

    // 친구 삭제
    @PostMapping("/members/{memberId}/friends/{friendId}")
    public String removeFriend(@PathVariable Long memberId, @PathVariable Long friendId) {
        friendRequestService.deleteFriend(memberId, friendId);
        return "친구 삭제 완료";
    }
    
    // 내 신청 목록 보기
    @GetMapping("/members/{memberId}/friends/request")
    public String getFriendRequests(@PathVariable Long memberId) {
        FriendsResponse friendsResponse = friendRequestService.myRequests(memberId);
        return friendsResponse.toString();
    }

    // 내가 받은 친구 요청 목록 보기
    @GetMapping("/members/{memberId}/friends/receive")
    public String getFriendReceive(@PathVariable Long memberId) {
        FriendsResponse friendsResponse = friendRequestService.myReceived(memberId);
        return friendsResponse.toString();
    }


}

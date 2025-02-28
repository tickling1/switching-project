package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.RequestStatus;
import com.switching.study_matching_site.dto.friend.FriendsResponse;
import com.switching.study_matching_site.repository.FriendRequestRepository;
import com.switching.study_matching_site.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final MemberRepository memberRepository;

    // 친구 신청 보내기 - 친구 신청 받은 사람 아이디 반환
    public String requestFriend(Long senderId, Long receiverId) {
        Optional<Member> sender = memberRepository.findById(senderId);
        Optional<Member> receiver = memberRepository.findById(receiverId);

        if (sender.isPresent() && receiver.isPresent()) {
            FriendRequest friendRequest = new FriendRequest(sender.get(), receiver.get());
            sender.get().addSentRequest(friendRequest);
            receiver.get().addReceivedRequest(friendRequest);
            friendRequestRepository.save(friendRequest);
            return receiver.get().getLoginId();
        }
        return null;
    }

    // 친구 신청 받기 --
    public void acceptFriend(Long senderId, Long receiverId) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(senderId, receiverId);

        if (friendRequest.isPresent()) {
            FriendRequest friendship = friendRequest.get();
            friendship.setStatus(RequestStatus.ACCEPTED);
        }
    }

    // 친구 거절
    public void rejectFriend(Long senderId, Long receiverId) {
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(senderId, receiverId);
        if (friendRequest.isPresent()) {
            FriendRequest friendship = friendRequest.get();
            friendship.setStatus(RequestStatus.REJECTED);
        }
    }

    // 친구 삭제
    public void deleteFriend(Long senderId, Long receiverId) {
        Member sender = memberRepository.findById(senderId).orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        Member receiver = memberRepository.findById(receiverId).orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        // 친구 요청을 찾아서 삭제
        Optional<FriendRequest> friendship = friendRequestRepository.findBySenderAndReceiver(senderId, receiverId);
        if (friendship.isPresent()) {
            FriendRequest friendRequest = friendship.get();
            friendRequestRepository.delete(friendRequest);  // 친구 요청 삭제
            sender.getSentRequests().remove(friendRequest);  // 보낸 요청에서 삭제
            receiver.getReceivedRequests().remove(friendRequest);  // 받은 요청에서 삭제
        } else {
            throw new IllegalArgumentException("친구 관계가 존재하지 않습니다.");
        }
    }

    // 친구 목록 조회 --
    public FriendsResponse myFriends(Long memberId) {
        List<FriendRequest> friends = friendRequestRepository.findFriends(memberId, RequestStatus.ACCEPTED);
        FriendsResponse myFriends = new FriendsResponse();

        for (FriendRequest friend : friends) {
            if (friend.getReceiver().getId().equals(memberId)) {
                myFriends.getFriendNames().add(friend.getSendMemberId());
            } else if (friend.getSender().getId().equals(memberId)) {
                myFriends.getFriendNames().add(friend.getReceiveMemberId());
            }
        }
        return myFriends;
    }

    // 내가 받은 신청 목록 (대기 상태)
    public FriendsResponse myReceived(Long memberId) {
        List<String> friends = friendRequestRepository.findFriendRequests(memberId, RequestStatus.PENDING);
        return new FriendsResponse(friends);
    }

    public FriendsResponse myRequests(Long memberId) {
        List<String> myRequstList = friendRequestRepository.findBySender(memberId, RequestStatus.PENDING);
        return new FriendsResponse(myRequstList);
    }


}

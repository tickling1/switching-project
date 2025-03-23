package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.type.RequestStatus;
import com.switching.study_matching_site.dto.friend.FriendsResponse;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
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
    private final SecurityUtil securityUtil;


    // 친구 신청 보내기 - 친구 신청 받은 사람 아이디 반환
    public String requestFriend(Long receiverId) {
        Member sender = securityUtil.getMemberByUserDetails();
        Optional<Member> receiver = memberRepository.findById(receiverId);
        Optional<FriendRequest> friendStatus = friendRequestRepository.alreadyFriendStatus(sender.getId(), RequestStatus.ACCEPTED);

        if (receiver.isPresent() && friendStatus.isEmpty()) {
            FriendRequest friendRequest = new FriendRequest(sender, receiver.get());
            sender.addSentRequest(friendRequest);
            receiver.get().addReceivedRequest(friendRequest);
            friendRequestRepository.save(friendRequest);
            return receiver.get().getLoginId();
        } else {
            throw new EntityNotFoundException(ErrorCode.FRIEND_NOT_FOUND);
        }
    }

    // 친구 신청 받기 --
    public void acceptFriend(Long receiverId) {
        Long senderId = securityUtil.getMemberIdByUserDetails();
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(senderId, receiverId);

        if (friendRequest.isPresent()) {
            FriendRequest friendship = friendRequest.get();
            friendship.setStatus(RequestStatus.ACCEPTED);
        }
    }

    // 친구 거절
    public void rejectFriend(Long receiverId) {
        Long senderId = securityUtil.getMemberIdByUserDetails();
        Optional<FriendRequest> friendRequest = friendRequestRepository.findBySenderAndReceiver(senderId, receiverId);

        if (friendRequest.isPresent()) {
            FriendRequest friendship = friendRequest.get();
            friendship.setStatus(RequestStatus.REJECTED);
        }
    }

    // 친구 삭제
    public void deleteFriend(Long receiverId) {
        Member sender = securityUtil.getMemberByUserDetails();
        Optional<FriendRequest> friendship = friendRequestRepository.findBySenderAndReceiver(sender.getId(), receiverId);

        Member receiver = memberRepository.findById(receiverId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        if (friendship.isPresent()) {
            FriendRequest friendRequest = friendship.get();
            friendRequestRepository.delete(friendRequest);  // 친구 요청 삭제
            sender.getSentRequests().remove(friendRequest);  // 보낸 요청에서 삭제
            receiver.getReceivedRequests().remove(friendRequest);  // 받은 요청에서 삭제
        } else {
            throw new EntityNotFoundException(ErrorCode.NOT_FRIEND_RELATIONSHIP);
        }
    }

    // 친구 목록 조회 --
    public FriendsResponse myFriends() {
            Long senderId = securityUtil.getMemberIdByUserDetails();
            List<FriendRequest> friends = friendRequestRepository.findFriends(senderId, RequestStatus.ACCEPTED);
            FriendsResponse myFriends = new FriendsResponse();

        for (FriendRequest friend : friends) {
                if (friend.getReceiver().getId().equals(senderId)) {
                    myFriends.getFriendNames().add(friend.getSendMemberId());
                } else if (friend.getSender().getId().equals(senderId)) {
                    myFriends.getFriendNames().add(friend.getReceiveMemberId());
                }
            }
            return myFriends;
    }

    // 내가 받은 신청 목록 (대기 상태)
    public FriendsResponse myReceived() {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        List<String> friends = friendRequestRepository.findFriendRequests(memberId, RequestStatus.PENDING);
        return new FriendsResponse(friends);
    }

    public FriendsResponse myRequests() {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        List<String> myRequstList = friendRequestRepository.findBySender(memberId, RequestStatus.PENDING);
        return new FriendsResponse(myRequstList);
    }


}

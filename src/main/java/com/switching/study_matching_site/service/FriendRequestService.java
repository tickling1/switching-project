package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.type.RequestStatus;
import com.switching.study_matching_site.dto.friend.FriendRequestResponse;
import com.switching.study_matching_site.dto.friend.FriendsListResponse;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.FriendRequestRepository;
import com.switching.study_matching_site.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtil securityUtil;


    /**
     * 친구 신청 보내기
     * 내가 받아서 이미 친구가 된 상태 OR 내가 신청해서 이미 친구가 되었다면 예외발생
     * 자기 자신에게 친구 요청을 하면 예외발생
     * 친구 신청을 보내면 받는이, 보낸이 모두 대기(PENDING) 상태가 됨.
     * sender: 보내는 이, receiver: 받는 이
     */
    public FriendRequestResponse requestFriend(Long receiverId) {
        Member sender = securityUtil.getMemberByUserDetails();

        // 1. 자기 자신에게 요청 보낼 수 없음
        if (Objects.equals(sender.getId(), receiverId)) {
            throw new InvalidValueException(ErrorCode.CANNOT_REQUEST_SELF);
        }

        // 2. 상대방 존재 확인
        Member receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        // 3. 이미 존재하는 요청이 있는지 확인
        Optional<FriendRequest> optionalRequest = friendRequestRepository.findFriendStatusBetween(sender.getId(), receiverId);

        if (optionalRequest.isPresent()) {
            // 이미 요청이 있으면 그 상태를 그대로 반환
            FriendRequest existingRequest = optionalRequest.get();
            return new FriendRequestResponse(
                    sender.getId(),
                    receiverId,
                    existingRequest.getStatus()
            );
        }

        // 4. 요청이 없으면 새로 생성
        FriendRequest newRequest = new FriendRequest(sender, receiver);
        sender.addSentRequest(newRequest);
        receiver.addReceivedRequest(newRequest);
        friendRequestRepository.save(newRequest);

        return new FriendRequestResponse(
                sender.getId(),
                receiverId,
                RequestStatus.PENDING
        );
    }


    /**
     * 친구 신청 받기
     * 상대방에게 요청이 오지 않았다면, 예외 발생
     * 이미 친구 상태라면 예외 발생
     */
    public void acceptFriend(Long senderId) {
        Long receiverId = securityUtil.getMemberIdByUserDetails();

        FriendRequest friendRequest = friendRequestRepository.findByReceiverIdAndSenderId(receiverId, senderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CANNOT_ACCEPT_REQUEST));

        if (friendRequest.getStatus() == RequestStatus.ACCEPTED) {
            throw new InvalidValueException(ErrorCode.ALREADY_FRIEND_RELATIONSHIP);
        }
        friendRequest.setStatus(RequestStatus.ACCEPTED);
    }

    /**
     * 친구 거절
     * 친구 거절 시 친구 요청 기록이 사라짐
     * 친구 거절은 친구 신청을 받은 사용자만 가능
     */
    public void rejectFriend(Long senderId) {
        Long receiverId = securityUtil.getMemberIdByUserDetails();
        FriendRequest friendRequest = friendRequestRepository.findByReceiverIdAndSenderId(receiverId, senderId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CANNOT_REJECT_RELATIONSHIP));

        friendRequest.setStatus(RequestStatus.REJECTED);

        // 연관관계 끊기
        friendRequest.getSender().removeSentRequest(friendRequest);
        friendRequest.getReceiver().removeReceivedRequest(friendRequest);

        friendRequestRepository.deleteById(friendRequest.getId());
    }

    /**
     * 친구 삭제
     */
    public void deleteFriend(Long removeMemberId) {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        FriendRequest friendRequest = friendRequestRepository.findFriendStatusBetween(memberId, removeMemberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_FRIEND_RELATIONSHIP));

        // 연관관계 끊기
        friendRequest.getSender().removeSentRequest(friendRequest);
        friendRequest.getReceiver().removeReceivedRequest(friendRequest);
        friendRequestRepository.delete(friendRequest);  // 친구 요청 삭제
    }

    /**
     * 친구 목록 조회
     * 친구 상태의 전체 목록을 조회해옵니다.
     */
    @Transactional(readOnly = true)
    public FriendsListResponse myFriends() {
            Long memberId = securityUtil.getMemberIdByUserDetails();
            List<FriendRequest> friends = friendRequestRepository.findFriends(memberId);
            FriendsListResponse response = new FriendsListResponse();

        for (FriendRequest friend : friends) {
            Member other;
            if (friend.getReceiver() != null && friend.getReceiver().getId().equals(memberId)) {
                other = friend.getSender();
            } else {
                other = friend.getReceiver();
            }

            if (other != null) {
                response.getFriends().put(other.getId(), other.getUsername());
            }
        }
        return response;

    }

    /**
     * 내가 받은 친구 요청 목록
     * 친구 상태가 대기 중인 받은 요청 목록을 불러옵니다.
     */
    @Transactional(readOnly = true)
    public FriendsListResponse myReceived() {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        List<FriendRequest> myReceivedList = friendRequestRepository.findMyReceivedList(memberId);

        FriendsListResponse response = new FriendsListResponse();
        for (FriendRequest request : myReceivedList) {
            Member sender = request.getSender();
            if (sender != null) {
                response.getFriends().put(sender.getId(), sender.getUsername());
            }
        }
        return response;
    }

    /**
     * 내 친구 요청 목록
     * 친구 상태가 대기 중인 보낸 요청 목록을 불러옵니다.
     */
    @Transactional(readOnly = true)
    public FriendsListResponse myRequests() {
        Long senderId = securityUtil.getMemberIdByUserDetails();
        List<FriendRequest> requests = friendRequestRepository.findMyRequestList(senderId);

        FriendsListResponse response = new FriendsListResponse();
        for (FriendRequest request : requests) {
            Member receiver = request.getReceiver();
            if (receiver != null) {
                response.getFriends().put(receiver.getId(), receiver.getUsername());
            }
        }
        return response;
    }

}

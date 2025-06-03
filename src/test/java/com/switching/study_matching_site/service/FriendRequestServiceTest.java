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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceTest {

    @Mock
    private FriendRequestRepository friendRequestRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private FriendRequestService friendRequestService;

    private static Member createMember1() {
        Member member = new Member();
        member.setId(50L);
        member.setLoginId("kqk1234");
        member.setEmail("kqk1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setUsername("testMember1");
        return member;
    }

    private static Member createMember2() {
        Member member = new Member();
        member.setId(100L);
        member.setLoginId("kqk7777");
        member.setEmail("kqk777@gmail.com");
        member.setPhoneNumber("010-1111-1111");
        member.setUsername("testMember2");
        return member;
    }

    @Test
    void 친구_상태에서_친구_요청() {
        // given
        Member sender = createMember1();
        Member receiver = createMember2();

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        friendRequest.setSendMemberId(sender.getUsername());
        friendRequest.setReceiveMemberId(receiver.getUsername());
        friendRequest.setStatus(RequestStatus.ACCEPTED);

        when(securityUtil.getMemberByUserDetails()).thenReturn(sender);
        when(memberRepository.findById(any())).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findFriendStatusBetween(sender.getId(), receiver.getId()))
                .thenReturn(Optional.of(friendRequest));

        // when
        FriendRequestResponse responseDto = friendRequestService.requestFriend(receiver.getId());

        // then
        assertEquals(responseDto.getStatus(), RequestStatus.ACCEPTED);
        assertEquals(responseDto.getRequestId(), sender.getId());
        assertEquals(responseDto.getReceiverId(), receiver.getId());

    }

    @Test
    void 친구_요청_성공() {
        // given
        Member sender = createMember1();
        Member receiver = createMember2();

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        friendRequest.setSendMemberId(sender.getUsername());
        friendRequest.setReceiveMemberId(receiver.getUsername());

        when(securityUtil.getMemberByUserDetails()).thenReturn(sender);
        when(memberRepository.findById(any())).thenReturn(Optional.of(receiver));
        when(friendRequestRepository.findFriendStatusBetween(sender.getId(), receiver.getId()))
                .thenReturn(Optional.empty());
        // when
        FriendRequestResponse responseDto = friendRequestService.requestFriend(receiver.getId());

        // then
        assertEquals(responseDto.getStatus(), RequestStatus.PENDING);
    }

    @Test
    void 자신_에게_친구_요청() {
        // given
        Member sender = createMember1();
        when(securityUtil.getMemberByUserDetails()).thenReturn(sender);

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> friendRequestService.requestFriend(sender.getId()));
        assertEquals(ex.getErrorCode(), ErrorCode.CANNOT_REQUEST_SELF);
    }

    @Test
    void 친구_신청_수락_성공() {
        // given
        Member sender = createMember1();
        Member receiver = createMember2();

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setSendMemberId(sender.getUsername());
        friendRequest.setReceiveMemberId(receiver.getUsername());
        friendRequest.setStatus(RequestStatus.PENDING);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(receiver.getId());
        when(friendRequestRepository.findByReceiverIdAndSenderId(100L, 50L)).thenReturn(Optional.of(friendRequest));

        // when
        friendRequestService.acceptFriend(50L);

        // then
        assertEquals(RequestStatus.ACCEPTED, friendRequest.getStatus());
    }

    @Test
    void 친구_상태_친구_수락_실패() {
        Member sender = createMember1();
        Member receiver = createMember2();

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setSendMemberId(sender.getUsername());
        friendRequest.setReceiveMemberId(receiver.getUsername());
        friendRequest.setStatus(RequestStatus.ACCEPTED);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(receiver.getId());
        when(friendRequestRepository.findByReceiverIdAndSenderId(100L, 50L)).thenReturn(Optional.of(friendRequest));

        // when & then
        InvalidValueException ex = assertThrows(InvalidValueException.class, () -> friendRequestService.acceptFriend(50L));
        assertEquals(ex.getErrorCode(), ErrorCode.ALREADY_FRIEND_RELATIONSHIP);
    }

    @Test
    void 받지_않은_요청_수락() {
        // given
        Member receiver = createMember1();
        when(securityUtil.getMemberIdByUserDetails()).thenReturn(receiver.getId());
        when(friendRequestRepository.findByReceiverIdAndSenderId(receiver.getId(), 100L)).thenReturn(Optional.empty());
        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> friendRequestService.acceptFriend(100L));
        assertEquals(ex.getErrorCode(), ErrorCode.CANNOT_ACCEPT_REQUEST);
    }

    @Test
    void 친구_거절_성공() {
        Member sender = createMember1();
        Member receiver = createMember2();

        FriendRequest friendRequest = new FriendRequest(sender, receiver);
        friendRequest.setId(1L);
        friendRequest.setSender(sender);
        friendRequest.setReceiver(receiver);
        friendRequest.setSendMemberId(sender.getUsername());
        friendRequest.setReceiveMemberId(receiver.getUsername());
        friendRequest.setStatus(RequestStatus.PENDING);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(receiver.getId());
        when(friendRequestRepository.findByReceiverIdAndSenderId(100L, 50L)).thenReturn(Optional.of(friendRequest));

        // when
        friendRequestService.rejectFriend(50L);

        // then
        assertEquals(RequestStatus.REJECTED, friendRequest.getStatus());
        verify(friendRequestRepository, times(1)).deleteById(friendRequest.getId());
    }

    @Test
    void 받지_않은_요청_거절() {
        Member receiver = createMember1();
        when(securityUtil.getMemberIdByUserDetails()).thenReturn(receiver.getId());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> friendRequestService.rejectFriend(100L));
        assertEquals(ex.getErrorCode(), ErrorCode.CANNOT_REJECT_RELATIONSHIP);
    }

    @Test
    void 친구_삭제_성공() {
        // given
        Member member = createMember1();
        Member removeMember = createMember2();

        FriendRequest friendRequest = new FriendRequest(member, removeMember);
        friendRequest.setId(1L);
        friendRequest.setSender(member);
        friendRequest.setReceiver(removeMember);
        friendRequest.setSendMemberId(member.getUsername());
        friendRequest.setReceiveMemberId(removeMember.getUsername());
        friendRequest.setStatus(RequestStatus.ACCEPTED);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(friendRequestRepository.findFriendStatusBetween(member.getId(), removeMember.getId()))
                .thenReturn(Optional.of(friendRequest));

        // when
        friendRequestService.deleteFriend(removeMember.getId());

        // then
        verify(friendRequestRepository, times(1)).delete(friendRequest);
    }

    @Test
    void 친구_삭제_실패() {
        // given
        Member member = createMember1();

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(friendRequestRepository.findFriendStatusBetween(member.getId(), 100L))
                .thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> friendRequestService.deleteFriend(100L));
        assertEquals(ex.getErrorCode(), ErrorCode.NOT_FRIEND_RELATIONSHIP);
        verify(friendRequestRepository, never()).delete(any(FriendRequest.class));
    }

    @Test
    void 나의_친구_목록_보기() {
        // given
        Member member = createMember1();
        Member friend1 = createMember2();
        Member friend2 = createMember2();
        friend2.setId(30L);
        friend2.setUsername("real friend");

        FriendRequest friendRequest1 = new FriendRequest(member, friend1);
        friendRequest1.setId(1L);
        friendRequest1.setSender(member);
        friendRequest1.setReceiver(friend1);
        friendRequest1.setSendMemberId(member.getUsername());
        friendRequest1.setReceiveMemberId(friend1.getUsername());
        friendRequest1.setStatus(RequestStatus.ACCEPTED);

        FriendRequest friendRequest2 = new FriendRequest(friend2, member);
        friendRequest2.setId(2L);
        friendRequest2.setSender(friend2);
        friendRequest2.setReceiver(member);
        friendRequest2.setSendMemberId(friend2.getUsername());
        friendRequest2.setReceiveMemberId(member.getUsername());
        friendRequest2.setStatus(RequestStatus.ACCEPTED);

        List<FriendRequest> myFriends = new ArrayList<>();
        myFriends.add(friendRequest1);
        myFriends.add(friendRequest2);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(friendRequestRepository.findFriends(member.getId())).thenReturn(myFriends);

        // when
        FriendsListResponse friendsListResponse = friendRequestService.myFriends();

        // then
        assertEquals(2, friendsListResponse.getFriends().size());
        assertEquals(friend1.getUsername(), friendsListResponse.getFriends().get(100L));
        assertEquals(friend2.getUsername(), friendsListResponse.getFriends().get(30L));
        verify(friendRequestRepository, times(1)).findFriends(member.getId());

    }

    @Test
    void 친구_없는_나의_친구_목록_보기() {
        // given
        Member member = createMember1();

        List<FriendRequest> myFriends = new ArrayList<>();

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(friendRequestRepository.findFriends(member.getId())).thenReturn(myFriends);

        // when
        FriendsListResponse friendsListResponse = friendRequestService.myFriends();

        // then
        assertEquals(0, friendsListResponse.getFriends().size());
        verify(friendRequestRepository, times(1)).findFriends(member.getId());
    }

    @Test
    void 사용자가_받은_대기중인_요청() {
        // given
        Member member = createMember1();
        Member friend1 = createMember2();
        Member friend2 = createMember2();
        friend2.setId(30L);
        friend2.setUsername("real friend");

        // sender -> member, receiver -> friend1
        FriendRequest friendRequest1 = new FriendRequest(friend1, member);
        friendRequest1.setId(1L);
        friendRequest1.setSender(friend1);
        friendRequest1.setReceiver(member);
        friendRequest1.setSendMemberId(friend1.getUsername());
        friendRequest1.setReceiveMemberId(member.getUsername());
        friendRequest1.setStatus(RequestStatus.PENDING);

        // sender -> friend2, receiver -> member
        FriendRequest friendRequest2 = new FriendRequest(friend2, member);
        friendRequest2.setId(2L);
        friendRequest2.setSender(friend2);
        friendRequest2.setReceiver(member);
        friendRequest2.setSendMemberId(friend2.getUsername());
        friendRequest2.setReceiveMemberId(member.getUsername());
        friendRequest2.setStatus(RequestStatus.PENDING);

        List<FriendRequest> myReceived = new ArrayList<>();
        myReceived.add(friendRequest1);
        myReceived.add(friendRequest2);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(friendRequestRepository.findMyReceivedList(member.getId())).thenReturn(myReceived);

        // when
        FriendsListResponse friendsListResponse = friendRequestService.myReceived();

        // then
        System.out.println("friendsListResponse = " + friendsListResponse);
        assertEquals(2, friendsListResponse.getFriends().size());
        assertEquals(friend1.getUsername(), friendsListResponse.getFriends().get(100L));
        assertEquals(friend2.getUsername(), friendsListResponse.getFriends().get(30L));
        verify(friendRequestRepository, times(1)).findMyReceivedList(member.getId());
    }

    @Test
    void 사용자가_신청중인_요청() {
        // given
        Member member = createMember1();
        Member friend1 = createMember2();
        Member friend2 = createMember2();
        friend2.setId(30L);
        friend2.setUsername("real friend");

        FriendRequest friendRequest1 = new FriendRequest(member, friend1);
        friendRequest1.setId(1L);
        friendRequest1.setSender(member);
        friendRequest1.setReceiver(friend1);
        friendRequest1.setSendMemberId(member.getUsername());
        friendRequest1.setReceiveMemberId(friend1.getUsername());
        friendRequest1.setStatus(RequestStatus.PENDING);

        FriendRequest friendRequest2 = new FriendRequest(member, friend2);
        friendRequest2.setId(2L);
        friendRequest2.setSender(member);
        friendRequest2.setReceiver(friend2);
        friendRequest2.setSendMemberId(member.getUsername());
        friendRequest2.setReceiveMemberId(friend2.getUsername());
        friendRequest2.setStatus(RequestStatus.PENDING);

        List<FriendRequest> myRequests = new ArrayList<>();
        myRequests.add(friendRequest1);
        myRequests.add(friendRequest2);

        when(securityUtil.getMemberIdByUserDetails()).thenReturn(member.getId());
        when(friendRequestRepository.findMyRequestList(member.getId())).thenReturn(myRequests);

        // when
        FriendsListResponse friendsListResponse = friendRequestService.myRequests();

        // then
        System.out.println("friendsListResponse = " + friendsListResponse);
        assertEquals(2, friendsListResponse.getFriends().size());
        assertEquals(friend1.getUsername(), friendsListResponse.getFriends().get(100L));
        assertEquals(friend2.getUsername(), friendsListResponse.getFriends().get(30L));
        verify(friendRequestRepository, times(1)).findMyRequestList(member.getId());
    }


}
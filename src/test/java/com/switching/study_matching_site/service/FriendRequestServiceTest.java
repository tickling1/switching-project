/*
package com.switching.study_matching_site.service;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.RequestStatus;
import com.switching.study_matching_site.dto.friend.FriendsResponse;
import com.switching.study_matching_site.repository.FriendRequestRepository;
import com.switching.study_matching_site.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = StudyMatchingSiteApplication.class)
@Transactional
class FriendRequestServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Test
    @DisplayName("친구 신청")
    public void 친구_신청() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("kqk1ds");
        memberRepository.save(member1);
        Member member2 = new Member();
        member2.setLoginId("qqqqq");
        memberRepository.save(member2);

        // when
        Long friendId = friendRequestService.requestFriend(member1.getId(), member2.getId());

        // then

    }
    
    @Test
    @DisplayName("친구 신청 수락")
    public void 친구_신청_수락() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("kqk1ds");
        memberRepository.save(member1);
        Member member2 = new Member();
        member2.setLoginId("qqqqq");
        memberRepository.save(member2);

        // when
        Long friendId = friendRequestService.requestFriend(member1.getId(), member2.getId());
        friendRequestService.acceptFriend(member1.getId(), member2.getId());
        FriendRequest friendRequest = friendRequestRepository.findById(friendId).get();

        // then
        assertThat(friendRequest.getStatus()).isEqualTo(RequestStatus.ACCEPTED);
    }

    @Test
    @DisplayName("친구 신청 거절")
    public void 친구_신청_거절() throws Exception {

        // given
        Member member1 = new Member();
        member1.setLoginId("kqk1ds");
        memberRepository.save(member1);
        Member member2 = new Member();
        member2.setLoginId("qqqqq");
        memberRepository.save(member2);

        // when
        Long friendId = friendRequestService.requestFriend(member1.getId(), member2.getId());
        friendRequestService.rejectFriend(member1.getId(), member2.getId());
        FriendRequest friendRequest = friendRequestRepository.findById(friendId).get();
        // then
        assertThat(friendRequest.getStatus()).isEqualTo(RequestStatus.REJECTED);
    }

    @Test
    @DisplayName("친구 삭제")
    public void 친구_삭제() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("kqk1ds");
        memberRepository.save(member1);
        Member member2 = new Member();
        member2.setLoginId("qqqqq");
        memberRepository.save(member2);

        // when
        Long friendId = friendRequestService.requestFriend(member1.getId(), member2.getId());
        friendRequestService.deleteFriend(member1.getId(), member2.getId());

        // then
        assertThat(friendRequestRepository.findById(friendId).isEmpty());
    }

    @Test
    @DisplayName("내 친구 목록")
    public void 나의_친구_목록() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("kqk1ds");
        memberRepository.save(member1);
        // member1.getId() 값이 null이 아닌지 확인
        assertNotNull(member1.getId(), "ID should be generated for member1");

        Member member2 = new Member();
        member2.setLoginId("member2");
        memberRepository.save(member2);
        // member2.getId() 값 확인
        assertNotNull(member2.getId(), "ID should be generated for member2");

        Member member3 = new Member();
        member3.setLoginId("member3");
        memberRepository.save(member3);
        // member3.getId() 값 확인
        assertNotNull(member3.getId(), "ID should be generated for member3");

        Member member4 = new Member();
        member4.setLoginId("member4");
        memberRepository.save(member4);
        // member4.getId() 값 확인
        assertNotNull(member4.getId(), "ID should be generated for member4");

        // when
        friendRequestService.requestFriend(member1.getId(), member2.getId());
        friendRequestService.acceptFriend(member1.getId(), member2.getId());

        friendRequestService.requestFriend(member1.getId(), member3.getId());
        friendRequestService.rejectFriend(member1.getId(), member3.getId());

        friendRequestService.requestFriend(member1.getId(), member4.getId());
        friendRequestService.acceptFriend(member1.getId(), member4.getId());

        // then
        FriendsResponse friendsResponse = friendRequestService.myFriends(member1.getId());
        List<String> friendNames = friendsResponse.getFriendNames();
        for (String friendName : friendNames) {
            System.out.println("friendName = " + friendName);
        }
    }

    @Test
    @DisplayName("사용자가 받은 친구 요청")
    public void 사용자가_받은_친구_요청() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("kqk1ds");
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setLoginId("member2");
        memberRepository.save(member2);

        Member member3 = new Member();
        member3.setLoginId("member3");
        memberRepository.save(member3);

        Member member4 = new Member();
        member4.setLoginId("member4");
        memberRepository.save(member4);

        // when
        friendRequestService.requestFriend(member2.getId(), member1.getId());

        friendRequestService.requestFriend(member3.getId(), member1.getId());
        friendRequestService.rejectFriend(member3.getId(), member1.getId());

        friendRequestService.requestFriend(member4.getId(), member1.getId());

        // then
        FriendsResponse friendsResponse = friendRequestService.myReceived(member1.getId());
        List<String> friendNames = friendsResponse.getFriendNames();
        for (String friendName : friendNames) {
            System.out.println("friendName = " + friendName);
        }
    }
    
    @Test
    @DisplayName("사용자가 보낸 친구 요청 목록")
    @Rollback(value = false)
    public void 사용자가_보낸_친구_요청_목록() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("kqk1ds");
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setLoginId("member2");
        memberRepository.save(member2);

        Member member3 = new Member();
        member3.setLoginId("member3");
        memberRepository.save(member3);

        Member member4 = new Member();
        member4.setLoginId("member4");
        memberRepository.save(member4);

        // when
        friendRequestService.requestFriend(member1.getId(), member2.getId());

        friendRequestService.requestFriend(member1.getId(), member3.getId());
        friendRequestService.acceptFriend(member1.getId(), member3.getId());

        friendRequestService.requestFriend(member1.getId(), member4.getId());

        // then
        FriendsResponse friendsResponse = friendRequestService.myRequests(member1.getId());
        List<String> friendNames = friendsResponse.getFriendNames();
        for (String friendName : friendNames) {
            System.out.println("friendName = " + friendName);
        }
    }
}*/

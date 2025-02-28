package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.RequestStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(classes = StudyMatchingSiteApplication.class)
class FriendRepositoryTest {

    @Autowired
    private FriendRequestRepository friendRequestRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    public void CRUD() {

        Member memberA = new Member();
        memberA.setUsername("memberA");
        memberA.setLoginId("qqqqqqqqqqq");
        memberRepository.save(memberA);

        Member memberB = new Member();
        memberB.setUsername("memberB");
        memberB.setLoginId("bbbbbbbbbbb");
        memberRepository.save(memberB);

        FriendRequest friendRequest = new FriendRequest(memberA, memberB);
        friendRequestRepository.save(friendRequest);

        FriendRequest findRequest = friendRequestRepository.findById(friendRequest.getId()).get();
        assertThat(friendRequest).isEqualTo(findRequest);

        Member sender = findRequest.getSender();
        Member receiver = findRequest.getReceiver();

        assertThat(sender).isEqualTo(memberA);
        assertThat(receiver).isEqualTo(memberB);
    }

    @Test
    @DisplayName("친구 신청 보낸 회원의 아이디 보기")
    @Transactional
    public void 친구_신청_목록_보기() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("member1");
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setLoginId("member2");
        memberRepository.save(member2);

        Member member3 = new Member();
        member3.setLoginId("member3");
        memberRepository.save(member3);

        // when
        FriendRequest friendRequest1 = new FriendRequest(member1, member2);
        friendRequestRepository.save(friendRequest1);
        FriendRequest friendRequest2 = new FriendRequest(member1, member3);
        friendRequestRepository.save(friendRequest2);
        List<String> friendRequests = friendRequestRepository.findFriendRequests(member1.getId(), RequestStatus.ACCEPTED);


        // then
        assertThat(friendRequestRepository.count()).isEqualTo(2);
        assertThat(friendRequests.get(0)).isEqualTo(member2.getLoginId());
        assertThat(friendRequests.get(1)).isEqualTo(member3.getLoginId());
    }

    @Test
    @DisplayName("친구_목록_보기")
    @Transactional
    public void 친구_목록_보기() throws Exception {
        // given
        Member member1 = new Member();
        member1.setLoginId("member1");
        memberRepository.save(member1);

        Member member2 = new Member();
        member2.setLoginId("member2");
        memberRepository.save(member2);

        Member member3 = new Member();
        member3.setLoginId("member3");
        memberRepository.save(member3);

        // when
        FriendRequest friendRequest1 = new FriendRequest(member1, member2);
        friendRequest1.reject();
        friendRequestRepository.save(friendRequest1);
        FriendRequest friendRequest2 = new FriendRequest(member1, member3);
        friendRequest2.accept();
        friendRequestRepository.save(friendRequest2);
        List<FriendRequest> friends = friendRequestRepository.findFriends(member1.getId(), RequestStatus.ACCEPTED);


        // then
        assertThat(friends.size()).isEqualTo(1);
        assertThat(friends.get(0).getReceiver()).isEqualTo(member3);
    }

}
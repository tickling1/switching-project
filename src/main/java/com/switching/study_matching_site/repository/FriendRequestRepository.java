package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.type.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    // 내가 보낸 친구 신청
    @Query("SELECT f.receiveMemberId FROM FriendRequest f WHERE f.sender.id =:memberId AND f.status =:status")
    List<String> findBySender(@Param("memberId") Long memberId, @Param("status") RequestStatus status);

    // 내 친구 목록(status=ACCEPTED)
    @Query("SELECT f FROM FriendRequest f WHERE (f.sender.id =:memberId OR f.receiver.id =:memberId) AND f.status =:status")
    List<FriendRequest> findFriends(@Param("memberId") Long memberId, @Param("status") RequestStatus status);

    // 내가 받은 친구 요청 목록
    @Query("SELECT f.sendMemberId FROM FriendRequest f WHERE f.receiver.id =:memberId AND f.status =:status")
    List<String> findFriendRequests(@Param("memberId") Long memberId, @Param("status") RequestStatus status);

    // 특정 사용자의 친구 신청 내용
    @Query("SELECT f FROM FriendRequest f WHERE f.sender.id =:senderId AND f.receiver.id =:receiverId")
    Optional<FriendRequest> findBySenderAndReceiver(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    // 친구 상태(status = ACCEPTED)
    @Query("SELECT f FROM FriendRequest f WHERE (f.sender.id =:memberId OR f.receiver.id =:memberId) AND f.status =:status")
    Optional<FriendRequest> alreadyFriendStatus (@Param("memberId") Long memberId, @Param("status") RequestStatus status);

}

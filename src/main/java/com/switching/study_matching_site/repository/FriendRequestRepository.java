package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.type.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    // 내 친구 목록(status=ACCEPTED)
    @Query("SELECT f FROM FriendRequest f WHERE (f.sender.id =:memberId OR f.receiver.id =:memberId) AND f.status = 'ACCEPTED'")
    List<FriendRequest> findFriends(@Param("memberId") Long memberId);

    // 내가 받은 친구 요청 목록
    @Query("SELECT f FROM FriendRequest f WHERE f.receiver.id =:memberId AND f.status = 'PENDING'")
    List<FriendRequest> findMyReceivedList(@Param("memberId") Long memberId);


    // 내가 보낸 친구 신청
    @Query("SELECT f FROM FriendRequest f WHERE f.sender.id =:memberId AND f.status = 'PENDING'")
    List<FriendRequest> findMyRequestList(@Param("memberId") Long memberId);


    // 친구 신청 받기
    @Query("SELECT f FROM FriendRequest f WHERE f.receiver.id =:receiverId AND f.sender.id =:senderId")
    Optional<FriendRequest> findByReceiverIdAndSenderId(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);

    // 친구 상태(status = ACCEPTED)
    @Query("SELECT f FROM FriendRequest f WHERE " +
            "(f.sender.id =:senderId AND f.receiver.id =:receiverId) OR " +
            "(f.sender.id =:receiverId AND f.receiver.id =:senderId)")
    Optional<FriendRequest> findFriendStatusBetween(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);


}

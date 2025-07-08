package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.dto.friend.FriendRequestReceiverDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepositoryCustom {

    List<FriendRequestReceiverDto> findMyRequestDtos(@Param("memberId") Long memberId);

}

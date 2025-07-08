package com.switching.study_matching_site.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.switching.study_matching_site.domain.FriendRequest;
import com.switching.study_matching_site.domain.QFriendRequest;
import com.switching.study_matching_site.domain.QMember;
import com.switching.study_matching_site.domain.type.RequestStatus;
import com.switching.study_matching_site.dto.friend.FriendRequestReceiverDto;
import com.switching.study_matching_site.dto.friend.QFriendRequestReceiverDto;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FriendRequestRepositoryCustomImpl implements FriendRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public FriendRequestRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<FriendRequestReceiverDto> findMyRequestDtos(Long memberId) {
        QFriendRequest fr = QFriendRequest.friendRequest;
        QMember sender = new QMember("sender");
        QMember receiver = new QMember("receiver");

        return queryFactory
                .select(new QFriendRequestReceiverDto(
                        sender.id,
                        receiver.id,
                        receiver.username
                ))
                .from(fr)
                .join(fr.sender, sender)
                .join(fr.receiver, receiver)
                .where(
                        sender.id.eq(memberId),
                        fr.status.eq(RequestStatus.PENDING)
                )
                .fetch();
    }
}

package com.switching.study_matching_site.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.dto.condition.RoomSearchCond;
import com.switching.study_matching_site.dto.matching.ProfileCond;
import com.switching.study_matching_site.dto.room.QRoomInfoResponseDto;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.switching.study_matching_site.domain.QRoom.*;

@Repository
public class RoomRepositoryImpl implements RoomRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public RoomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<RoomInfoResponseDto> searchRoom(RoomSearchCond condition, Pageable pageable) {
        List<RoomInfoResponseDto> content = queryFactory
                .select(new QRoomInfoResponseDto(
                        room.roomTitle,
                        room.currentCount,
                        room.maxCount,
                        room.uuid
                ))
                .from(room)
                .where(
                        roomNameEq(condition.getRoomName()),
                        roomTechSkillEq(condition.getTechSkill()),
                        roomGoalEq(condition.getGoal()),
                        studyRegionEq(condition.getRegion()),
                        studyIsOfflineEq(condition.getOfflineStatus())
                )
                .orderBy(room.roomTitle.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(room.count())
                .from(room)
                .where(
                        roomNameEq(condition.getRoomName()),
                        roomTechSkillEq(condition.getTechSkill()),
                        roomGoalEq(condition.getGoal()),
                        studyRegionEq(condition.getRegion()),
                        studyIsOfflineEq(condition.getOfflineStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public Page<RoomInfoResponseDto> matchingRoom(ProfileCond condition, Pageable pageable) {
        List<RoomInfoResponseDto> content = queryFactory
                .select(new QRoomInfoResponseDto(
                        room.roomTitle,
                        room.currentCount,
                        room.maxCount,
                        room.uuid
                ))
                .from(room)
                .where(
                        roomTechSkillEq(condition.getTechSkill()),
                        roomGoalEq(condition.getStudyGoal()),
                        studyRegionEq(condition.getRegion()),
                        studyIsOfflineEq(condition.getIsOfflineStatus())
                )
                .orderBy(room.roomTitle.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 개수 조회
        JPAQuery<Long> countQuery = queryFactory
                .select(room.count())
                .from(room)
                .where(
                        roomTechSkillEq(condition.getTechSkill()),
                        roomGoalEq(condition.getStudyGoal()),
                        studyRegionEq(condition.getRegion()),
                        studyIsOfflineEq(condition.getIsOfflineStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchOne());
    }

    private BooleanExpression roomNameEq(String roomNameCond) {
        return roomNameCond != null ? room.roomTitle.contains(roomNameCond) : null;
    }
    private BooleanExpression roomTechSkillEq(TechSkill roomTechSkillCond) {
        return roomTechSkillCond != null ? room.techSkill.eq(roomTechSkillCond) : null;
    }
    private BooleanExpression roomGoalEq(Goal roomGoalEq) {
        return roomGoalEq != null ? room.projectGoal.eq(roomGoalEq) : null;
    }
    private BooleanExpression studyRegionEq(Region roomRegionEq) {
        return roomRegionEq != null ? room.projectRegion.eq(roomRegionEq) : null;
    }
    private BooleanExpression studyIsOfflineEq(OfflineStatus roomStatusEq) {
        return roomStatusEq != null ? room.offlineStatus.eq(roomStatusEq) : null;
    }

}

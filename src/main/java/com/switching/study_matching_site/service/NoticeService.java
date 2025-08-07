package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.domain.type.RoleType;
import com.switching.study_matching_site.dto.notice.NoticeCreateDto;
import com.switching.study_matching_site.dto.notice.NoticeReadDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.NoticeRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final ParticipationRepository participationRepository;
    private final SecurityUtil securityUtil;

    /**
     * 공지사항 생성
     * 참여하고 있는 활동이없다면 예외르 터트림
     * 참여하고 있는 방이 없다면 예외를 터트림
     * 참여하고 있는 방의 방장이 아니라면 예외르 터트림
     *
     */
    public void addNotice(NoticeCreateDto noticeCreateDto) {

        Participation participation = findParticipationBySecurity();
        Room room = participation.getRoom();
        if (room == null) {
         throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND)   ;
        }

        Notice newNotice = noticeCreateDto.toEntity();

        RoleType roleType = participation.getRoleType();
        if (roleType == RoleType.ADMIN) {
            // 연관관계 메서드
            room.addNotice(newNotice);
            noticeRepository.save(newNotice);
        } else {
            throw new InvalidValueException(ErrorCode.NOTICE_FORBIDDEN);
        }
    }

    /**
     * 공지사항 보기
     * 방에 참여하지 않았을 경우 예외를 일으켜야 함
     * 해당 공지사항이 없으면 예외를 일으켜야 함.
     */
    @Transactional(readOnly = true)
    public NoticeReadDto readNotice(Long noticeId) {

        Participation participation = findParticipationBySecurity();
        Room room = participation.getRoom();
        if (room == null) {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }

        Notice findNotice = noticeRepository.findById(noticeId).orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOTICE_NOT_FOUND));

        if (!findNotice.getRoom().equals(room)) {
            throw new InvalidValueException(ErrorCode.NOTICE_FORBIDDEN);
        }

        NoticeReadDto noticeReadDtoResponse = NoticeReadDto.fromEntity(findNotice);
        return noticeReadDtoResponse;
    }

    // Security 을 이용해 멤버의 참여찾기
    private Participation findParticipationBySecurity() {
        Long memberId = securityUtil.getMemberIdByUserDetails();
        Participation findParticipation = participationRepository.findActiveParticipation(memberId).orElseThrow(
                () -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));
        return findParticipation;
    }
}

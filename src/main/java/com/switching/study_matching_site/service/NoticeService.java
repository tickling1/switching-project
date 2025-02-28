package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.dto.member.CustomUserDetails;
import com.switching.study_matching_site.dto.notice.NoticeCreate;
import com.switching.study_matching_site.dto.notice.NoticeRead;
import com.switching.study_matching_site.dto.notice.NoticeUpdate;
import com.switching.study_matching_site.repository.NoticeRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final RoomRepository roomRepository;
    private final ParticipationRepository participationRepository;
    private final SecurityUtil securityUtil;

    // 공지사항 생성 - 방 ID (방장만 가능)
    public Long addNotice(NoticeCreate noticeCreateDto, Long roomId) {

        Notice newNotice = noticeCreateDto.toEntity();
        Optional<Room> findRoom = roomRepository.findById(roomId);
        // 멤버 룰 찾기 위해 
        Optional<Participation> findRule = participationRepository.findByRoomAndMember(roomId, securityUtil.getMemberIdByUserDetails());

        if (findRoom.isPresent() && findRule.isPresent()) {
            newNotice.setRoom(findRoom.get());
            RoleType roleType = findRule.get().getRoleType();
            if (roleType == RoleType.ADMIN) {
                Notice savedNotice = noticeRepository.save(newNotice);
                return savedNotice.getId();
            } else {
                throw new IllegalStateException("방장만 공지사항을 작성할 수 있습니다.");
            }
        } else {
            throw new IllegalStateException("방을 찾을 수 없습니다.");
        }
    }

    // 공지사항 보기 - 방 ID
    @Transactional(readOnly = true)
    public NoticeRead readNotice(Long roomId, Long noticeId) {
        Optional<Notice> findNotice = noticeRepository.findByRoom(roomId, noticeId);
        if (findNotice.isPresent()) {
            NoticeRead noticeRead = NoticeRead.fromEntity(findNotice.get());
            return noticeRead;
        } else {
            throw new IllegalStateException("찾으려는 공지사항이 없습니다.");
        }
    }

    // 공지사항 변경 - 방 ID, 공지사항 ID
    public void updateNotice(NoticeUpdate noticeUpdateDto, Long roomId, Long noticeId) {
        Optional<Notice> findNotice = noticeRepository.findById(noticeId);
        Long tryMemberId = securityUtil.getMemberIdByUserDetails();

        // 멤버 룰 찾기 위해
        Optional<Participation> findRule = participationRepository.findByRoomAndMember(roomId, tryMemberId);
        if (findRule.get().getRoleType() == RoleType.ADMIN) {

            if (findNotice.isPresent()) {
                Notice notice = findNotice.get();
                if (notice.getNoticeTitle() != null) notice.setNoticeTitle(noticeUpdateDto.getTitle());
                if (notice.getNoticeContent() != null) notice.setNoticeContent(noticeUpdateDto.getContent());
            } else {
                throw new IllegalStateException("찾으려는 공지사항이 없습니다. 공지사항을 먼저 생성해주세요.");
            }
        } else {
            throw new IllegalStateException("공지사항은 방장만 수정할 수 있습니다.");
        }
    }
}

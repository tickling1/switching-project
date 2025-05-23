package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.*;
import com.switching.study_matching_site.domain.type.RoleType;
import com.switching.study_matching_site.dto.notice.NoticeCreate;
import com.switching.study_matching_site.dto.notice.NoticeRead;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.repository.NoticeRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
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
                throw new InvalidValueException(ErrorCode.NOTICE_FORBIDDEN);
            }
        } else {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
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
            throw new EntityNotFoundException(ErrorCode.NOTICE_NOT_FOUND);
        }
    }
}

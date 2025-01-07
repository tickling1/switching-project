package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.domain.Notice;
import com.switching.study_matching_site.dto.notice.NoticeCreate;
import com.switching.study_matching_site.dto.notice.NoticeRead;
import com.switching.study_matching_site.dto.notice.NoticeUpdate;
import com.switching.study_matching_site.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studywithmatching.com")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;


    @PostMapping("members/rooms/{roomId}/notices")
    public Long createNotice(@RequestBody NoticeCreate noticeCreate,
                               @PathVariable(name = "roomId") Long roomId) {
        return noticeService.addNotice(noticeCreate, roomId);
    }

    @GetMapping("/rooms/{roomId}/notices/{noticeId}")
    public String viewNotice(@PathVariable(name = "roomId") Long roomId,
                             @PathVariable(name = "noticeId") Long noticeId) {
        NoticeRead noticeRead = noticeService.readNotice(roomId, noticeId);
        return noticeRead.toString();
    }

    @PutMapping("/rooms/{roomId}/notice/{noticeId}")
    public String updateNotice(@RequestBody NoticeUpdate noticeUpdate,
                               @PathVariable Long roomId, @PathVariable Long noticeId,
                               @PathVariable Long memberId) {

        noticeService.updateNotice(noticeUpdate, roomId, noticeId, memberId);
        return "수정 완료";
    }
}

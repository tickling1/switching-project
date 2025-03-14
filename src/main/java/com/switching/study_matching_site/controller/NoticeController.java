package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Notice;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.notice.NoticeCreate;
import com.switching.study_matching_site.dto.notice.NoticeRead;
import com.switching.study_matching_site.dto.notice.NoticeUpdate;
import com.switching.study_matching_site.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "NOTICE", description = "공지사항 API")
@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 등록", description = "공지사항을 등록합니다.")
    @PostMapping("/rooms/{roomId}/notices")
    public Long createNotice(@RequestBody NoticeCreate noticeCreate,
                               @Parameter(description = "rooms의 roomId")
                               @PathVariable(name = "roomId") Long roomId) {
        return noticeService.addNotice(noticeCreate, roomId);
    }

    @Operation(summary = "공지사항 보기", description = "공지사항 정보를 가져옵니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "공지사항 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoticeRead.class)
                            )
                    )
            })
    @Parameters({
            @Parameter(name = "roomId", description = "채팅방 ID", in = ParameterIn.PATH),
            @Parameter(name = "noticeId", description = "공지사항 번호", in = ParameterIn.PATH)
    })
    @GetMapping("/rooms/{roomId}/notices/{noticeId}")
    public String viewNotice(@PathVariable(name = "roomId") Long roomId,
                             @PathVariable(name = "noticeId") Long noticeId) {
        NoticeRead noticeRead = noticeService.readNotice(roomId, noticeId);
        return noticeRead.toString();
    }
}

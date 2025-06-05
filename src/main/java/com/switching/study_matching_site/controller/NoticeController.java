package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.notice.NoticeCreateDto;
import com.switching.study_matching_site.dto.notice.NoticeReadDto;
import com.switching.study_matching_site.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "NOTICE", description = "공지사항 API")
@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 등록", description = "공지사항을 등록합니다.")
    @Parameter(name = "NoticeCreateDto", description = "공지사항 생성 DTO")
    @PostMapping("/rooms/notices")
    public ResponseEntity<Void> createNotice(@RequestBody NoticeCreateDto noticeCreateDto) {
        noticeService.addNotice(noticeCreateDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "공지사항 보기", description = "공지사항 정보를 가져옵니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "공지사항 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = NoticeReadDto.class)
                            )
                    )
            })

    @Parameter(name = "noticeId", description = "공지사항 번호", in = ParameterIn.PATH)
    @GetMapping("/rooms/notices/{noticeId}")
    public ResponseEntity<String> viewNotice(@PathVariable(name = "noticeId") Long noticeId) {
        NoticeReadDto noticeReadDto = noticeService.readNotice(noticeId);
        return ResponseEntity.ok().body(noticeReadDto.toString());
    }
}

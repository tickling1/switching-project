package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.condition.RoomSearchCond;
import com.switching.study_matching_site.dto.room.PageResponseDto;
import com.switching.study_matching_site.dto.room.RoomDetailDto;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import com.switching.study_matching_site.service.MatchingService;
import com.switching.study_matching_site.service.RoomSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ROOM-SEARCH", description = "방 검색 API")
@RestController
@RequiredArgsConstructor
public class RoomSearchController {

    private final RoomSearchService roomSearchService;
    private final MatchingService matchingService;

    // 방 생성 목록
    @Operation(summary = "방 생성 목록 보기", description = "생성된 방 목록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "방 목록 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoomInfoResponseDto.class)
                            )
                    )
            })

    @GetMapping("/rooms/list")
    public PageResponseDto<RoomInfoResponseDto> roomList() {
        return roomSearchService.roomInfoList();
    }

    // 방 상세 정보
    @Operation(summary = "방 상세 정보 보기", description = "방의 상세 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "방 상세 정보 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoomDetailDto.class)
                            )
                    )
            })
    @GetMapping("/rooms/{roomId}")
    public String findRoomDetail(@Parameter(description = "방 ID")
                                 @PathVariable(name = "roomId") Long roomId) {
        return roomSearchService.findRoomById(roomId).toString();
    }

    @Operation(
            summary = "조건부 방 검색",
            description = "검색 조건(지역, 인원, 태그 등)을 받아 필터링된 방 목록을 페이징하여 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공"),
    })
    @GetMapping("/rooms/search")
    public Page<RoomInfoResponseDto> searchRoomList(@RequestBody @ParameterObject RoomSearchCond roomSearchCond) {
        return roomSearchService.roomSearchCondList(roomSearchCond);
    }

    @Operation(
            summary = "맞춤 매칭 방 목록 조회",
            description = "현재 로그인한 사용자의 프로필을 기반으로 최적의 스터디 방을 추천합니다."
    )
    @GetMapping("/rooms/matching")
    public Page<RoomInfoResponseDto> searchRoomList() {
        return matchingService.matchingRoomsList();
    }
}

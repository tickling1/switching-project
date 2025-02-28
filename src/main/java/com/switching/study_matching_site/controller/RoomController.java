package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.condition.RoomSearchCond;
import com.switching.study_matching_site.dto.room.PageResponseDto;
import com.switching.study_matching_site.dto.room.RoomDetail;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import com.switching.study_matching_site.dto.room.RoomUpdate;
import com.switching.study_matching_site.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ROOM", description = "방 API")
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    
    // 방 상세 정보
    @Operation(summary = "방 상세 정보 보기", description = "방의 상세 정보를 조회합니다.",
    responses = {
        @ApiResponse(
                responseCode = "200",
                description = "방 상세 정보 조회 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = RoomDetail.class)
                )
        )
    })

    @GetMapping("/rooms/{roomId}")
    public String findRoomDetail(@Parameter(description = "방 ID")
                                     @PathVariable(name = "roomId") Long roomId) {
        RoomDetail roomDetail = roomService.findRoomById(roomId);
        return roomDetail.toString();
    }

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
    @GetMapping("/rooms")
    public PageResponseDto<RoomInfoResponseDto> roomList() {
        roomService.roomInfoList();
        return roomService.roomInfoList();
    }

    @GetMapping("/rooms/search")
    public Page<RoomInfoResponseDto> searchRoomList(@RequestBody RoomSearchCond roomSearchCond) {
        return roomService.roomSearchInfoList(roomSearchCond);
    }

    // 방 수정 - 권한자가 수정 가능
    @Operation(summary = "방 내용 수정", description = "방 내용을 수정합니다.")
            @PutMapping("/rooms/{roomId}")
    public String updateRoom(@Parameter(description = "방 ID")
                                 @PathVariable(name = "roomId")Long roomId,
                             @RequestBody RoomUpdate roomUpdateDto) {
        roomService.updateRoom(roomId, roomUpdateDto);
        return "방이 수정되었습니다.";
    }

    // 방 제거 (남은 인원이 1명일 때 제거 가능)
    @Operation(summary = "방 삭제", description = "방을 삭제합니다.")
    @DeleteMapping("/rooms/{roomId}")
    public String deleteRoom(@Parameter(description = "방 ID")
                                 @PathVariable(name = "roomId")Long roomId) {
        roomService.removeRoom(roomId);
        return "방이 삭제되었습니다.";
    }
}

package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.notice.NoticeRead;
import com.switching.study_matching_site.dto.room.RoomCreate;
import com.switching.study_matching_site.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PARTICIPATION", description = "방 참여 API")
@RestController
@RequestMapping("/studywithmatching.com/members")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    // 멤버가 방 생성
    @Operation(summary = "방 생성", description = "회원이 방을 생성합니다.")
    @PostMapping("/{memberId}/rooms")
    public Long memberCreateRoom(@Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH)
                                     @PathVariable(name = "memberId")Long memberId,
                                 @RequestBody RoomCreate roomCreateDto) {
        return participationService.newParticipation(memberId, roomCreateDto);
    }

    // 멤버가 방 참여
    @Operation(summary = "방 참여", description = "회원이 방에 참여합니다.")
    @Parameters({
            @Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "roomId", description = "방 ID", in = ParameterIn.PATH)
    })
    @PostMapping("/{memberId}/rooms/{roomId}/join")
    public Long memberJoinRoom(@PathVariable(name = "memberId")Long memberId,
                               @PathVariable(name = "roomId")Long roomId) {
        return participationService.participate(memberId, roomId);
    }

    @Operation(summary = "UUID로 방 참여", description = "UUID로 회원이 방에 참여합니다.")
    @Parameters({
            @Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "roomUUID", description = "방 UUID", in = ParameterIn.PATH)
    })
    @PostMapping("/{memberId}/rooms/uuid/{roomUUID}/join")
    public Long memberJoinRoomWithUUID(@PathVariable(name = "memberId") Long memberId,
                                       @PathVariable(name = "roomUUID") String roomUUID) {
        return participationService.participateWithUUID(memberId, roomUUID);
    }

    // 문제 - 확인해보기
    @Operation(summary = "방 나가기", description = "회원이 방을 나갑니다.")
    @Parameters({
            @Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "roomId", description = "방 ID", in = ParameterIn.PATH)
    })
    @PostMapping("/{memberId}/rooms/{roomId}/leave")
    public String memberExitRoom(@PathVariable(name = "memberId") Long memberId,
                                 @PathVariable(name = "roomId")Long roomId) {
        participationService.leave(memberId, roomId);
        return "회원이 방을 나갔습니다.";
    }
}

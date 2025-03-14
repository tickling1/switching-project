package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.room.RoomCreateDto;
import com.switching.study_matching_site.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PARTICIPATION", description = "방 참여 API")
@RestController
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    // 멤버가 방 생성
    @Operation(summary = "방 생성", description = "회원이 방을 생성합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<Void> memberCreateRoom(@RequestBody RoomCreateDto roomCreateDto) {
        participationService.newRoomAndParticipation(roomCreateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 멤버가 방 참여
    @Operation(summary = "방 참여", description = "회원이 방에 참여합니다.")
    @Parameter(name = "roomId", description = "방 ID", in = ParameterIn.PATH)
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<Void> memberJoinRoom(@PathVariable(name = "roomId")Long roomId) {
        participationService.participate(roomId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "UUID로 방 참여", description = "UUID로 회원이 방에 참여합니다.")
    @Parameter(name = "roomUUID", description = "방 UUID", in = ParameterIn.PATH)
    @PostMapping("/rooms/uuid/{roomUUID}/join")
    public ResponseEntity<Void> memberJoinRoomWithUUID(@PathVariable(name = "roomUUID") String roomUUID) {
        participationService.participateWithUUID(roomUUID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 문제 - 확인해보기
    @Operation(summary = "방 나가기", description = "회원이 방을 나갑니다.")
    @Parameter(name = "roomId", description = "방 ID", in = ParameterIn.PATH)
    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> memberExitRoom(@PathVariable(name = "roomId")Long roomId) {
        participationService.leave(roomId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

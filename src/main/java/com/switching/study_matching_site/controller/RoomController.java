package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.room.*;
import com.switching.study_matching_site.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ROOM", description = "방 API")
@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // 방 수정 - 권한자가 수정 가능
    @Operation(summary = "방 내용 수정", description = "방 내용을 수정합니다.")
    @Parameter(name = "roomId", description = "방 ID", in = ParameterIn.PATH)
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateRoom(@PathVariable(name = "roomId")Long roomId,
                                           @RequestBody RoomUpdateDto roomUpdateDto) {
        roomService.updateRoom(roomId, roomUpdateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 멤버가 방 생성
    @Operation(summary = "방 생성", description = "회원이 방을 생성합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<Void> memberCreateRoom(@RequestBody RoomCreateDto roomCreateDto) {
        roomService.newRoomAndParticipation(roomCreateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 멤버가 방 참여
    @Operation(summary = "방 참여", description = "회원이 방에 참여합니다.")
    @Parameter(name = "roomId", description = "방 ID", in = ParameterIn.PATH)
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<Void> memberJoinRoom(@PathVariable(name = "roomId")Long roomId) {
        roomService.participateRoom(roomId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "UUID로 방 참여", description = "UUID로 회원이 방에 참여합니다.")
    @Parameter(name = "roomUUID", description = "방 UUID", in = ParameterIn.PATH)
    @PostMapping("/rooms/uuid/{roomUUID}/join")
    public ResponseEntity<Void> memberJoinRoomWithUUID(@PathVariable(name = "roomUUID") String roomUUID) {
        roomService.participateWithUUID(roomUUID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 방 나가기 & 방 제거 (1명 남았을 때 제거)
    @Operation(summary = "방 나가기", description = "회원이 방을 나갑니다. 방에 인원이 혼자 있을 때 방이 삭제됩니다.")
    @Parameter(name = "roomId", description = "방 ID", in = ParameterIn.PATH)
    @DeleteMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Void> memberExitRoom(@PathVariable(name = "roomId")Long roomId) {
        roomService.leaveRoom(roomId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

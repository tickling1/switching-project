package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.room.RoomCreate;
import com.switching.study_matching_site.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/studywithmatching.com/members")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    // 멤버가 방 생성
    @PostMapping("/{memberId}/rooms")
    public Long memberCreateRoom(@PathVariable(name = "memberId")Long memberId, @RequestBody RoomCreate roomCreateDto) {
        return participationService.newParticipation(memberId, roomCreateDto);
    }

    // 멤버가 방 참여
    @PostMapping("/{memberId}/rooms/{roomId}/join")
    public Long memberJoinRoom(@PathVariable(name = "memberId")Long memberId, @PathVariable(name = "roomId")Long roomId) {
        return participationService.participate(memberId, roomId);
    }

    @PostMapping("/{memberId}/rooms/uuid/{roomUUID}/join")
    public Long memberJoinRoomWithUUID(@PathVariable(name = "memberId") Long memberId,
                                       @PathVariable(name = "roomUUID") String roomUUID) {
        return participationService.participateWithUUID(memberId, roomUUID);
    }

    // 문제
    @PostMapping("/{memberId}/rooms/{roomId}/leave")
    public String memberExitRoom(@PathVariable(name = "memberId") Long memberId,
                                 @PathVariable(name = "roomId")Long roomId) {
        participationService.leave(memberId, roomId);
        return "회원이 방을 나갔습니다.";
    }
}

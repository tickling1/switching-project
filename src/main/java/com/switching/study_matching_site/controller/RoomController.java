package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.room.RoomDetail;
import com.switching.study_matching_site.dto.room.RoomInfo;
import com.switching.study_matching_site.dto.room.RoomUpdate;
import com.switching.study_matching_site.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studywithmatching.com")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    
    // 방 상세 정보
    @GetMapping("/rooms/{roomId}")
    public String findRoomDetail(@PathVariable(name = "roomId") Long roomId) {
        RoomDetail roomDetail = roomService.findRoomById(roomId);
        return roomDetail.toString();
    }

    // 방 생성 목록 
    @GetMapping("/rooms")
    public String roomList() {
        List<RoomInfo> roomInfos = roomService.roomInfoList();
        return roomInfos.toString();
    }

    // 방 수정 - 권한자가 수정 가능
    @PutMapping("/rooms/{roomId}")
    public String updateRoom(@PathVariable(name = "roomId")Long roomId, @RequestBody RoomUpdate roomUpdateDto) {
        roomService.updateRoom(roomId, roomUpdateDto);
        return "방이 수정되었습니다.";
    }

    // 방 제거 (남은 인원이 1명일 때 제거 가능)
    @DeleteMapping("/rooms/{roomId}")
    public String deleteRoom(@PathVariable(name = "roomId")Long roomId) {
        roomService.removeRoom(roomId);
        return "방이 삭제되었습니다.";
    }
}

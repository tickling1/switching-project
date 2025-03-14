package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.domain.Chat;
import com.switching.study_matching_site.dto.chat.ChatCreate;
import com.switching.study_matching_site.dto.chat.ChatRead;
import com.switching.study_matching_site.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "CHAT", description = "채팅 API")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅 등록", description = "채팅 내용(chatContent)을 이용하여 채팅을 신규 등록합니다.",
    responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "채팅 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChatRead.class)
                    )
            )
    })
    @PostMapping("/rooms/{roomId}/chats")
    public String addChat(@PathVariable Long roomId, @RequestBody ChatCreate chatCreate) {
        ChatRead chat = chatService.createChat(chatCreate, roomId);
        return chat.toString();
    }
    
    @Operation(summary = "채팅 불러오기", description = "채팅 내역을 확인합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "채팅 내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChatRead.class)
                    )
            )
    })
    @GetMapping("/rooms/{roomId}/chats")
    public String readChat(@PathVariable Long roomId) {
        List<ChatRead> chatReads = chatService.readChat(roomId);
        return chatReads.toString();
    }
}

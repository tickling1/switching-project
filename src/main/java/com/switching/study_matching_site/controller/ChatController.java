package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.chat.ChatCreateDto;
import com.switching.study_matching_site.dto.chat.ChatReadDto;
import com.switching.study_matching_site.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
                            schema = @Schema(implementation = ChatReadDto.class)
                    )
            )
    })
    @PostMapping("/rooms/chats")
    @Parameter(name = "ChatCreateDto", description = "채팅 생성 DTO")
    public ResponseEntity<String> addChat(@RequestBody ChatCreateDto chatCreateDto) {
        ChatReadDto chat = chatService.createChat(chatCreateDto);
        return ResponseEntity.ok().body(chat.toString());
    }
    
    @Operation(summary = "채팅 불러오기", description = "채팅 내역을 확인합니다.", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "채팅 내역 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChatReadDto.class)
                    )
            )
    })
    @GetMapping("/rooms/chats")
    public ResponseEntity<List<ChatReadDto>> readChat() {
        List<ChatReadDto> chatHistory = chatService.readChat();
        return ResponseEntity.ok().body(chatHistory);
    }
}

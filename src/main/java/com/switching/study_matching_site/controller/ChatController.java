package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.domain.Chat;
import com.switching.study_matching_site.dto.chat.ChatCreate;
import com.switching.study_matching_site.dto.chat.ChatRead;
import com.switching.study_matching_site.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studywithmatching.com")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("members/{memberId}/rooms/{roomId}/chats")
    public String addChat(@PathVariable Long memberId,
                          @PathVariable Long roomId,
                          @RequestBody ChatCreate chatCreate) {
        ChatRead chat = chatService.createChat(chatCreate, memberId, roomId);
        return chat.toString();
    }

    @GetMapping("/rooms/{roomId}/chats")
    public String addChat(@PathVariable Long roomId) {
        List<ChatRead> chatReads = chatService.readChat(roomId);
        return chatReads.toString();
    }
}

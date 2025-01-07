package com.switching.study_matching_site.dto.chat;


import com.switching.study_matching_site.domain.Chat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class ChatRead {

    private String chatContent;
    private LocalDateTime chatDateTime;
    private String username;

    public static ChatRead fromEntity(Chat chat) {
        return ChatRead.builder()
                .chatContent(chat.getChatContent())
                .chatDateTime(chat.getChatDateTime())
                .username(chat.getWriter())
                .build();
    }

    @Override
    public String toString() {
        return "ChatRead{" +
                "chatContent='" + chatContent + '\'' +
                ", chatDateTime=" + chatDateTime +
                ", username='" + username + '\'' +
                '}';
    }
}

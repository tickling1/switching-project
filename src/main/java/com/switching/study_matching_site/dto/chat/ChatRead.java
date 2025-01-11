package com.switching.study_matching_site.dto.chat;


import com.switching.study_matching_site.domain.Chat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Schema(description = "채팅 응답 DTO")
public class ChatRead {

    @Schema(description = "채팅 내용")
    private String chatContent;

    @Schema(description = "채팅 일자")
    private LocalDateTime chatDateTime;

    @Schema(description = "채팅을 보낸 사람")
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

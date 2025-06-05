package com.switching.study_matching_site.dto.chat;


import com.switching.study_matching_site.domain.Chat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Builder
@Getter
@Setter
@Schema(description = "채팅 응답 DTO")
public class ChatReadDto {

    @Schema(description = "채팅을 보낸 사람")
    private String username;

    @Schema(description = "채팅 내용")
    private String chatContent;

    @Schema(description = "채팅 일자")
    private LocalDateTime chatDateTime;


    public static ChatReadDto fromEntity(Chat chat) {
        return ChatReadDto.builder()
                .chatContent(chat.getChatContent())
                .chatDateTime(chat.getChatDateTime())
                .username(chat.getWriter())
                .build();
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "[" + username + ": " + chatContent + "] at " + chatDateTime.format(formatter);
    }
}

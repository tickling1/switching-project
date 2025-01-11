package com.switching.study_matching_site.dto.chat;

import com.switching.study_matching_site.domain.Chat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Schema(description = "채팅 요청 DTO")
public class ChatCreate {

    @Schema(description = "채팅 내용")
    private String chatContent;

    public Chat toEntity() {
        Chat chat = new Chat();
        chat.setChatContent(this.chatContent);
        chat.setChatDateTime(LocalDateTime.now());
        return chat;
    }
}

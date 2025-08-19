package com.switching.study_matching_site.dto.chat;

import com.switching.study_matching_site.domain.Chat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 요청 DTO")
public class ChatCreateDto {

    @Schema(description = "채팅 내용")
    private String chatContent;

    public Chat toEntity() {
        Chat chat = new Chat();
        chat.setChatContent(this.chatContent);
        chat.setChatDateTime(LocalDateTime.now());
        return chat;
    }
}

package com.switching.study_matching_site.dto.chat;

import com.switching.study_matching_site.domain.Chat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ChatCreate {

    private String chatContent;

    public Chat toEntity() {
        Chat chat = new Chat();
        chat.setChatContent(this.chatContent);
        chat.setChatDateTime(LocalDateTime.now());
        return chat;
    }
}

package com.switching.study_matching_site.dto.notice;

import com.switching.study_matching_site.domain.Notice;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class NoticeUpdate {

    @NotNull
    private String title;

    @NotNull
    private String content;
    private LocalDateTime updatedAt;

    /**
     * DTO -> ENTITY 변환 메소드
     */
    public Notice toEntity() {
        Notice notice = new Notice();
        notice.setNoticeTitle(this.title);
        notice.setNoticeContent(this.content);
        return notice;
    }
}

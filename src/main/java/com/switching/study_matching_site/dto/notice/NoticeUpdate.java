package com.switching.study_matching_site.dto.notice;

import com.switching.study_matching_site.domain.Notice;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class NoticeUpdate {

    private String title;
    private String content;
    private LocalDateTime updatedAt;

    /**
     * DTO -> ENTITY 변환 메소드
     */
    public Notice toEntity() {
        Notice notice = new Notice();
        notice.setNoticeTitle(this.title);
        notice.setNoticeContent(this.content);
        notice.setUpdatedAt(LocalDateTime.now());
        return notice;
    }
}

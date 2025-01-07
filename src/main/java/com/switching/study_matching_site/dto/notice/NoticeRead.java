package com.switching.study_matching_site.dto.notice;

import com.switching.study_matching_site.domain.Notice;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class NoticeRead {

    private String title;
    private String content;

    /**
     * ENTITY -> DTO 변환 메소드
     */
    public static NoticeRead fromEntity(Notice entity){
        return NoticeRead.builder()
                .title(entity.getNoticeTitle())
                .content(entity.getNoticeContent())
                .build();
    }

    @Override
    public String toString() {
        return "NoticeRead{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

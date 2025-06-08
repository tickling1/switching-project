package com.switching.study_matching_site.dto.notice;

import com.switching.study_matching_site.domain.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@Schema(name = "공지사항 보기 응답 DTO")
public class NoticeReadDto {

    @Schema(description = "공지사항 제목")
    private String title;
    
    @Schema(description = "공지사항 내용")
    private String content;

    /**
     * ENTITY -> DTO 변환 메소드
     */
    public static NoticeReadDto fromEntity(Notice entity){
        return NoticeReadDto.builder()
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

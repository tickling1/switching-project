package com.switching.study_matching_site.dto.room;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponseDto<T> {

    private List<T> content;
    private long totalElements;
    private int totalPages;

    public PageResponseDto(List<T> content, long totalElements, int totalPages) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}

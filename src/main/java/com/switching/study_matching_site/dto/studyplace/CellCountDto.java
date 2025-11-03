package com.switching.study_matching_site.dto.studyplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class CellCountDto {
    private String geohash;
    private long count;

}

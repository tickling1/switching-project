package com.switching.study_matching_site.dto.studyplace;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "위치 정보 요청 객체")
public class LocationRequestDto {

    @Schema(description = "경도 (Longitude)", example = "126.9780")
    private double lng;

    @Schema(description = "위도 (Latitude)", example = "37.5665")
    private double lat;
}

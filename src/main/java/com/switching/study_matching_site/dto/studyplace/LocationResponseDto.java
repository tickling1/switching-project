package com.switching.study_matching_site.dto.studyplace;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LocationResponseDto {

    private String placeName;
    private String address;
    private String roadAddress;
    private String phone;
    private String distance;
}

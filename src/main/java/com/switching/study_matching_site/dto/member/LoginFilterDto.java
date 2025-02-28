package com.switching.study_matching_site.dto.member;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class LoginFilterDto {

    private Long id;
    private String loginId;
    private String password;
}

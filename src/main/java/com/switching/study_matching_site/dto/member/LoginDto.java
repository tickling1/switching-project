package com.switching.study_matching_site.dto.member;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class LoginDto {

    private String loginId;
    private String password;

}

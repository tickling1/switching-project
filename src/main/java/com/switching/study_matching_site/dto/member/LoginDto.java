package com.switching.study_matching_site.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Schema(description = "로그인 요청 DTO")
public class LoginDto {

    @Schema(name = "아이디")
    private String loginId;
    @Schema(name = "비밀번호")
    private String password;

}

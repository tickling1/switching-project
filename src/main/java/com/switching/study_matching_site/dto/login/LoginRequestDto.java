package com.switching.study_matching_site.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @Schema(name = "아이디")
    private String loginId;
    @Schema(name = "비밀번호")
    private String password;
}

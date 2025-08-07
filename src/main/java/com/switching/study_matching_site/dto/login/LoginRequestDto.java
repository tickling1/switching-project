package com.switching.study_matching_site.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDto {

    @NotNull(message = "로그인 아이디는 필수입니다.")
    @Schema(name = "아이디")
    private String loginId;

    @NotNull(message = "비밀번호는 필수입니다.")
    @Schema(name = "비밀번호")
    private String password;
}

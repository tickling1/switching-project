package com.switching.study_matching_site.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
@Schema(description = "리프레시 토큰 재발급/로그아웃 요청 DTO")
public class TokenRequest {

    @NotNull
    @Schema(description = "리프레시 토큰")
    private String refreshToken;
}

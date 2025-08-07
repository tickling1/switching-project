package com.switching.study_matching_site.controller;


import com.switching.study_matching_site.dto.login.LoginRequestDto;
import com.switching.study_matching_site.dto.login.TokenRequest;
import com.switching.study_matching_site.dto.login.TokenResponse;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LOGIN", description = "로그인 API")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "로그인 유지 연장", description = "엑세스 토큰을 재발급해 로그인 유지를 연장합니다.")
    @PostMapping("/members/reissue")
    public ResponseEntity<?> reissue(@RequestBody @Validated TokenRequest tokenRequest) {
         return loginService.getRefreshToken(tokenRequest);
    }

    @Operation(summary = "회원 로그아웃", description = "회원 로그아웃을 시도합니다.")
    @PostMapping("/members/logout")
    public ResponseEntity<Void> logout(@RequestBody @Validated TokenRequest tokenRequest) {
        loginService.logout(tokenRequest); // Refresh Token 제거
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 로그인", description = "회원 로그인을 시도합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MemberCreateDto.class
                                    )
                            )
                    )
            })
    @PostMapping("/members/login")
    public ResponseEntity<TokenResponse> loginMember(@RequestBody @Validated LoginRequestDto loginRequestDto) {
        ResponseEntity<TokenResponse> responses = loginService.tryLogin(loginRequestDto);
        // ResponseEntity<Void> responses = loginService.tryLogin(loginRequestDto, response);
        return responses;
    }
}

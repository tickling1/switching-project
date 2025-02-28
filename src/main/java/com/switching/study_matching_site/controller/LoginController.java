package com.switching.study_matching_site.controller;


import com.switching.study_matching_site.dto.login.TokenResponse;
import com.switching.study_matching_site.dto.login.LoginRequestDto;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LOGIN", description = "로그인 API")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "리프레쉬 토큰을 생성합니다.", description = "만료된 리프레쉬 토큰을 재성성 합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
         return loginService.getRefreshToken(request, response);
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
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> loginMember(@RequestBody LoginRequestDto loginRequestDto) {
        ResponseEntity<TokenResponse> responses = loginService.login(loginRequestDto);
        return responses;
    }

}

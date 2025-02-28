package com.switching.study_matching_site.controller;


import com.switching.study_matching_site.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LOGIN", description = "로그인 API")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @Operation(summary = "리프레쉬 토큰을 생성합니다.", description = "만료된 리프레쉬 토큰을 재성성 합니다.")
    @PostMapping("/members/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

         return loginService.getRefreshToken(request, response);
    }
}

package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.jwt.JWTUtil;
import com.switching.study_matching_site.repository.RefreshRepository;
import com.switching.study_matching_site.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studywithmatching.com/members")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

         return loginService.getRefreshToken(request, response);
    }
}

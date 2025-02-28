package com.switching.study_matching_site.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class CustomAuthenticationFailureHandler implements AccessDeniedHandler, AuthenticationFailureHandler {


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 로그인 실패에 대한 응답 내용 설정
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.LOGIN_INPUT_INVALID);

        // 예외 메시지를 바탕으로 필드 오류를 추가
        List<ErrorResponse.FieldError> fieldErrors = Collections.singletonList(
                new ErrorResponse.FieldError("loginId", "", "아이디 또는 비밀번호가 잘못 되었습니다.")
        );

        // 응답 상태를 401로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // JSON 형태로 오류 응답을 반환
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        String errorMessage = "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.";

        response.getWriter().write(errorMessage);
    }
}

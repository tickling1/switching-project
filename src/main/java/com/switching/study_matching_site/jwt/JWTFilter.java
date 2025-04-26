package com.switching.study_matching_site.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.ErrorResponse;

import com.switching.study_matching_site.service.CustomerUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomerUserDetailsService customerUserDetailsService;

    public JWTFilter(JWTUtil jwtUtil, CustomerUserDetailsService customerUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customerUserDetailsService = customerUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        // 로그인, 회원가입, 토큰 재발급 경로는 JWT 필터를 거치지 않도록 설정
        if (requestURI.equals("/login") ||
                requestURI.equals("/members") ||
                requestURI.equals("/reissue") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/swagger-resources") ||
                requestURI.startsWith("/rooms") ||
                requestURI.startsWith("/actuator/health")){
            filterChain.doFilter(request, response);
            return;
        }

        // 헤더에서 access키에 담긴 토큰을 꺼냄
        //String accessToken = request.getHeader("access");

        // Authorization 헤더에서 "Bearer <token>" 부분 추출
        String accessToken = null;
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7);  // "Bearer "를 제외한 토큰 부분만 추출
        }

            // 토큰이 없다면 401 상태코드와 JSON 본문 응답
            if (accessToken == null) {
                sendErrorResponse(response, ErrorCode.JWT_TOKEN_MISSING);
                return;
            }

            // 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
            try {
                jwtUtil.isExpired(accessToken);
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, ErrorCode.JWT_TOKEN_EXPIRED);
                return;
            }

            // 토큰이 access인지 확인 (발급시 페이로드에 명시)
            String category = jwtUtil.getCategory(accessToken);

            if (!category.equals("access")) {
                sendErrorResponse(response, ErrorCode.JWT_TOKEN_MALFORMED);
                return;
            }

            // loginId 값을 획득
            String loginId = jwtUtil.getLoginId(accessToken);
            UserDetails userDetails = customerUserDetailsService.loadUserByUsername(loginId);

            // 넣는다
            Authentication authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 다음 필터로 넘김
            filterChain.doFilter(request, response);
        }

        private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401 상태 코드 설정
            response.getWriter().write(new ObjectMapper().writeValueAsString(ErrorResponse.of(errorCode)));
        }
    }
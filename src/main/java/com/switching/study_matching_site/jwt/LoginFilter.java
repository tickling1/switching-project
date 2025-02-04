package com.switching.study_matching_site.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.switching.study_matching_site.domain.Refresh;
import com.switching.study_matching_site.dto.member.CustomUserDetails;
import com.switching.study_matching_site.dto.member.LoginFilterDto;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    // 검증을 진행하는 곳, 진행방법: DB에서 회원정보를 UserDetailService 통해서 유저 정보를 받고 인증을 진행
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    // 의존성 추가
    private RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        // 파라미터 이름 설정 (기본값은 "username"과 "password")
        setUsernameParameter("loginId"); // loginId를 username으로 처리
        setPasswordParameter("password");  // password는 그대로 사용

        // 기본값: /login
        setFilterProcessesUrl("/members/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            // JSON 형태로 요청 본문을 읽음
            ObjectMapper objectMapper = new ObjectMapper();
            LoginFilterDto loginRequest = objectMapper.readValue(request.getInputStream(), LoginFilterDto.class);

            // 로그인 ID와 비밀번호를 사용하여 UsernamePasswordAuthenticationToken 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getLoginId(), loginRequest.getPassword());


            // token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authenticationToken);

        } catch (IOException e) {
            throw new IllegalStateException("아이디 또는 비밀번호가 잘못되었습니다.!!");
        }
    }


    // 검증 성공 시 해당 로직 수행
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //UserDetailsS
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String loginId = customUserDetails.getUsername();

        // loginId와 만료기간을 초기화 10분
        String access = jwtUtil.createJwt("access", loginId, 600000L);
        String refresh = jwtUtil.createJwt("refresh", loginId, 86400000L);

        // refresh 토큰 저장
        addRefreshEntity(loginId, refresh, 86400000L);

        // response에 담아 Header에 응답
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpStatus.OK.value());

    }

    // 검증 실패 시 해당 로직 수행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
        System.out.println("로그인이 실패하였습니다.");
    }

    // 쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }


    private void addRefreshEntity(String loginId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setLoginId(loginId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}

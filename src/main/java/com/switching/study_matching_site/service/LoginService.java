package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Refresh;
import com.switching.study_matching_site.dto.login.TokenResponse;
import com.switching.study_matching_site.dto.login.LoginRequestDto;
import com.switching.study_matching_site.dto.member.CustomUserDetails;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.InvalidValueException;
import com.switching.study_matching_site.jwt.JWTUtil;
import com.switching.study_matching_site.repository.MemberRepository;
import com.switching.study_matching_site.repository.RefreshRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<TokenResponse> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        Member member = memberRepository.findByLoginId(loginRequestDto.getLoginId())
                .orElseThrow(() -> new InvalidValueException(ErrorCode.LOGIN_INPUT_INVALID));

        // 비밀번호 검증 600000L
        if (bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {

            // ✅ 1. Access Token & Refresh Token 생성
            String accessToken = jwtUtil.createJwt("access", loginRequestDto.getLoginId(), 6000000000L);
            String refreshToken = jwtUtil.createJwt("refresh", loginRequestDto.getLoginId(), 86400000L);

            // ✅ 2. Refresh Token을 DB 또는 Redis에 저장 (선택)
            addRefreshEntity(loginRequestDto.getLoginId(), refreshToken, 86400000L);

            // ✅ 3. 클라이언트에게 토큰 반환
            // response에 담아 Header에 응답
            // response.setHeader("access", accessToken);
            // response.addCookie(createCookie("refresh", refreshToken));
            // response.setStatus(HttpStatus.OK.value());


            // JWT 토큰을 Authorization 헤더에 포함하여 응답
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);  // Bearer {token} 형식으로 설정
            // 쿠키 생성후 클라이언트에게 실제로 쿠키 전달
            response.addCookie(createCookie("refresh", refreshToken));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new TokenResponse(accessToken, refreshToken));
            } else {
                throw new InvalidValueException(ErrorCode.LOGIN_INPUT_INVALID);
            }
    }

    public ResponseEntity<?> getRefreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refresh = null;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refresh")) {
                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        //DB에 저장되어 있는지 확인
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            // 존재하지 않다면
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String loginId = jwtUtil.getLoginId(refresh);

        //make new JWT
        String newAccess = jwtUtil.createJwt("Authorization", loginId, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", loginId, 86400000L);

        //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(loginId, newRefresh, 86400000L);

        //response
        response.setHeader("Authorization", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }


    private void addRefreshEntity(String loginId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);
        Refresh refreshEntity = new Refresh(loginId, refresh, date.toString());
        refreshEntity.setLoginId(loginId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    // 쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        // cookie.setSecure(true); - Https 환경에서만
        // cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
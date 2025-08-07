package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Refresh;
import com.switching.study_matching_site.dto.login.TokenRequest;
import com.switching.study_matching_site.dto.login.TokenResponse;
import com.switching.study_matching_site.dto.login.LoginRequestDto;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final JWTUtil jwtUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<TokenResponse> tryLogin(LoginRequestDto loginRequestDto) {

        Member member = memberRepository.findByLoginId(loginRequestDto.getLoginId())
                .orElseThrow(() -> new InvalidValueException(ErrorCode.LOGIN_INPUT_INVALID));

        if (bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {

            String accessToken = jwtUtil.createJwt("access", loginRequestDto.getLoginId(), 600000L);
            String refreshToken = jwtUtil.createJwt("refresh", loginRequestDto.getLoginId(), 86400000L);

            // 기존 Refresh Token 무효화
            refreshRepository.deleteByLoginId(loginRequestDto.getLoginId());
            addRefreshEntity(loginRequestDto.getLoginId(), refreshToken, 86400000L);

            // 헤더에 토큰을 넣지 않고, 바디에 토큰을 담아 반환
            TokenResponse tokenResponse = new TokenResponse(accessToken, refreshToken);

            return ResponseEntity.ok(tokenResponse);

        } else {
            throw new InvalidValueException(ErrorCode.LOGIN_INPUT_INVALID);
        }
    }


    public ResponseEntity<?> getRefreshToken(TokenRequest tokenRequest) {
        String refresh = tokenRequest.getRefreshToken();

        if (refresh == null) {
            return new ResponseEntity<>("리프레시 토큰을 입력해주세요", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("접속 만료 기한이 지났습니다. 다시 로그인 해주세요.", HttpStatus.BAD_REQUEST);
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            return new ResponseEntity<>("잘못된 형식의 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST);
        }

        String loginId = jwtUtil.getLoginId(refresh);

        String newAccess = jwtUtil.createJwt("access", loginId, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", loginId, 86400000L);

        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(loginId, newRefresh, 86400000L);

        TokenResponse tokenResponse = new TokenResponse(newAccess, newRefresh);
        return ResponseEntity.ok(tokenResponse);
    }


    public void logout(TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRefreshToken();

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new InvalidValueException(ErrorCode.JWT_TOKEN_MISSING);
        }

        try {
            jwtUtil.isExpired(refreshToken);  // 유효성 체크
            long deleted = refreshRepository.deleteByRefresh(refreshToken);
            if (deleted == 0) {
                throw new InvalidValueException(ErrorCode.JWT_TOKEN_MALFORMED);
            }

        } catch (ExpiredJwtException e) {
            throw new InvalidValueException(ErrorCode.JWT_TOKEN_EXPIRED);
        } catch (Exception e) {
            throw new InvalidValueException(ErrorCode.JWT_TOKEN_MALFORMED);
        }
    }

    private void addRefreshEntity(String loginId, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        Refresh refreshEntity = new Refresh(loginId, refresh, date.toString());
        refreshRepository.save(refreshEntity);
    }


    /*public ResponseEntity<TokenResponse> tryLogin(LoginRequestDto loginRequestDto, HttpServletResponse response) {

        Member member = memberRepository.findByLoginId(loginRequestDto.getLoginId())
                .orElseThrow(() -> new InvalidValueException(ErrorCode.LOGIN_INPUT_INVALID));

        // 비밀번호 검증
        if (bCryptPasswordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {

            // 1. Access Token & Refresh Token 생성
            String accessToken = jwtUtil.createJwt("access", loginRequestDto.getLoginId(), 600000L);
            String refreshToken = jwtUtil.createJwt("refresh", loginRequestDto.getLoginId(), 86400000L);

            // 2. Refresh Token을 DB 또는 Redis에 저장
            addRefreshEntity(loginRequestDto.getLoginId(), refreshToken, 86400000L);

            // JWT 토큰을 Authorization 헤더에 포함하여 응답
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);  // Bearer {token} 형식으로 설정
            // 쿠키 생성후 클라이언트에게 실제로 쿠키 전달
            response.addCookie(createCookie("refresh", refreshToken));

            // 3. 클라이언트에게 토큰 반환
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
    }*/


    /*// 쿠키 생성 메서드
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setPath("/");  // 모든 요청에 포함
        cookie.setSecure(true); // - Https 환경에서만
        cookie.setHttpOnly(true);
        return cookie;
    }*/
}
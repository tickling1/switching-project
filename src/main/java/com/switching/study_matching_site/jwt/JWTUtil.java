package com.switching.study_matching_site.jwt;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key()
                        .build()
                        .getAlgorithm());
    }

    // 우리가 가지고 있는 시크릿 키를 넣어서 우리 서버에서 생성된 키가 맞는지 확인 진행
    public String getLoginId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 서명 검증에 사용할 키 설정
                .build()
                .parseSignedClaims(token) // 여기서 서명 검증 + 파싱 + 유효성 검사 모두 수행
                .getPayload()
                .get("loginId", String.class);
    }


    // 토큰이 소멸되었는지 확인하는 메서드
    public Boolean isExpired(String token) {
        return Jwts.parser().
                verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration()
                .before(new Date());
    }

    // 토큰을 생성하는 메서드
    public String createJwt(String category, String loginId, Long expiredMs) {
        return Jwts.builder()
                .claim("category", category) // access 인지 refresh 토큰인지
                .claim("loginId", loginId) // 특정한 키에 대한 데이터를 넣어둘 수 있음
                .issuedAt(new Date(System.currentTimeMillis())) // 토큰이 언제 발행됬는지? 현재 발행시간을 넣어줄 수 있음
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 토큰이 언제 소멸되는지 설정
                .signWith(secretKey) // 암호화 진행
                .compact();
    }

    // 내부 카테고리 값(access, refresh) 확인 메서드
    public String getCategory(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }
}
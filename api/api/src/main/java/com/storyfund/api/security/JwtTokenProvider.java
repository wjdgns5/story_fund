package com.storyfund.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private SecretKey secretKey;

    @Value("${jwt.secret}")
    private String secretKeyString;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @PostConstruct // Bean객체 생성 후. 가장 먼저 실행되는 메서드
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    // Access Token 생성
    public String createAccessToken(String email, String role) {
        return Jwts.builder()
                .subject(email) // 토큰 주인이 누구인지
                .claim("role", role) // 추가로 담고 싶은 데이터
                .issuedAt(new Date()) // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + accessExpiration)) // 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact(); // 최종
    }

    // Refresh Token 생성
    public String createRefreshToken(String email) {
        return Jwts.builder()
                .subject(email) // 토큰 주인이 누구인지
                .issuedAt(new Date()) // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration )) // 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact(); // 최종
    }

    // 토큰 파싱 (내부 메서드)
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // 서명 검증을 위한 키 설정
                .build()
                .parseSignedClaims(token) // 토큰 파싱 (서명 확인 포함)
                .getPayload(); // 내부 데이터(Claims) 추출
    }

    // 토큰에서 이메일 꺼내기
    public String getEmail(String token) {
        return getClaims(token).getSubject(); // email 값 읽기
    }

    // 토큰에서 역할 꺼내기
    public String getRole(String token) {
        return getClaims(token).get("role", String.class); // claim 넣은 "role" 값 읽기
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try{
            getClaims(token);
            return true;
        } catch (Exception e) {
            System.out.println("토큰 검증 실패: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return false;
        }
    }

}

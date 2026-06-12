package com.storyfund.api.controller;

import com.storyfund.api.dto.*;
import com.storyfund.api.service.EmailService;
import com.storyfund.api.service.KakaoService;
import com.storyfund.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API") //  API 를 그룹으로 묶는다. -> Auth 그룹

@RequestMapping("/api/auth")
@RestController // @Controller + @ResponseBody 를 합친 거
public class UserController {

    private UserService userService;
    private EmailService emailService;
    private KakaoService kakaoService;

    public UserController(UserService userService, EmailService emailService,  KakaoService kakaoService) {
        this.userService = userService;
        this.emailService = emailService;
        this.kakaoService = kakaoService;
    } // end of Constructor

    // 1. 회원가입  -- @Valid — DTO 에 달아둔 @NotBlank, @Email 같은 검증을 실행
    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 회원가입") // summary — API 이름 (짧게) description — 상세 설명 (길게)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok("회원가입 완료");
    }

    // 2. 로그인 -- @Valid — DTO 에 달아둔 @NotBlank, @Email 같은 검증을 실행
    @Operation(summary = "로그인", description = "이메일, 비밀번호로 로그인. Access Token 반환, Refresh Token Cookie 저장") // summary — API 이름 (짧게) description — 상세 설명 (길게)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto dto,
                                        HttpServletResponse response) {

        LoginResponseDto result = userService.login(dto);

        // Refresh Token 을 HttpOnly Cookie 에 담아서 전송
        Cookie cookie = new Cookie("refreshToken", userService.getRefreshToken(result.getEmail()));
        cookie.setHttpOnly(true);    // JS 에서 접근 불가 (XSS 방어)
        cookie.setSecure(false);     // 배포 때 true 로 변경 (HTTPS 에서만 전송)
        cookie.setPath("/");         // 모든 경로에서 Cookie 전송
        cookie.setMaxAge(7 * 24 * 60 * 60);  // 7일 (초 단위)
        response.addCookie(cookie);

        return ResponseEntity.ok(result);
    }

    // 3. refresh -- HttpServletRequest : 서버로 보낸 요청 정보를 담는 객체
    @Operation(summary = "토큰 갱신", description = "Cookie 의 Refresh Token 으로 새 Access Token 발급")
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(HttpServletRequest request) {

        // Cookie 에서 Refresh Token 꺼내기
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh Token 이 없습니다.");
        }

        String newAccessToken = userService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @AuthenticationPrincipal String email,
            HttpServletResponse response) {
        //  AuthenticationPrincipal : Spring Security에서 로그인된 사용자의 정보(Principal)를
        //  컨트롤러(Controller)의 매개변수로 직접 주입받을 수 있게 해주는 어노테이션

        userService.logout(email);

        // Cookie 만료시키기
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("로그아웃 성공");
    }

    // 인증 코드 발송
    @Operation(summary = "카카오 소셜 로그인", description = "카카오 인가 코드로 로그인. 신규 유저면 자동 회원가입")
    @PostMapping("/emails/send")
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestBody EmailRequestDto dto) {
        // @Valid — DTO 에 달아둔 @NotBlank, @Email 같은 검증을 실행
        emailService.sendVerificationCode(dto.getEmail());
        return ResponseEntity.ok("인증 코드를 발송했습니다.");
    }

    // 인증 코드 확인
    @Operation(summary = "이메일 인증 코드 발송")
    @PostMapping("/emails/verify")
    public ResponseEntity<String> verifyCode(@Valid @RequestBody EmailVerifyRequestDto dto) {
        // @Valid — DTO 에 달아둔 @NotBlank, @Email 같은 검증을 실행

        boolean result = emailService.verifyCode(dto.getEmail(), dto.getCode());

        if (!result) {
            throw new IllegalArgumentException("인증 코드가 일치하지 않거나 만료됐습니다.");
        }

        // 인증 완료 → DB 업데이트
       // userService.updateEmailVerified(dto.getEmail());

        return ResponseEntity.ok("이메일 인증이 완료됐습니다.");
    }

    // 카카오 로그인 API
    @Operation(summary = "이메일 인증 코드 확인")
    @GetMapping("/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code, HttpServletResponse response) {

        LoginResponseDto result = kakaoService.kakaoLogin(code);

        // Refresh Token Cookie 설정
        Cookie cookie = new Cookie("refreshToken",
                userService.getRefreshToken(result.getEmail()));
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(result);
    }

}

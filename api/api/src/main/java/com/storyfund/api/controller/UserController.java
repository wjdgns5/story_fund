package com.storyfund.api.controller;

import com.storyfund.api.dto.LoginRequestDto;
import com.storyfund.api.dto.LoginResponseDto;
import com.storyfund.api.dto.SignupRequestDto;
import com.storyfund.api.security.JwtTokenProvider;
import com.storyfund.api.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController // @Controller + @ResponseBody 를 합친 거
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    } // end of Constructor

    // 1. 회원가입  -- @Valid — DTO 에 달아둔 @NotBlank, @Email 같은 검증을 실행
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok("회원가입 완료");
    }

    // 2. 로그인 -- @Valid — DTO 에 달아둔 @NotBlank, @Email 같은 검증을 실행
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

}

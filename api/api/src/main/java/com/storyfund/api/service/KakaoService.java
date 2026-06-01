package com.storyfund.api.service;

import com.storyfund.api.dto.KakaoTokenResponseDto;
import com.storyfund.api.dto.KakaoUserInfoDto;
import com.storyfund.api.dto.LoginResponseDto;
import com.storyfund.api.entity.User;
import com.storyfund.api.repository.UserRepository;
import com.storyfund.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    public LoginResponseDto kakaoLogin(String code) {

        // 1. 인가 코드로 카카오 Access Token 받기
        String kakaoAccessToken = getKakaoAccessToken(code);

        // 2. Access Token 으로 사용자 정보 받기
        KakaoUserInfoDto userInfo = getKakaoUserInfo(kakaoAccessToken);

        // 3. 이메일로 기존 회원 조회
        String email    = userInfo.getEmail();
        String nickname = userInfo.getNickname();

        // 4. 신규 회원이면 자동 가입
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> registerKakaoUser(email, nickname));

        // 5. JWT 발급
        String accessToken  = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole() );
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail() );

        // 6. Refresh Token DB 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponseDto(
                accessToken,
                user.getEmail(),
                user.getNickname(),
                user.getRole()
        );
    }

    // 인가 코드 → 카카오 Access Token
    private String getKakaoAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 바디
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type",    "authorization_code");
        body.add("client_id",     clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri",  redirectUri);
        body.add("code",          code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // 카카오 토큰 서버에 POST 요청
        ResponseEntity<KakaoTokenResponseDto> response = restTemplate.postForEntity
                ("https://kauth.kakao.com/oauth/token", request, KakaoTokenResponseDto.class);

        return response.getBody().getAccessToken();
    } // end of getKakaoAccessToken

    // 카카오 Access Token → 사용자 정보
    private KakaoUserInfoDto getKakaoUserInfo(String kakaoAccessToken) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();

        headers.setBearerAuth(kakaoAccessToken);   // Authorization: Bearer 토큰
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange
                ("https://kapi.kakao.com/v2/user/me", HttpMethod.GET, request, KakaoUserInfoDto.class);

        return response.getBody();
    }

    // 신규 카카오 유저 자동 회원가입
    private User registerKakaoUser(String email, String nickname) {

        // 닉네임 중복이면 뒤에 숫자 붙이기
        String uniqueNickname = nickname;
        int count = 1;
        while (userRepository.existsByNickname(uniqueNickname)) {
            uniqueNickname = nickname + count;
            count++;
        }

        User user = User.builder()
                .email(email)
                .password(null)       // 소셜 유저는 비밀번호 없음
                .nickname(uniqueNickname)
                .build();

        return userRepository.save(user);
    }

}

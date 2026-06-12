package com.storyfund.api.service;

import com.storyfund.api.dto.LoginRequestDto;
import com.storyfund.api.dto.LoginResponseDto;
import com.storyfund.api.dto.SignupRequestDto;
import com.storyfund.api.entity.User;
import com.storyfund.api.repository.UserRepository;
import com.storyfund.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;

    public UserService (UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    } // end of Constructor

    // 1. 회원가입
    public void signup(SignupRequestDto dto) {

        // 1. 이메일 중복 확인
        if(userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        }

        // 2. 닉네임 중복 확인
        if(userRepository.existsByNickname(dto.getNickname())) {
            throw  new IllegalArgumentException("이미 사용중인 닉네임 입니다.");
        }

        // 3. 비밀번호 암호화 후 유저 저장
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword() ))
                .nickname(dto.getNickname())
                .build();

        // 회원가입 완료 후 이메일 인증 처리
        user.setEmailVerified(true);

        // 유저 데이터 DB에 저장
        userRepository.save(user);
    }

    // 2. 로그인
    public LoginResponseDto login(LoginRequestDto dto) {

        // 1. 이메일로 유저 찾기
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 이메일 입니다."));

        // 2. 비밀번호 확인
        if(!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw  new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        // 4. Refresh Token DB 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return new LoginResponseDto(accessToken, user.getEmail(), user.getNickname(), user.getRole());
    } // end of login

    // 3. Refresh Token 꺼내기
    public String getRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다."));
        return user.getRefreshToken();
    }

    // 4. refreshAccessToken 으로 accessToken 새로 받기
    public String refreshAccessToken(String refreshToken) {

        // 1. Refresh Token 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token 입니다.");
        }

        // 2. 토큰에서 이메일 꺼내기
        String email = jwtTokenProvider.getEmail(refreshToken);

        // 3. DB 의 Refresh Token 과 비교
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh Token 이 일치하지 않습니다.");
        }

        // 4. 새 Access Token 발급
        return jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
    }

    // 5. 로그아웃
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // Refresh Token null 로 만들어서 무효화
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    // 이메일 인증 완료 처리
    public void updateEmailVerified(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        user.setEmailVerified(true);
        userRepository.save(user);
    }

}

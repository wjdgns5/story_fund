package com.storyfund.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Profile;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    // 카카오 사용자 정보를 담는 DTO

    private Long id;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;


    @Getter
    @NoArgsConstructor
    public static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        @NoArgsConstructor
        public static class Profile {
            private String nickname;

        } // end of Profile

    } // end of KakaoUserInfoDto

    // 이메일 꺼내기
    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }

    // 닉네임 꺼내기
    public String getNickname() {
        return kakaoAccount != null && kakaoAccount.getProfile() != null
                ? kakaoAccount.getProfile().getNickname() : null;
    }

}

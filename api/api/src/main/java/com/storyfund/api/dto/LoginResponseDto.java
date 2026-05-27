package com.storyfund.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {

    private String accessToken;
    private String email;
    private String nickname;
    private String role;
}

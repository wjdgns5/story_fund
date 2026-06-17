package com.storyfund.api.dto;

import com.storyfund.api.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserInfoResponseDto {

    private String email;
    private String nickname;
    private int coin;
    private LocalDateTime createdAt;

    // User Entity -> DTO
    public UserInfoResponseDto(User user) {
        this.email     = user.getEmail();
        this.nickname  = user.getNickname();
        this.coin      = user.getCoin();
        this.createdAt = user.getCreatedAt();
    }

}

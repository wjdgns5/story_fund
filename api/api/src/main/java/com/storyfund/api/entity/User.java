package com.storyfund.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = true, length = 200)
    private String password;

    @Column(nullable = false, unique = true, length = 20)
    private String nickname;

    @Column(nullable = false, length = 20)
    private String role;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private int coin;

    @Column(nullable = true, length = 500)
    private String refreshToken;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // 회원가입 현재시간
        this.coin = 0; // 코인 보유 : 0
        this.emailVerified = false; // 이메일 인증 완료 여부
        this.role = "ROLE_USER"; // 유저등급
    }

}

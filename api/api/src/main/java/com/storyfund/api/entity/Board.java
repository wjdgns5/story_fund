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
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Board 조회할 때 User 는 일단 조회 안 함 (실제로 호출할때 사용)
    @JoinColumn(name = "user_id", nullable = false) // 외래키(FK)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT") // 게시글 본문은 그보다 길 수 있어서 사용
    private String content;

    @Column(nullable = false)
    private boolean isPaid;        // 유료 여부

    @Column(nullable = false)
    private int viewCount;         // 조회수

    @Column(nullable = true)
    private LocalDateTime deletedAt;   // 삭제 시간 (소프트 삭제)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
    }
}

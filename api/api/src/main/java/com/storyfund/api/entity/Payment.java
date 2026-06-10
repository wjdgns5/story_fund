package com.storyfund.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Table(name = "payments")
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, unique = true, length = 100)
    private String orderId;        // 주문 ID (UUID)

    @Column(nullable = false)
    private int amount;            // 결제 금액 (원)

    @Column(nullable = false)
    private int coinAmount;        // 충전 코인 수

    @Column(length = 200)
    private String paymentKey;     // Toss 결제 키 (승인 후 저장)

    @Column(nullable = false, length = 20)
    private String status;         // READY, DONE, CANCELED

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;  // 결제 완료 시간

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = "READY";
    }

    /**
     * READY    주문 생성됨. 아직 결제 안 됨
     * DONE     결제 완료. 코인 충전됨
     * CANCELED 결제 취소됨
     */
}

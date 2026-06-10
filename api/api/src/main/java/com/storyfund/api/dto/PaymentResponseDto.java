package com.storyfund.api.dto;

import com.storyfund.api.entity.Payment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentResponseDto {
    // 결제 내역 응답

    private Long id;
    private String orderId;
    private int amount;
    private int coinAmount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public PaymentResponseDto(Payment payment) {
        this.id         = payment.getId();
        this.orderId    = payment.getOrderId();
        this.amount     = payment.getAmount();
        this.coinAmount = payment.getCoinAmount();
        this.status     = payment.getStatus();
        this.createdAt  = payment.getCreatedAt();
        this.paidAt     = payment.getPaidAt();
    }
}

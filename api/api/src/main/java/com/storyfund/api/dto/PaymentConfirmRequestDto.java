package com.storyfund.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentConfirmRequestDto {
    // 결제 검증 요청

    @NotBlank(message = "paymentKey 가 없습니다.")
    private String paymentKey;  // Toss 결제 키

    @NotBlank(message = "orderId 가 없습니다.")
    private String orderId; // 주문 ID

    @NotNull(message = "금액이 없습니다.")
    private Integer amount; // 결제 금액
}

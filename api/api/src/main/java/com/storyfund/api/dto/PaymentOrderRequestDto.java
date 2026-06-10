package com.storyfund.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PaymentOrderRequestDto {
    // 주문 생성 요청

    @NotNull(message = "결제 금액을 입력해주세요.")
    @Min(value = 1000, message = "최소 결제 금액은 1,000원 입니다.")
    private Integer amount; // 결제 금액 (원)

    @NotNull(message = "충전 코인을 입력해주세요.")
    private Integer coinAmount; // 충전 코인 수
}

package com.storyfund.api.controller;

import com.storyfund.api.dto.BoardResponseDto;
import com.storyfund.api.dto.PaymentConfirmRequestDto;
import com.storyfund.api.dto.PaymentOrderRequestDto;
import com.storyfund.api.dto.PaymentResponseDto;
import com.storyfund.api.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 주문 생성", description = "Toss 결제 위젯 실행 전 주문 정보 DB 저장")
    @PostMapping("/order")
    public ResponseEntity<Map<String, Object>> createOrder(
            @Valid @RequestBody PaymentOrderRequestDto dto,
            @AuthenticationPrincipal String email) {

        return ResponseEntity.ok(paymentService.createOrder(dto, email));
    }

    @Operation(summary = "결제 검증 + 코인 충전", description = "Toss 결제 완료 후 금액 검증 및 코인 충전")
    @PostMapping("/confirm")
    public ResponseEntity<PaymentResponseDto> confirmPayment(
            @Valid @RequestBody PaymentConfirmRequestDto dto,
            @AuthenticationPrincipal String email) {

        return ResponseEntity.ok(paymentService.confirmPayment(dto, email));
    }

    @Operation(summary = "유료 글 열람", description = "코인 1개 차감 후 유료 게시글 열람")
    @PostMapping("/unlock/{boardId}")
    public ResponseEntity<BoardResponseDto> unlockBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal String email) {

        return ResponseEntity.ok(paymentService.unlockBoard(boardId, email));
    }

    @Operation(summary = "결제 내역 조회")
    @GetMapping("/history")
    public ResponseEntity<Page<PaymentResponseDto>> getPaymentHistory(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal String email) {

        return ResponseEntity.ok(paymentService.getPaymentHistory(email, page));
    }

}

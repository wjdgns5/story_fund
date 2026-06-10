package com.storyfund.api.service;

import com.storyfund.api.dto.BoardResponseDto;
import com.storyfund.api.dto.PaymentConfirmRequestDto;
import com.storyfund.api.dto.PaymentOrderRequestDto;
import com.storyfund.api.dto.PaymentResponseDto;
import com.storyfund.api.entity.Board;
import com.storyfund.api.entity.Payment;
import com.storyfund.api.entity.UnlockHistory;
import com.storyfund.api.entity.User;
import com.storyfund.api.repository.BoardRepository;
import com.storyfund.api.repository.PaymentRepository;
import com.storyfund.api.repository.UnlockHistoryRepository;
import com.storyfund.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final UnlockHistoryRepository unlockHistoryRepository;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    @Value("${toss.confirm-url}")
    private String tossConfirmUrl;

    // 1. 결제 주문 생성
    public Map<String, Object> createOrder(PaymentOrderRequestDto dto, String email) {

        // 1. 사용자 email 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 2. UUID로 고유한 주문 ID 생성하기
        String orderId = UUID.randomUUID().toString();

        // 3. 결제에 필요한 정보를 Entity에 저장
        Payment payment = Payment.builder()
                .user(user)
                .orderId(orderId)
                .amount(dto.getAmount())
                .coinAmount(dto.getCoinAmount())
                .build();

        // 정보저장
        paymentRepository.save(payment);

        // 프론트에서 Toss 결제 위젯에 넘길 정보
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("amount", dto.getAmount());
        result.put("coinAmount", dto.getCoinAmount());
        return result;
    } // end of createOrder

    // 2. 결제 검증 + 코인 충전
    @Transactional
    public PaymentResponseDto confirmPayment(PaymentConfirmRequestDto dto, String email) {

        // 1. DB에서 주문 조회
        Payment payment = paymentRepository.findByOrderId(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));

        // 2. 금액 위변조 검증 ← 핵심
        if (payment.getAmount() != dto.getAmount()) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        // 3. Toss API 에 최종 승인 요청
        confirmTossPayment(dto.getPaymentKey(), dto.getOrderId(), dto.getAmount());

        // 4. 결제 완료 처리
        payment.setPaymentKey(dto.getPaymentKey());
        payment.setStatus("DONE");
        payment.setPaidAt(LocalDateTime.now());

        // 5. 코인 충전
        User user = payment.getUser();
        user.setCoin(user.getCoin() + payment.getCoinAmount());

        return new PaymentResponseDto(payment);
    } // end of confirmPayment

    // 3. 유료 글 코인 차감 열람
    @Transactional
    public BoardResponseDto unlockBoard(Long boardId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Board board = boardRepository.findByIdAndDeletedAtIsNull(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 유료 글인지 확인
        if (!board.isPaid()) {
            throw new IllegalArgumentException("무료 게시글입니다.");
        }

        // 이미 열람했는지 확인
        if (unlockHistoryRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalArgumentException("이미 열람한 게시글입니다.");
        }

        // 코인 확인
        if (user.getCoin() < 1) {
            throw new IllegalArgumentException("코인이 부족합니다.");
        }

        // 코인 차감
        user.setCoin(user.getCoin() - 1);

        // 열람 기록 저장
        UnlockHistory history = UnlockHistory.builder()
                .user(user)
                .board(board)
                .build();
        unlockHistoryRepository.save(history);

        return new BoardResponseDto(board);
    }

    // 4. 결제 내역 조회
    public Page<PaymentResponseDto> getPaymentHistory(String email, int page) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Pageable pageable = PageRequest.of(page, 10,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return paymentRepository.findByUserAndStatus(user, "DONE", pageable)
                .map(PaymentResponseDto::new);
    }

    // Toss API 승인 요청 (내부 메서드)
    private void confirmTossPayment(String paymentKey, String orderId, int amount) {

        RestTemplate restTemplate = new RestTemplate();

        // Toss 인증 방식: Base64(시크릿키:) 를 Basic 인증으로 전송
        String encoded = Base64.getEncoder()
                .encodeToString((tossSecretKey + ":").getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + encoded);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId",    orderId);
        body.put("amount",     amount);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(tossConfirmUrl, request, String.class);
        } catch (HttpClientErrorException e) {
            // ← Toss API 에서 4xx 응답 왔을 때
            throw new IllegalArgumentException(
                    "Toss 결제 승인 실패: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            // ← 그 외 에러
            throw new IllegalArgumentException(
                    "Toss 결제 승인 실패: " + e.getMessage());
        }
    }
}



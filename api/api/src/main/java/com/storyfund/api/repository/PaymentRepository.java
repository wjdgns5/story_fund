package com.storyfund.api.repository;

import com.storyfund.api.entity.Payment;
import com.storyfund.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // orderId 로 주문 조회
    Optional<Payment> findByOrderId(String orderId);

    // 유저의 결제 내역 조회 (최신순)
    Page<Payment> findByUserAndStatus(User user, String status, Pageable pageable);
}

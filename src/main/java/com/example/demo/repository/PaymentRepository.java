package com.example.demo.repository;

import com.example.demo.entity.Payment;
import com.example.demo.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderOrderId(Long orderId);

    // ✅ Lấy danh sách thanh toán theo trạng thái
    List<Payment> findByPaymentStatus(PaymentStatus status);
}

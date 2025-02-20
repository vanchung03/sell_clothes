package com.example.demo.repository;

import com.example.demo.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    List<PaymentHistory> findByPaymentPaymentId(Long paymentId);
}

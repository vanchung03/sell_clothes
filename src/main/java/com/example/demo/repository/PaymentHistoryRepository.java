package com.example.demo.repository;

import com.example.demo.entity.Payment;
import com.example.demo.entity.PaymentHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    @EntityGraph(attributePaths = {"payment", "payment.order", "payment.method"})
    List<PaymentHistory> findByPaymentIn(List<Payment> payments);
}

package com.example.demo.service;

import com.example.demo.dto.PaymentHistoryDTO;
import com.example.demo.entity.PaymentHistory;
import com.example.demo.entity.Order;
import com.example.demo.entity.Payment;
import com.example.demo.mapper.PaymentHistoryMapper;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.PaymentHistoryRepository;
import com.example.demo.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentHistoryService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Transactional
    public List<PaymentHistoryDTO> getUserPaymentHistory(Long userId) {
        List<Order> userOrders = orderRepository.findByUserUserId(userId);
        if (userOrders.isEmpty()) {
            return List.of();
        }

        List<Payment> payments = paymentRepository.findByOrderIn(userOrders);
        if (payments.isEmpty()) {
            return List.of();
        }

        List<PaymentHistory> paymentHistories = paymentHistoryRepository.findByPaymentIn(payments);

        // 🚀 Dùng MapStruct thay vì tự ánh xạ
        return paymentHistories.stream()
                .map(PaymentHistoryMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }
    // ✅ 1. Lấy tất cả giao dịch thanh toán
    @Transactional
    public List<PaymentHistoryDTO> getAllPaymentHistories() {
        List<PaymentHistory> paymentHistories = paymentHistoryRepository.findAll();
        return paymentHistories.stream()
                .map(PaymentHistoryMapper.INSTANCE::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ 2. Xóa một giao dịch thanh toán theo ID
    @Transactional
    public void deletePaymentHistory(Long historyId) {
        if (!paymentHistoryRepository.existsById(historyId)) {
            throw new RuntimeException("Không tìm thấy giao dịch có ID: " + historyId);
        }
        paymentHistoryRepository.deleteById(historyId);
    }
}

package com.example.demo.controller;

import com.example.demo.dto.PaymentHistoryDTO;
import com.example.demo.service.PaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/payments-history")
public class PaymentHistoryController {
    @Autowired
    private PaymentHistoryService paymentHistoryService;

    @GetMapping("/user/{userId}/payment-history")
    public ResponseEntity<List<PaymentHistoryDTO>> getUserPaymentHistory(@PathVariable Long userId) {
        List<PaymentHistoryDTO> paymentHistory = paymentHistoryService.getUserPaymentHistory(userId);
        return ResponseEntity.ok(paymentHistory);
    }
    // ✅ Lấy tất cả giao dịch thanh toán
    @GetMapping("/all")
    public ResponseEntity<List<PaymentHistoryDTO>> getAllPaymentHistories() {
        List<PaymentHistoryDTO> paymentHistories = paymentHistoryService.getAllPaymentHistories();
        return ResponseEntity.ok(paymentHistories);
    }

    // ✅ Xóa một giao dịch theo ID
    @DeleteMapping("/{historyId}")
    public ResponseEntity<String> deletePaymentHistory(@PathVariable Long historyId) {
        paymentHistoryService.deletePaymentHistory(historyId);
        return ResponseEntity.ok("Đã xóa giao dịch có ID: " + historyId);
    }

}

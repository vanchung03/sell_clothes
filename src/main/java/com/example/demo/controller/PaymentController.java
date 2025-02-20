package com.example.demo.controller;

import com.example.demo.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ✅ API Xử Lý Thanh Toán (Hỗ trợ VNPay, MoMo, Tiền Mặt)
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<String> createPayment(@PathVariable Long orderId, @RequestParam String methodCode) {
        String response = paymentService.createPayment(orderId, methodCode);
        return ResponseEntity.ok(response);
    }

    // ✅ API Nhận Callback từ VNPay
    @GetMapping("/vnpay-payment")
    public ResponseEntity<String> handleVNPayCallback(HttpServletRequest request) {
        String orderId = request.getParameter("vnp_OrderInfo");
        String transactionStatus = request.getParameter("vnp_TransactionStatus");

        String result = paymentService.updatePaymentStatus(Long.valueOf(orderId), transactionStatus);
        return ResponseEntity.ok(result);
    }
}

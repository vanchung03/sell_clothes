package com.example.demo.controller;

import com.example.demo.dto.PaymentMethodDTO;
import com.example.demo.service.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/payment-methods")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @PostMapping
    public ResponseEntity<PaymentMethodDTO> createPaymentMethod(@RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodDTO createdPaymentMethod = paymentMethodService.createPaymentMethod(paymentMethodDTO);
        return ResponseEntity.ok(createdPaymentMethod);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PaymentMethodDTO> updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethodDTO paymentMethodDTO) {
        PaymentMethodDTO updatedPaymentMethod = paymentMethodService.updatePaymentMethod(id, paymentMethodDTO);
        return updatedPaymentMethod != null ? ResponseEntity.ok(updatedPaymentMethod) : ResponseEntity.notFound().build();
    }
    @GetMapping
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        return paymentMethodService.getAllPaymentMethods();
    }
    // Tìm kiếm PaymentMethod theo ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodDTO> getPaymentMethodById(@PathVariable Long id) {
        PaymentMethodDTO paymentMethodDTO = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(paymentMethodDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.noContent().build();
    }
}

package com.example.demo.controller.report;

import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class StatisticsController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    // 1. Tổng doanh thu (các đơn hàng đã hoàn thành)
    @GetMapping("/total-revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        double totalRevenue = orderRepository.sumTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

    // 2. Tổng số đơn hàng
    @GetMapping("/total-orders")
    public ResponseEntity<Long> getTotalOrders() {
        long totalOrders = orderRepository.count();
        return ResponseEntity.ok(totalOrders);
    }
}

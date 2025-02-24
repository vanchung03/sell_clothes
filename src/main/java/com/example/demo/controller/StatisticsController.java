package com.example.demo.controller;

import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class StatisticsController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    // ✅ Tổng doanh thu
    @GetMapping("/total-revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        double totalRevenue = orderRepository.sumTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

    // ✅ Tổng số đơn hàng
    @GetMapping("/total-orders")
    public ResponseEntity<Long> getTotalOrders() {
        long totalOrders = orderRepository.count();
        return ResponseEntity.ok(totalOrders);
    }

    // ✅ Tỷ lệ đơn hàng theo trạng thái
    @GetMapping("/order-status-ratio")
    public ResponseEntity<List<Object[]>> getOrderStatusRatio() {
        return ResponseEntity.ok(orderRepository.getOrderStatusStatistics());
    }

    // ✅ Phương thức thanh toán phổ biến
    @GetMapping("/popular-payment-methods")
    public ResponseEntity<List<Object[]>> getPopularPaymentMethods() {
        return ResponseEntity.ok(orderRepository.getPaymentMethodStatistics());
    }

    // ✅ Sản phẩm bán chạy nhất
    @GetMapping("/top-products")
    public ResponseEntity<List<Object[]>> getTopSellingProducts() {
        return ResponseEntity.ok(productRepository.getTopSellingProducts());
    }

    // ✅ Doanh thu theo ngày
    @GetMapping("/revenue-by-date")
    public ResponseEntity<List<Object[]>> getRevenueByDate() {
        return ResponseEntity.ok(orderRepository.getRevenueByDate());
    }
}

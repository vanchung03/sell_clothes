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

    // 3. Tỷ lệ đơn hàng theo trạng thái
    @GetMapping("/order-status-ratio")
    public ResponseEntity<List<Object[]>> getOrderStatusRatio() {
        return ResponseEntity.ok(orderRepository.getOrderStatusStatistics());
    }

    // 4. Phương thức thanh toán phổ biến
    @GetMapping("/popular-payment-methods")
    public ResponseEntity<List<Object[]>> getPopularPaymentMethods() {
        return ResponseEntity.ok(orderRepository.getPaymentMethodStatistics());
    }

    // 5. Top 5 sản phẩm bán chạy nhất
    @GetMapping("/top-products")
    public ResponseEntity<List<Object[]>> getTopSellingProducts() {
        return ResponseEntity.ok(productRepository.getTopSellingProducts());
    }

    // 6. Doanh thu theo ngày
    @GetMapping("/revenue-by-date")
    public ResponseEntity<List<Object[]>> getRevenueByDate() {
        return ResponseEntity.ok(orderRepository.getRevenueByDate());
    }

    // 7. Doanh thu theo tháng
    @GetMapping("/revenue-by-month")
    public ResponseEntity<List<Object[]>> getRevenueByMonth() {
        return ResponseEntity.ok(orderRepository.getRevenueByMonth());
    }

    // 8. Thống kê tình trạng tồn kho của các biến thể sản phẩm
    @GetMapping("/product-variants-stock")
    public ResponseEntity<List<Object[]>> getProductVariantsStock() {
        return ResponseEntity.ok(productVariantRepository.getStockStatistics());
    }
}

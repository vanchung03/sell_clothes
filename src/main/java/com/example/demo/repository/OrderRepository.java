package com.example.demo.repository;

import com.example.demo.entity.Order;
import com.example.demo.enums.OrderStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserUserId(Long userId);
    @EntityGraph(attributePaths = {"orderItems", "orderItems.variant"}) // ✅ Ép tải orderItems ngay từ query
    Optional<Order> findById(Long orderId);
    // ✅ Tổng doanh thu (sum totalAmount của tất cả đơn hàng đã hoàn thành)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    double sumTotalRevenue();

    // ✅ Thống kê số lượng đơn hàng theo trạng thái
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusStatistics();

    // ✅ Thống kê phương thức thanh toán phổ biến
    @Query("SELECT p.method, COUNT(p) FROM Payment p GROUP BY p.method")
    List<Object[]> getPaymentMethodStatistics();

    // ✅ Thống kê doanh thu theo ngày
    @Query("SELECT DATE(o.createdAt), SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' GROUP BY DATE(o.createdAt)")
    List<Object[]> getRevenueByDate();
}

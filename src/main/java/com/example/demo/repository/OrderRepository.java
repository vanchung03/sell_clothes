package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Sử dụng fetch join để nạp luôn thông tin User, ShipMethod và OrderItems (với variant)
    @Query("SELECT o FROM Order o " +
            "JOIN FETCH o.user " +
            "JOIN FETCH o.shipMethod " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.variant " +
            "WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    List<Order> findByUserUserId(Long userId);

    @EntityGraph(attributePaths = {"orderItems", "orderItems.variant"})
    Optional<Order> findById(Long orderId);

    // Truy vấn lấy danh sách đơn hàng theo khoảng thời gian
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :from AND :to")
    List<Order> findOrdersInRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);


    // 📌 Lấy doanh thu theo tháng
    @Query("SELECT o FROM Order o WHERE FUNCTION('YEAR', o.createdAt) = :year " +
            "AND FUNCTION('MONTH', o.createdAt) = :month")
    List<Order> findOrdersByMonth(@Param("year") int year, @Param("month") int month);







    // Tổng doanh thu (chỉ tính các đơn hàng đã hoàn thành)
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status = 'COMPLETED'")
    double sumTotalRevenue();

    // Thống kê số lượng đơn hàng theo trạng thái
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> getOrderStatusStatistics();

    // Thống kê phương thức thanh toán phổ biến (giả sử Payment là entity liên quan)
    @Query("SELECT p.method, COUNT(p) FROM Payment p GROUP BY p.method")
    List<Object[]> getPaymentMethodStatistics();

    // Doanh thu theo ngày: sử dụng hàm JPQL FUNCTION (có thể thay bằng native query nếu cần)
    @Query("SELECT FUNCTION('DATE', o.createdAt), SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' GROUP BY FUNCTION('DATE', o.createdAt)")
    List<Object[]> getRevenueByDate();

    // Doanh thu theo tháng: sử dụng hàm JPQL FUNCTION
    @Query("SELECT FUNCTION('MONTH', o.createdAt), SUM(o.totalAmount) FROM Order o WHERE o.status = 'COMPLETED' GROUP BY FUNCTION('MONTH', o.createdAt)")
    List<Object[]> getRevenueByMonth();
}

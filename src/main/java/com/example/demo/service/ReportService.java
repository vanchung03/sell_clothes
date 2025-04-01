package com.example.demo.service;

import com.example.demo.dto.report.DailyRevenueDTO;
import com.example.demo.dto.report.MonthlyRevenueDTO;
import com.example.demo.dto.report.ProductRevenueDTO;
import com.example.demo.dto.report.RevenueReportDTO;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final OrderRepository orderRepository;

    public ReportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public RevenueReportDTO getRevenueReport(LocalDate fromDate, LocalDate toDate) {
        List<Order> orders = orderRepository.findOrdersInRange(
                fromDate.atStartOfDay(),
                toDate.atTime(23, 59, 59)
        );
        return processDailyRevenueReport(orders);
    }

    @Transactional
    public RevenueReportDTO getMonthlyRevenue(int year, int month) {
        List<Order> orders = orderRepository.findOrdersByMonth(year, month);
        return processMonthlyRevenueReport(year, month, orders);
    }

    // 📌 Xử lý báo cáo doanh thu theo ngày
    private RevenueReportDTO processDailyRevenueReport(List<Order> orders) {
        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        List<ProductRevenueDTO> productRevenueList = calculateProductRevenue(orders);

        // ✅ Tính doanh thu theo từng ngày
        List<DailyRevenueDTO> dailyRevenueList = orders.stream()
                .collect(Collectors.groupingBy(order -> order.getCreatedAt().toLocalDate(),
                        Collectors.summingDouble(Order::getTotalAmount)))
                .entrySet().stream()
                .map(entry -> new DailyRevenueDTO(entry.getKey(), BigDecimal.valueOf(entry.getValue())))
                .collect(Collectors.toList());

        return new RevenueReportDTO(totalRevenue, productRevenueList, dailyRevenueList,null);
    }

    // 📌 Xử lý báo cáo doanh thu theo tháng
    private RevenueReportDTO processMonthlyRevenueReport(int year, int month, List<Order> orders) {
        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        List<ProductRevenueDTO> productRevenueList = calculateProductRevenue(orders);

        // ✅ Tính doanh thu theo tháng
        MonthlyRevenueDTO monthlyRevenue = new MonthlyRevenueDTO(month, year, totalRevenue);

        return new RevenueReportDTO(totalRevenue, productRevenueList, null, List.of(monthlyRevenue));
    }

    // 📌 Tính tổng doanh thu
    private BigDecimal calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .map(order -> BigDecimal.valueOf(order.getTotalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // 📌 Tính doanh thu theo sản phẩm
    private List<ProductRevenueDTO> calculateProductRevenue(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getVariant().getProduct(),
                        Collectors.summingDouble(OrderItem::getTotalPrice)))
                .entrySet().stream()
                .map(entry -> new ProductRevenueDTO(
                        entry.getKey().getProductId(),
                        entry.getKey().getName(),
                        (int) orders.stream()
                                .flatMap(order -> order.getOrderItems().stream())
                                .filter(item -> item.getVariant().getProduct().equals(entry.getKey()))
                                .mapToInt(OrderItem::getQuantity).sum(),
                        BigDecimal.valueOf(entry.getValue())
                ))
                .collect(Collectors.toList());
    }
}

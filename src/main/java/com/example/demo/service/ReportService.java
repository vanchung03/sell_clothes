package com.example.demo.service;

import com.example.demo.dto.report.DailyRevenueDTO;
import com.example.demo.dto.report.MonthlyRevenueDTO;
import com.example.demo.dto.report.ProductRevenueDTO;
import com.example.demo.dto.report.RevenueReportDTO;
import com.example.demo.dto.OrderDTO;              // import thêm
import com.example.demo.dto.OrderItemDTO;         // import thêm

import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
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
        RevenueReportDTO report = processDailyRevenueReport(orders);

        // Set thêm danh sách OrderDTO
        report.setOrders(convertToOrderDTO(orders));

        return report;
    }

    @Transactional
    public RevenueReportDTO getMonthlyRevenue(int year, int month) {
        List<Order> orders = orderRepository.findOrdersByMonth(year, month);
        RevenueReportDTO report = processMonthlyRevenueReport(year, month, orders);

        // Set thêm danh sách OrderDTO
        report.setOrders(convertToOrderDTO(orders));

        return report;
    }

    // ---------------------------
    //   CÁC HÀM XỬ LÝ THỐNG KÊ
    // ---------------------------

    // Xử lý báo cáo doanh thu theo ngày
    private RevenueReportDTO processDailyRevenueReport(List<Order> orders) {
        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        List<ProductRevenueDTO> productRevenueList = calculateProductRevenue(orders);

        // Tính doanh thu theo từng ngày
        List<DailyRevenueDTO> dailyRevenueList = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> order.getCreatedAt().toLocalDate(),
                        Collectors.summingDouble(Order::getTotalAmount)
                ))
                .entrySet()
                .stream()
                .map(entry ->
                        new DailyRevenueDTO(
                                entry.getKey(),
                                BigDecimal.valueOf(entry.getValue())
                        )
                )
                .collect(Collectors.toList());

        // Trả về DTO (chưa có orders, tí nữa set sau)
        return new RevenueReportDTO(
                totalRevenue,
                productRevenueList,
                dailyRevenueList,
                null // monthlyRevenueList
        );
    }

    // Xử lý báo cáo doanh thu theo tháng
    private RevenueReportDTO processMonthlyRevenueReport(int year, int month, List<Order> orders) {
        BigDecimal totalRevenue = calculateTotalRevenue(orders);
        List<ProductRevenueDTO> productRevenueList = calculateProductRevenue(orders);

        // Tính doanh thu theo tháng
        MonthlyRevenueDTO monthlyRevenue = new MonthlyRevenueDTO(month, year, totalRevenue);

        // Trả về DTO (chưa có orders, tí nữa set sau)
        return new RevenueReportDTO(
                totalRevenue,
                productRevenueList,
                null, // dailyRevenueList
                List.of(monthlyRevenue)
        );
    }

    // Tính tổng doanh thu
    private BigDecimal calculateTotalRevenue(List<Order> orders) {
        return orders.stream()
                .map(order -> BigDecimal.valueOf(order.getTotalAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Tính doanh thu theo sản phẩm
    private List<ProductRevenueDTO> calculateProductRevenue(List<Order> orders) {
        return orders.stream()
                .flatMap(order -> order.getOrderItems().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getVariant().getProduct(),
                        Collectors.summingDouble(OrderItem::getTotalPrice)
                ))
                .entrySet()
                .stream()
                .map(entry -> new ProductRevenueDTO(
                        entry.getKey().getProductId(),
                        entry.getKey().getName(),
                        (int) orders.stream()
                                .flatMap(order -> order.getOrderItems().stream())
                                .filter(item -> item.getVariant().getProduct().equals(entry.getKey()))
                                .mapToInt(OrderItem::getQuantity)
                                .sum(),
                        BigDecimal.valueOf(entry.getValue())
                ))
                .collect(Collectors.toList());
    }

    // ---------------------------
    //  CHUYỂN ĐỔI Order -> DTO
    // ---------------------------
    private List<OrderDTO> convertToOrderDTO(List<Order> orders) {
        return orders.stream().map(order -> {
            OrderDTO dto = new OrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setUserId(order.getUser().getUserId());
            dto.setAddressId(order.getAddress().getAddressId());
            dto.setShipMethodId(order.getShipMethod().getShip_method_id());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setStatus(String.valueOf(order.getStatus()));
            dto.setVoucherCode(order.getVoucherCode());
            dto.setDiscountAmount(order.getDiscountAmount());
            dto.setCreatedAt(order.getCreatedAt());


            // Lấy orderItems
            List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                    .map(orderItem -> {
                        OrderItemDTO itemDTO = new OrderItemDTO();
                        itemDTO.setOrderId(order.getOrderId());
                        itemDTO.setVariantId(orderItem.getVariant().getVariantId());
                        itemDTO.setQuantity(orderItem.getQuantity());
                        itemDTO.setUnitPrice(orderItem.getUnitPrice());
                        itemDTO.setTotalPrice(orderItem.getTotalPrice());
                        return itemDTO;
                    })
                    .collect(Collectors.toList());

            dto.setOrderItems(itemDTOs);

            return dto;
        }).collect(Collectors.toList());
    }
}

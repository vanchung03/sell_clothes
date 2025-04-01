package com.example.demo.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private Long addressId;
    private Long shipMethodId;
    private List<OrderItemDTO> orderItems = new ArrayList<>();
    private Double totalAmount;
    private String status;

    private String voucherCode; // ✅ Lưu mã giảm giá (nếu có)
    private Double discountAmount; // ✅ Lưu số tiền giảm giá áp dụng
}

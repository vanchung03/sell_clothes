package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private Long addressId;
    private List<OrderItemDTO> orderItems;  // ✅ Không để null
    private Double totalAmount;
    private Double shippingFee;
    private String status;
}

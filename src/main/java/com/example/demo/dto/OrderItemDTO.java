package com.example.demo.dto;
import lombok.Data;

@Data
public class OrderItemDTO {
    private Long orderId;
    private Long variantId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
}

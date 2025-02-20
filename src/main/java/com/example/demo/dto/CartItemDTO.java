package com.example.demo.dto;

import lombok.Data;

@Data
public class CartItemDTO {
    private Long cartItemId;
    private Long cartId;
    private Long variantId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
}

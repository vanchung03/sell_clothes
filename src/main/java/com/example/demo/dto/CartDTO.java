package com.example.demo.dto;

import lombok.Data;
import java.util.List;

@Data
public class CartDTO {
    private Long cartId;
    private Long userId;
    private List<CartItemDTO> cartItems;
}

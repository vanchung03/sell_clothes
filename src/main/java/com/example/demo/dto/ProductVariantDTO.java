package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDTO {
    private Long variantId;
    private Long productId;
    private String size;
    private String color;
    private String sku;
    private double price;
    private int stockQuantity;
    private String imageUrl;
    private boolean status;
}

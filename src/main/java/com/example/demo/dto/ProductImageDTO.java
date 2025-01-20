package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDTO {
    private Long imageId;
    private Long productId;
    private String imageUrl;
    private boolean isPrimary;
    private int displayOrder;
}

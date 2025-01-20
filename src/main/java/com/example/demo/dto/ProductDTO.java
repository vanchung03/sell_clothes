package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long productId;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal salePrice;
    private String thumbnail;
    private boolean status;
    private LocalDate createdAt ;
    private LocalDate updatedAt ;
}

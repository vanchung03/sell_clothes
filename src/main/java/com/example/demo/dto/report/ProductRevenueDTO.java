package com.example.demo.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductRevenueDTO {
    private Long productId;
    private String productName;
    private int totalQuantitySold;
    private BigDecimal totalRevenue;
}

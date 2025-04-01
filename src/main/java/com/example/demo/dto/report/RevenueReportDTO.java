package com.example.demo.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class RevenueReportDTO {
    private BigDecimal totalRevenue; // Tổng doanh thu
    private List<ProductRevenueDTO> productRevenueList; // Doanh thu theo sản phẩm
    private List<DailyRevenueDTO> dailyRevenueList; // Doanh thu theo từng ngày
    private List<MonthlyRevenueDTO>monthlyRevenueDTOList;


}

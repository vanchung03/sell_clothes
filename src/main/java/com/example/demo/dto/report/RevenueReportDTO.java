package com.example.demo.dto.report;

import com.example.demo.dto.OrderDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportDTO {

    private BigDecimal totalRevenue;
    private List<ProductRevenueDTO> productRevenueList;
    private List<DailyRevenueDTO> dailyRevenueList;
    private List<MonthlyRevenueDTO> monthlyRevenueList;

    // Thêm trường danh sách đơn hàng
    private List<OrderDTO> orders; // <-- Mới thêm

    // Bạn có thể tạo constructor khác cho tiện nếu muốn
    public RevenueReportDTO(
            BigDecimal totalRevenue,
            List<ProductRevenueDTO> productRevenueList,
            List<DailyRevenueDTO> dailyRevenueList,
            List<MonthlyRevenueDTO> monthlyRevenueList
    ) {
        this.totalRevenue = totalRevenue;
        this.productRevenueList = productRevenueList;
        this.dailyRevenueList = dailyRevenueList;
        this.monthlyRevenueList = monthlyRevenueList;
    }
}

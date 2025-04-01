package com.example.demo.controller.report;

import com.example.demo.dto.report.RevenueReportDTO;
import com.example.demo.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // 📌 API lấy báo cáo doanh thu theo ngày
    @GetMapping("/revenue")
    public RevenueReportDTO getRevenueReport(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {
        return reportService.getRevenueReport(fromDate, toDate);
    }

    // 📌 API lấy báo cáo doanh thu theo tháng
    @GetMapping("/revenue/monthly")
    public RevenueReportDTO getMonthlyRevenue(
            @RequestParam int year,
            @RequestParam int month) {
        return reportService.getMonthlyRevenue(year, month);
    }
}

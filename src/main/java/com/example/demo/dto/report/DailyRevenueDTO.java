package com.example.demo.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DailyRevenueDTO {
    private LocalDate date;
    private BigDecimal revenue;
}

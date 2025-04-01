package com.example.demo.dto.report;

import java.math.BigDecimal;

public class MonthlyRevenueDTO {
    private int month;
    private int year;
    private BigDecimal revenue;

    public MonthlyRevenueDTO(int month, int year, BigDecimal revenue) {
        this.month = month;
        this.year = year;
        this.revenue = revenue;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }
}

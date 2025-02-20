package com.example.demo.enums;

public enum OrderStatus {
    PENDING,      // Đơn hàng đang chờ xác nhận
    CONFIRMED,    // Đơn hàng đã được xác nhận
    SHIPPING,     // Đơn hàng đang được giao
    COMPLETED,    // Đơn hàng đã giao thành công
    CANCELLED     // Đơn hàng đã bị hủy
}

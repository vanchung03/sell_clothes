package com.example.demo.config;

public class ApiPermissions {

    // API dùng chung cho tất cả vai trò
    public static final String[] SHARED_APIS = {
            "/api/v1/brands/**",
            "/api/v1/categories/**",
            "/api/v1/products/**",
            "/api/v1/orders/**",
            "/api/v1/payments/**",
            "/api/v1/product-images/**"
    };

    // API riêng cho ADMIN
    public static final String[] ADMIN_APIS = {
            "/api/v1/users/**",          // Quản lý người dùng
            "/api/v1/roles/**",          // Quản lý vai trò
            "/api/v1/orders/**",         // Toàn quyền với đơn hàng
            "/api/v1/payments/**",       // Toàn quyền với thanh toán
            "/api/v1/payment-history/**" // Lịch sử thanh toán
    };

    // API riêng cho DEALER
    public static final String[] DEALER_APIS = {
            "/api/v1/products/**",       // Quản lý sản phẩm của họ
            "/api/v1/product-images/**", // Quản lý ảnh sản phẩm
            "/api/v1/orders/**"          // Xem đơn hàng liên quan đến sản phẩm của họ
    };

    // API riêng cho USER
    public static final String[] USER_APIS = {
            "/api/v1/user-addresses/**", // Quản lý địa chỉ của chính họ
            "/api/v1/orders/**",         // Quản lý đơn hàng cá nhân
            "/api/v1/payments/**"        // Xem thanh toán của chính họ
    };
}

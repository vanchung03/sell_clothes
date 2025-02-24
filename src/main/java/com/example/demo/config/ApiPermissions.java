package com.example.demo.config;

public class ApiPermissions {
    public static final String[] SHARED_APIS = {
            "/api/auth/**",
            "/api/v1/brands/**",
            "/api/v1/categories/**",
            "/api/v1/products/**",
            "/api/v1/product_images/**",
            "/api/v1/product_variants/**",
            "/api/v1/payments/callback",
            "/api/v1/payments/**",
            "/api/v1/orders/**"
    };
    public static final String[] ADMIN_APIS = {
            "/api/v1/roles/**",
            "/api/v1/products/**",
            "/api/v1/orders/**",
            "/api/cloudinary",
            "/api/v1/statistics/**",
    };
    public static final String[] USER_APIS = {
            "/api/v1/user-addresses/**",
            "/api/v1/orders/**",
            "/api/v1/payment-methods/**",
            "/api/v1/users/**",
            "/api/v1/payment-history/**",
            "/api/v1/user-addresses/**",
            "/api/v1/cart/**",
            "/api/v1/orders/**",
            "/api/v1/payments/**",
            "/api/v1/order-items"
    };
}

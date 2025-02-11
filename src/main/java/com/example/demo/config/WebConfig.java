package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/auth/**")  // Áp dụng CORS cho tất cả các endpoint
                .allowedOrigins("http://localhost:4200")  // Cho phép frontend từ localhost:4200
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // Các phương thức HTTP được phép
                .allowedHeaders("*")  // Cho phép tất cả các header
                .allowCredentials(true);  // Cho phép gửi cookie
    }
}

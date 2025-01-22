package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email; // Email được liên kết với OTP

    @Column(nullable = false)
    private String otp; // OTP mã hóa

    @Column(nullable = false)
    private LocalDateTime createdAt; // Thời gian tạo OTP

    @Column(nullable = false)
    private LocalDateTime expiredAt; // Thời gian OTP hết hạn

    @Column(nullable = false)
    private Integer attemptCount; // Số lần thử nhập OTP
}

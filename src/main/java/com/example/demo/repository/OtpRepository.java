package com.example.demo.repository;

import com.example.demo.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Otp findByEmail(String email);  // Tìm OTP dựa trên email
}

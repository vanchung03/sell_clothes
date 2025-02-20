package com.example.demo.repository;

import com.example.demo.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Long> {
    Optional<PaymentMethod> findByCode(String code);
}

package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long methodId;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false, length = 50, unique = true)
    private String code;
    private String description;
    private boolean status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt = LocalDate.now();
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt = LocalDate.now();
}

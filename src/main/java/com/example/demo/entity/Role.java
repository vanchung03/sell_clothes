package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    private String name; // ADMIN, DEALER, USER

    private String description;

    @Column(updatable = false)
    private LocalDate createdAt = LocalDate.now();

    private LocalDate updatedAt = LocalDate.now();
}

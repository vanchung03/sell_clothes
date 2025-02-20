package com.example.demo.entity;

import com.example.demo.enums.RoleName;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName name;

    private String description;

    @Column(updatable = false)
    private LocalDate createdAt = LocalDate.now();

    private LocalDate updatedAt = LocalDate.now();
}

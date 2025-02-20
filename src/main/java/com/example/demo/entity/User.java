package com.example.demo.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Users")
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String phone;
    private String avatar;

    @Column(updatable = false)
    private LocalDate createdAt = LocalDate.now();
    @Column
    private LocalDate updatedAt = LocalDate.now();
    private int status = 1; // 1: active, 0: inactive, 2: banned

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "User_Roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}

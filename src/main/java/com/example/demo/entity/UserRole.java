package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "User_Roles")
@IdClass(UserRoleId.class)
public class UserRole implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @Id
    @ManyToOne
    @JoinColumn(name = "role_id")
    @JsonIgnore
    private Role role;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}


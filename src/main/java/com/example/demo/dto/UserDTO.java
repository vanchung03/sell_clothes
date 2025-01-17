package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String phone;
    private String avatar;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int status;
    private Set<String> roles;
}
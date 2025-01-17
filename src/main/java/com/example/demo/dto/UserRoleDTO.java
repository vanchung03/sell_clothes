package com.example.demo.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRoleDTO {
    private Long userId;
    private Long roleId;
    private LocalDateTime createdAt;
}

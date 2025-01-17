package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RoleDTO {
    private Long roleId;
    private String name;
    private String description;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}

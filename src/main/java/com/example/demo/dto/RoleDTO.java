package com.example.demo.dto;

import com.example.demo.enums.RoleName;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RoleDTO {
    private Long roleId;
    private RoleName name;
    private String description;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}

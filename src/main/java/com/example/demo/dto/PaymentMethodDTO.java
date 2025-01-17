package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodDTO {
    private Long methodId;
    private String name;
    private String code;
    private String description;
    private boolean status;
    private String createdAt;
    private String updatedAt;
}

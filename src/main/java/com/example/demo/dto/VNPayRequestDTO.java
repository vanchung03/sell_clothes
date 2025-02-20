package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VNPayRequestDTO {
    private Long orderId;
    private double amount;
}

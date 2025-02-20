package com.example.demo.dto;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long paymentId;
    private Long orderId;
    private String method;
    private double amount;
    private String transactionCode;
    private String paymentStatus;
}

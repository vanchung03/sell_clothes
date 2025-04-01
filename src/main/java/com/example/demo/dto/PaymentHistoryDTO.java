package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class PaymentHistoryDTO {
    private Long historyId;
    private Long paymentId;
    private Long orderId;
    private String orderStatus;
    private Double amount;
    private String transactionCode;
    private String paymentMethod;
    private String status;
    private String note;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
package com.example.demo.entity;

import com.example.demo.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voucherId;

    @Column(unique = true, nullable = false)
    private String voucherCode;

    private Double discountAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private Double maxDiscount;
    private int quantity;

    @JsonProperty("active")
    private boolean active;
}


package com.example.demo.dto;

import com.example.demo.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class VoucherDTO {
    private Long voucherId;
    private String voucherCode;
    private Double discountAmount;
    private String discountType;
    private Double maxDiscount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    private int quantity; // ✅ Thêm số lượng voucher
    @JsonProperty("active") // ✅ Đảm bảo JSON trả về "active"
    private boolean active; // ✅ Đổi từ `isActive` thành `active`
}

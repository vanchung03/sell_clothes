package com.example.demo.entity;

import com.example.demo.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "address_id", nullable = true)
    private UserAddress address;

    @JsonManagedReference
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "ship_method_id", nullable = false)
    private ShipMethod shipMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // ✅ Lưu mã giảm giá thay vì FK đến Voucher
    private String voucherCode;

    // ✅ Lưu số tiền giảm giá áp dụng
    private Double discountAmount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    // ✅ Tính phí vận chuyển
    public Double getShippingFee() {
        return (shipMethod != null) ? shipMethod.getShippingFee() : 0.0;
    }
}

package com.example.demo.entity;

import com.example.demo.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // ✅ Đánh dấu quan hệ cha
    private List<OrderItem> orderItems;  // ✅ Đảm bảo không null khi ánh xạ

    private Double totalAmount;
    private Double shippingFee;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}

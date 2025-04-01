package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ship_method")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShipMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ship_method_id; // ID tự động tăng

    @Column(nullable = false, unique = true)
    private String name; // Tên phương thức vận chuyển

    @Column(nullable = false)
    private String description; // Mô tả phương thức vận chuyển

    @Column(nullable = false)
    private Double shippingFee; // Phí vận chuyển
}

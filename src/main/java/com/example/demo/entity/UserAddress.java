package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "User_Addresses")
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String addressLine;

    private String city;
    private String district;
    private String ward;

    @Column(columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isDefault;
}

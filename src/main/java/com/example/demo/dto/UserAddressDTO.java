package com.example.demo.dto;

import lombok.Data;

@Data
public class UserAddressDTO {
    private Long addressId;
    private Long userId;
    private String addressLine;
    private String city;
    private String district;
    private String ward;
    private boolean isDefault;
}

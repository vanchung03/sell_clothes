package com.example.demo.dto;
import lombok.Data;
@Data
public class BrandDTO {
    private Long brandId;
    private String name;
    private String logoUrl;
    private String description;
    private boolean status;
}

package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteProductDTO {
    private Long favoriteId;
    private Long userId;
    private Long productId;
    private LocalDateTime createdAt;
}

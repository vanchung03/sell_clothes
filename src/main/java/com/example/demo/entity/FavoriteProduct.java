package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@Table(name = "favorite_products",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class FavoriteProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Long favoriteId;

    @ManyToOne
    @JoinColumn(name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_Favorite_User"))
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_Favorite_Product"))
    private Product product;

    @Column(name = "created_at",
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    // Constructors
    public FavoriteProduct() {}

    public FavoriteProduct(User user, Product product) {
        this.user = user;
        this.product = product;
        this.createdAt = java.time.LocalDateTime.now();
    }

}

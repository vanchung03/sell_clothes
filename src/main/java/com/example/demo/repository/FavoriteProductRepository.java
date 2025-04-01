package com.example.demo.repository;

import com.example.demo.entity.FavoriteProduct;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {
    // Tìm theo user và product
    Optional<FavoriteProduct> findByUserAndProduct(User user, Product product);

    // Danh sách sản phẩm yêu thích theo user
    List<FavoriteProduct> findByUser(User user);
}

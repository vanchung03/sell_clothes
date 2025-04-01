package com.example.demo.repository;

import com.example.demo.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProduct_ProductId(Long productId);
    List<ProductReview> findByUser_UserId(Long userId);
}

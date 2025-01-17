package com.example.demo.repository;

import com.example.demo.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    // Truy vấn tất cả hình ảnh của sản phẩm theo productId
    List<ProductImage> findByProduct_ProductId(Long productId);
}

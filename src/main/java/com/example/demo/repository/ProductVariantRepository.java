package com.example.demo.repository;

import com.example.demo.entity.Brand;
import com.example.demo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProduct_ProductId(Long productId);
    @Query("SELECT v.product.brand FROM ProductVariant v WHERE v.variantId = :variantId")
    Brand findBrandByVariantId(@Param("variantId") Long variantId);

}

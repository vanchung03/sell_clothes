package com.example.demo.repository;

import com.example.demo.entity.Brand;
import com.example.demo.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProduct_ProductId(Long productId);
    @Query("SELECT v.product.brand FROM ProductVariant v WHERE v.variantId = :variantId")
    Brand findBrandByVariantId(@Param("variantId") Long variantId);

    // Thống kê số lượng biến thể theo tình trạng hàng tồn
    // Nếu stockQuantity = 0 thì xem là "OUT_OF_STOCK", ngược lại "IN_STOCK"
    @Query("SELECT CASE WHEN pv.stockQuantity = 0 THEN 'OUT_OF_STOCK' ELSE 'IN_STOCK' END, COUNT(pv) " +
            "FROM ProductVariant pv GROUP BY CASE WHEN pv.stockQuantity = 0 THEN 'OUT_OF_STOCK' ELSE 'IN_STOCK' END")
    List<Object[]> getStockStatistics();


    // Các method khác...

    @Query("SELECT v FROM ProductVariant v " +
            "WHERE v.product.productId = :productId " +
            "  AND v.size = :size " +
            "  AND v.color = :color " +
            "  AND v.price = :price " +
            "  AND v.stockQuantity = :stockQuantity " +
            "  AND v.status = :status")
    Optional<ProductVariant> findDuplicateVariant(
            @Param("productId") Long productId,
            @Param("size") String size,
            @Param("color") String color,
            @Param("price") Double price,
            @Param("stockQuantity") Integer stockQuantity,
            @Param("status") boolean status
    );

}

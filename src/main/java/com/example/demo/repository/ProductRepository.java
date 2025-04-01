package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
//    // ✅ Lấy danh sách sản phẩm bán chạy nhất
//    @Query("SELECT p.name, SUM(oi.quantity) FROM OrderItem oi " +
//            "JOIN oi.variant v " +
//            "JOIN v.product p " +
//            "GROUP BY p.name " +
//            "ORDER BY SUM(oi.quantity) DESC")
//    List<Object[]> getTopSellingProducts();
//
    // Lấy danh sách sản phẩm theo categoryId
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);


    // Top 5 sản phẩm bán chạy nhất (JOIN giữa order_items, product_variants và products)
    @Query(value = "SELECT p.name, SUM(oi.quantity) as totalSold " +
            "FROM order_items oi " +
            "JOIN product_variants pv ON oi.variant_id = pv.variant_id " +
            "JOIN products p ON pv.product_id = p.product_id " +
            "WHERE oi.order_id IN (SELECT o.order_id FROM orders o WHERE o.status = 'COMPLETED') " +
            "GROUP BY p.product_id " +
            "ORDER BY totalSold DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> getTopSellingProducts();
}

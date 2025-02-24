package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    // ✅ Lấy danh sách sản phẩm bán chạy nhất
    @Query("SELECT p.name, SUM(oi.quantity) FROM OrderItem oi " +
            "JOIN oi.variant v " +
            "JOIN v.product p " +
            "GROUP BY p.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> getTopSellingProducts();
}

package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId AND oi.variant.variantId = :variantId")
    Optional<OrderItem> findByOrderIdAndVariantId(Long orderId, Long variantId);

}

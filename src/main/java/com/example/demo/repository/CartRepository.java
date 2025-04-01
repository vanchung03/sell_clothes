package com.example.demo.repository;

import com.example.demo.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserUserId(Long userId);
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.user.userId = :userId")
    int countCartItemsByUserId(@Param("userId") Long userId);
}

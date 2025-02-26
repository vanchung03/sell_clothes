package com.example.demo.controller;

import com.example.demo.dto.CartDTO;
import com.example.demo.dto.CartItemDTO;
import com.example.demo.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<CartDTO> addCartItem(@PathVariable Long userId, @RequestBody CartItemDTO cartItemDTO) {
        return ResponseEntity.ok(cartService.addItemToCart(userId, cartItemDTO));
    }
    // ✅ Thêm hoặc cập nhật số lượng sản phẩm trong giỏ hàng
    @PutMapping("/{userId}")
    public ResponseEntity<CartDTO> updateCartItem(@PathVariable Long userId, @RequestBody CartItemDTO cartItemDTO) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, cartItemDTO));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
    }
}

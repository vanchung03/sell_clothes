package com.example.demo.controller;

import com.example.demo.dto.FavoriteProductDTO;
import com.example.demo.service.FavoriteProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/favorites")
public class FavoriteProductController {

    @Autowired
    private FavoriteProductService favoriteProductService;

    // Thêm sản phẩm yêu thích
    @PostMapping
    public ResponseEntity<FavoriteProductDTO> addFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        FavoriteProductDTO savedFav = favoriteProductService.addFavorite(userId, productId);
        return ResponseEntity.ok(savedFav);
    }

    // Xóa khỏi danh sách yêu thích
    @DeleteMapping
    public ResponseEntity<String> removeFavorite(
            @RequestParam Long userId,
            @RequestParam Long productId
    ) {
        favoriteProductService.removeFavorite(userId, productId);
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi danh sách yêu thích.");
    }

    // Lấy danh sách sản phẩm yêu thích của user
    @GetMapping("/{userId}")
    public ResponseEntity<List<FavoriteProductDTO>> getUserFavorites(@PathVariable Long userId) {
        List<FavoriteProductDTO> favorites = favoriteProductService.getUserFavorites(userId);
        return ResponseEntity.ok(favorites);
    }
}

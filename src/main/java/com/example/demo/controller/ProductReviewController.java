package com.example.demo.controller;

import com.example.demo.dto.ProductReviewDTO;
import com.example.demo.service.ProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ProductReviewController {

    private final ProductReviewService reviewService;

    // 🟢 [GET] Lấy tất cả đánh giá của sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductReviewDTO>> getReviewsByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    // 🟢 [GET] Lấy tất cả đánh giá của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ProductReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    // 🟢 [POST] Thêm đánh giá
    @PostMapping
    public ResponseEntity<ProductReviewDTO> addReview(@RequestBody ProductReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.addReview(reviewDTO));
    }

    // 🟢 [PUT] Cập nhật (sửa) đánh giá
    @PutMapping("/{reviewId}")
    public ResponseEntity<ProductReviewDTO> updateReview(@PathVariable Long reviewId,
                                                         @RequestBody ProductReviewDTO reviewDTO) {
        ProductReviewDTO updatedReview = reviewService.updateReview(reviewId, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    // 🟢 [DELETE] Xóa đánh giá
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build(); // 204
    }
}

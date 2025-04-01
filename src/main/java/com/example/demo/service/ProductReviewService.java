package com.example.demo.service;

import com.example.demo.dto.ProductReviewDTO;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductReview;
import com.example.demo.entity.User;
import com.example.demo.mapper.ProductReviewMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductReviewRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductReviewMapper reviewMapper;

    // 🟢 Lấy danh sách đánh giá của sản phẩm
    public List<ProductReviewDTO> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProduct_ProductId(productId)
                .stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 🟢 Lấy danh sách đánh giá của một user
    public List<ProductReviewDTO> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUser_UserId(userId)
                .stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    // 🟢 Thêm đánh giá mới
    @Transactional
    public ProductReviewDTO addReview(ProductReviewDTO reviewDTO) {
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));

        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));

        ProductReview review = ProductReview.builder()
                .user(user)
                .product(product)
                .rating(reviewDTO.getRating())
                .comment(reviewDTO.getComment())
                .build();

        return reviewMapper.toDTO(reviewRepository.save(review));
    }
    // 🟢 Hàm cập nhật Review
    @Transactional
    public ProductReviewDTO updateReview(Long reviewId, ProductReviewDTO reviewDTO) {
        ProductReview existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review không tồn tại!"));

        // Cập nhật rating, comment
        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());
        existingReview.setUpdatedAt(java.time.LocalDateTime.now());

        // Nếu cho phép đổi user/product, thì cập nhật thêm:
        /*
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));
        Product product = productRepository.findById(reviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại!"));
        existingReview.setUser(user);
        existingReview.setProduct(product);
        */

        ProductReview saved = reviewRepository.save(existingReview);
        return reviewMapper.toDTO(saved);
    }

    // 🟢 Xóa đánh giá
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review không tồn tại!");
        }
        reviewRepository.deleteById(reviewId);
    }
}

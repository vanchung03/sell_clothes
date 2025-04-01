package com.example.demo.service;

import com.example.demo.dto.FavoriteProductDTO;
import com.example.demo.entity.FavoriteProduct;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.mapper.FavoriteProductMapper;
import com.example.demo.repository.FavoriteProductRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteProductService {

    @Autowired
    private FavoriteProductRepository favoriteProductRepository;

    @Autowired
    private UserRepository userRepository; // Giả sử đã có
    @Autowired
    private ProductRepository productRepository; // Giả sử đã có

    @Autowired
    private FavoriteProductMapper favoriteProductMapper;

    public FavoriteProductDTO addFavorite(Long userId, Long productId) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra đã tồn tại chưa
        Optional<FavoriteProduct> existing = favoriteProductRepository.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            throw new RuntimeException("Sản phẩm đã được yêu thích trước đó!");
        }

        // Tạo mới
        FavoriteProduct newFav = new FavoriteProduct(user, product);
        FavoriteProduct savedFav = favoriteProductRepository.save(newFav);

        return favoriteProductMapper.toDto(savedFav);
    }

    public void removeFavorite(Long userId, Long productId) {
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tìm product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Kiểm tra trong DB
        Optional<FavoriteProduct> existing = favoriteProductRepository.findByUserAndProduct(user, product);
        if (existing.isPresent()) {
            favoriteProductRepository.delete(existing.get());
        } else {
            throw new RuntimeException("Không tìm thấy mục yêu thích để xóa!");
        }
    }

    public List<FavoriteProductDTO> getUserFavorites(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<FavoriteProduct> favorites = favoriteProductRepository.findByUser(user);
        return favorites.stream()
                .map(favoriteProductMapper::toDto)
                .collect(Collectors.toList());
    }
}


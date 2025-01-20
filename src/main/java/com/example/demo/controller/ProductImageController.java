package com.example.demo.controller;

import com.example.demo.dto.ProductImageDTO;
import com.example.demo.entity.ProductImage;
import com.example.demo.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product_images")
public class ProductImageController {
    @Autowired
    private ProductImageService productImageService;

    // Lấy tất cả hình ảnh của sản phẩm theo productId
    @GetMapping("/{productId}")
    public List<ProductImageDTO> getAllProductImages(@PathVariable Long productId) {
        return productImageService.getAllProductImages(productId);
    }

    // Thêm mới hình ảnh sản phẩm
    @PostMapping
    public ProductImageDTO createProductImage(@RequestBody ProductImageDTO productImageDTO) {
        return productImageService.createProductImage(productImageDTO);
    }
    // Cập nhật hình ảnh sản phẩm
    @PutMapping("/{imageId}")
    public ProductImageDTO updateProductImage(@PathVariable Long imageId, @RequestBody ProductImageDTO productImageDTO) {
        return productImageService.updateProductImage(imageId, productImageDTO);
    }
    // Xóa hình ảnh sản phẩm
    @DeleteMapping("/{imageId}")
    public ResponseEntity<?> deleteProductImage(@PathVariable Long imageId) {
        productImageService.deleteProductImage(imageId);
        return ResponseEntity.ok("Product image deletion was successful");
    }
}


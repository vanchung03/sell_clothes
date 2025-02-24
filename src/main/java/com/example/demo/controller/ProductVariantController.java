package com.example.demo.controller;

import com.example.demo.dto.ProductVariantDTO;
import com.example.demo.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/product_variants")
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;

    // Lấy tất cả biến thể sản phẩm theo productId
    @GetMapping("/{productId}")
    public List<ProductVariantDTO> getAllVariantsByProductId(@PathVariable Long productId) {
        return productVariantService.getAllVariantsByProductId(productId);
    }
    // ✅ Lấy thông tin chi tiết của một biến thể sản phẩm theo variantId
    @GetMapping("/variant/{variantId}")
    public ResponseEntity<ProductVariantDTO> getVariantById(@PathVariable Long variantId) {
        return ResponseEntity.ok(productVariantService.getVariantById(variantId));
    }

    // Thêm mới biến thể sản phẩm
    @PostMapping
    public ProductVariantDTO createProductVariant(@RequestBody ProductVariantDTO productVariantDTO) {
        return productVariantService.createProductVariant(productVariantDTO);
    }

    // Cập nhật biến thể sản phẩm
    @PutMapping("/{variantId}")
    public ProductVariantDTO updateProductVariant(@PathVariable Long variantId, @RequestBody ProductVariantDTO productVariantDTO) {
        return productVariantService.updateProductVariant(variantId, productVariantDTO);
    }

    // Xóa biến thể sản phẩm
    @DeleteMapping("/{variantId}")
    public ResponseEntity<?> deleteProductVariant(@PathVariable Long variantId) {
        productVariantService.deleteProductVariant(variantId);
        return ResponseEntity.ok("Product variant deletion was successful");
    }
}

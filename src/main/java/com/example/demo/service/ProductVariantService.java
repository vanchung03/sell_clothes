package com.example.demo.service;

import com.example.demo.dto.ProductVariantDTO;
import com.example.demo.entity.ProductVariant;
import com.example.demo.mapper.ProductVariantMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantMapper productVariantMapper;

    // Lấy tất cả các biến thể của sản phẩm theo productId
    public List<ProductVariantDTO> getAllVariantsByProductId(Long productId) {
        return productVariantRepository.findByProduct_ProductId(productId)
                .stream()
                .map(productVariantMapper::toDTO)
                .toList();
    }

    // Thêm mới biến thể sản phẩm
    public ProductVariantDTO createProductVariant(ProductVariantDTO productVariantDTO) {
        ProductVariant productVariant = productVariantMapper.toEntity(productVariantDTO);
        productVariant.setProduct(productRepository.findById(productVariantDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found")));
        ProductVariant savedVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toDTO(savedVariant);
    }

    // Cập nhật thông tin biến thể sản phẩm
    public ProductVariantDTO updateProductVariant(Long variantId, ProductVariantDTO productVariantDTO) {
        ProductVariant existingVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        existingVariant.setSize(productVariantDTO.getSize());
        existingVariant.setColor(productVariantDTO.getColor());
        existingVariant.setSku(productVariantDTO.getSku());
        existingVariant.setPrice(productVariantDTO.getPrice());
        existingVariant.setStockQuantity(productVariantDTO.getStockQuantity());
        existingVariant.setImageUrl(productVariantDTO.getImageUrl());
        existingVariant.setStatus(productVariantDTO.isStatus());

        ProductVariant updatedVariant = productVariantRepository.save(existingVariant);
        return productVariantMapper.toDTO(updatedVariant);
    }

    // Xóa biến thể sản phẩm
    public void deleteProductVariant(Long variantId) {
        productVariantRepository.deleteById(variantId);
    }
}

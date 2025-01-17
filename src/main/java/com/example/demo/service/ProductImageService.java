package com.example.demo.service;


import com.example.demo.dto.ProductImageDTO;
import com.example.demo.entity.ProductImage;

import com.example.demo.mapper.ProductImageMapper;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageMapper productImageMapper;


    // Lấy tất cả hình ảnh của một sản phẩm theo productId
    public List<ProductImageDTO> getAllProductImages(Long productId) {
        return productImageRepository.findByProduct_ProductId(productId)
                .stream()
                .map(productImageMapper::toDTO)
                .toList();
    }

    // Thêm mới một ảnh sản phẩm
    public ProductImageDTO createProductImage(ProductImageDTO productImageDTO) {
        ProductImage productImage = productImageMapper.toEntity(productImageDTO);
        productImage.setProduct(productRepository.findById(productImageDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found")));
        ProductImage savedImage = productImageRepository.save(productImage);
        return productImageMapper.toDTO(savedImage);
    }

    // Cập nhật thông tin ảnh sản phẩm
    public ProductImageDTO updateProductImage(Long imageId, ProductImageDTO productImageDTO) {
        ProductImage existingProductImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Product image not found"));

        existingProductImage.setImageUrl(productImageDTO.getImageUrl());
        existingProductImage.setPrimary(productImageDTO.isPrimary());
        existingProductImage.setDisplayOrder(productImageDTO.getDisplayOrder());
        ProductImage savedImage = productImageRepository.save(existingProductImage);

        return productImageMapper.toDTO(savedImage);
    }

    // Xóa ảnh sản phẩm
    public void deleteProductImage(Long imageId) {
        productImageRepository.deleteById(imageId);
    }
}

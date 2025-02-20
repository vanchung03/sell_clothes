package com.example.demo.service;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Brand;
import com.example.demo.entity.Category;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CloudinaryService cloudinaryService; // Gọi CloudinaryService thay vì Cloudinary trực tiếp

    // Lấy tất cả sản phẩm
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(productMapper::toDTO).collect(Collectors.toList());
    }

    // Lấy sản phẩm theo ID
    public Optional<ProductDTO> getByIdProducts(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(productMapper::toDTO);
    }

    // Thêm sản phẩm mới
    public ProductDTO createProduct(ProductDTO productDTO) {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        Brand brand = brandRepository.findById(productDTO.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
        Product product = productMapper.toEntity(productDTO);
        product.setCategory(category);
        product.setBrand(brand);
        product.setCreatedAt(LocalDate.now());
        product.setUpdatedAt(LocalDate.now());
        Product savedProduct = productRepository.save(product);
        return productMapper.toDTO(savedProduct);
    }

    // Cập nhật sản phẩm
    public ProductDTO updateProduct(Long id, ProductDTO updatedProductDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        existingProduct.setName(updatedProductDTO.getName());
        existingProduct.setDescription(updatedProductDTO.getDescription());
        existingProduct.setPrice(updatedProductDTO.getPrice());
        existingProduct.setSalePrice(updatedProductDTO.getSalePrice());
        existingProduct.setStatus(updatedProductDTO.isStatus());
        existingProduct.setThumbnail(updatedProductDTO.getThumbnail());
        existingProduct.setUpdatedAt(LocalDate.now());
        Product updatedProduct = productRepository.save(existingProduct);
        return productMapper.toDTO(updatedProduct);
    }

    // Xóa sản phẩm
    public boolean deleteProduct(Long id) {
        productRepository.deleteById(id);
        return true;
    }
}
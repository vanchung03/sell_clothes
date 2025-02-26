package com.example.demo.service;

import com.example.demo.dto.BrandDTO;
import com.example.demo.entity.Brand;
import com.example.demo.entity.ProductVariant;
import com.example.demo.mapper.BrandMapper;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private BrandMapper brandMapper;

    /**
     * ✅ Lấy brand từ variantId
     */
    public BrandDTO getBrandByVariantId(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể sản phẩm"));
        return brandMapper.toDTO(variant.getProduct().getBrand());
    }
    // Lấy danh sách Brand
    public List<BrandDTO> getAllBrands() {
        List<Brand> brands = brandRepository.findAll();
        return brandMapper.toDTOs(brands);
    }

    // Tạo mới Brand
    public BrandDTO createBrand(BrandDTO brandDTO) {
        Brand brand = brandMapper.toEntity(brandDTO);
        Brand savedBrand = brandRepository.save(brand);
        return brandMapper.toDTO(savedBrand);
    }

    // Cập nhật Brand
    public BrandDTO updateBrand(Long id, BrandDTO brandDTO) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Brand not found with id: " + id));
        brand.setName(brandDTO.getName());
        brand.setLogoUrl(brandDTO.getLogoUrl());
        brand.setDescription(brandDTO.getDescription());
        brand.setStatus(brandDTO.isStatus());
        Brand updatedBrand = brandRepository.save(brand);
        return brandMapper.toDTO(updatedBrand);
    }
    // Xóa Brand
    public void deleteBrand(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new RuntimeException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
    }
}

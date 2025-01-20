package com.example.demo.controller;

import com.example.demo.dto.BrandDTO;
import com.example.demo.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandController {

    @Autowired
    private BrandService brandService;

    // Lấy danh sách Brand
    @GetMapping
    public ResponseEntity<List<BrandDTO>> getAllBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }
    // Tạo mới Brand
    @PreAuthorize("hasRole('ROLE_DEALER') or hasRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        return ResponseEntity.ok(createdBrand);
    }

    // Cập nhật Brand
    @PutMapping("/{id}")
    public ResponseEntity<BrandDTO> updateBrand(@PathVariable Long id, @RequestBody BrandDTO brandDTO) {
        BrandDTO updatedBrand = brandService.updateBrand(id, brandDTO);
        return ResponseEntity.ok(updatedBrand);
    }

    // Xóa Brand
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok("Brand deletion was successful");
    }
}

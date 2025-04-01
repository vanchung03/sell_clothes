package com.example.demo.controller;

import com.example.demo.dto.ProductImageDTO;
import com.example.demo.entity.ProductImage;
import com.example.demo.service.ExcelTemplateService;
import com.example.demo.service.ProductImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/product_images")
public class ProductImageController {
    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ExcelTemplateService excelTemplateService;

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

    /**
     * ✅ API Import Ảnh Sản Phẩm từ Excel và thư mục ảnh
     */
    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importProductImagesFromExcel(
            @RequestPart("file") MultipartFile file,
            @RequestPart("images") List<MultipartFile> imageFiles) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductImageDTO> importedImages = productImageService.importProductImagesFromExcel(file, imageFiles);
            response.put("success", true);
            response.put("importedImages", importedImages);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi khi import ảnh sản phẩm: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    /**
     * ✅ API tải xuống file Excel mẫu
     */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadProductImageTemplate() {
        try {
            byte[] excelFile = excelTemplateService.generateProductImageTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=ProductImage_Template.xlsx");
            return ResponseEntity.ok().headers(headers).body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}


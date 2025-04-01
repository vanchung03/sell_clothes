package com.example.demo.controller;

import com.example.demo.dto.BrandDTO;
import com.example.demo.dto.ProductVariantDTO;
import com.example.demo.service.BrandService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ExcelTemplateService;
import com.example.demo.service.ProductVariantService;
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
@RequestMapping("/api/v1/product_variants")
public class ProductVariantController {

    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private ExcelTemplateService excelTemplateService;


    @Autowired
    private CategoryService categoryService;

    /**
     * 🏷 API: Lấy brand từ variantId
     */
    @GetMapping("/{variantId}/brand")
    public ResponseEntity<BrandDTO> getBrandByVariant(@PathVariable Long variantId) {
        BrandDTO brand = productVariantService.getBrandByVariantId(variantId);
        return ResponseEntity.ok(brand);
    }

    // Lấy tất cả biến thể sản phẩm theo productId
    @GetMapping("/{productId}")
    public List<ProductVariantDTO> getAllVariantsByProductId(@PathVariable Long productId) {
        return productVariantService.getAllVariantsByProductId(productId);
    }
    //  Lấy thông tin chi tiết của một biến thể sản phẩm theo variantId
    @GetMapping("/variant/{variantId}")
    public ResponseEntity<ProductVariantDTO> getVariantById(@PathVariable Long variantId) {
        return ResponseEntity.ok(productVariantService.getVariantById(variantId));
    }

//    // Thêm mới biến thể sản phẩm
//    @PostMapping
//    public ProductVariantDTO createProductVariant(@RequestBody ProductVariantDTO productVariantDTO) {
//        return productVariantService.createProductVariant(productVariantDTO);
//    }
    // Tạo biến thể sản phẩm mới
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

    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importProductVariantsFromExcel(
            @RequestPart("file") MultipartFile file,
            @RequestPart("images") List<MultipartFile> imageFiles) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductVariantDTO> importedVariants = productVariantService.importProductVariantsFromExcel(file, imageFiles);
            response.put("success", true);
            response.put("importedVariants", importedVariants);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi khi import biến thể sản phẩm: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadProductVariantTemplate() {
        try {
            byte[] excelFile = excelTemplateService.generateProductVariantTemplate();

            // ✅ Cấu hình header để tải file xuống
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=ProductVariant_Template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }


}

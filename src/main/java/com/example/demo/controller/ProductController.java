package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.service.BrandService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ExcelTemplateService;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ExcelTemplateService excelTemplateService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    // Lấy tất cả sản phẩm
    @GetMapping
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    // Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getByIdProduct(@PathVariable Long id) {
        Optional<ProductDTO> optionalProduct = productService.getByIdProducts(id);
        return optionalProduct.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    /**
     * ✅ Nhập sản phẩm từ Excel + Upload ảnh từ folder lên Cloudinary
     */
    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importProductsFromExcel(
            @RequestPart("file") MultipartFile file,
            @RequestPart("images") List<MultipartFile> imageFiles) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<ProductDTO> importedProducts = productService.importProductsFromExcel(file, imageFiles);

            response.put("success", true);
            response.put("importedProducts", importedProducts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Lỗi khi import sản phẩm: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    /**
     * ✅ API tải xuống file Excel mẫu
     */
    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadProductTemplate() {
        try {
            // ✅ Lấy danh sách Category ID và Brand ID từ database
            List<String> categoryIds = categoryService.getAllCategories()
                    .stream()
                    .map(category -> String.valueOf(category.getCategoryId()))
                    .toList();

            List<String> brandIds = brandService.getAllBrands()
                    .stream()
                    .map(brand -> String.valueOf(brand.getBrandId()))
                    .toList();

            // ✅ Tạo file Excel với danh sách ID của Category & Brand
            byte[] excelFile = excelTemplateService.generateProductTemplate(categoryIds, brandIds);

            // ✅ Cấu hình header để tải file xuống
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=Product_Template.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {

        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(createdProduct);
    }

    // Cập nhật sản phẩm (hỗ trợ upload ảnh mới)
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO){

        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        boolean isDeleted = productService.deleteProduct(id);

        Map<String, String> response = new HashMap<>();
        response.put("status", isDeleted ? "success" : "error");
        response.put("message", isDeleted ? "Product deletion was successful" : "Product deletion failed: Product not found");

        return isDeleted
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // API lấy danh sách sản phẩm theo categoryId
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(products);
    }
    // ✅ API lấy danh sách sản phẩm theo danh sách ID
    @PostMapping("/by-ids")
    public ResponseEntity<List<ProductDTO>> getProductsByIds(@RequestBody Map<String, List<Long>> requestBody) {
        List<Long> productIds = requestBody.get("productIds"); // Lấy danh sách ID từ JSON

        if (productIds == null || productIds.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<ProductDTO> products = productService.getProductsByIds(productIds);
        return ResponseEntity.ok(products);
    }
}
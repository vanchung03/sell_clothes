package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

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

    // Thêm sản phẩm mới
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(createdProduct); // Return ProductDTO when the product is successfully created
    }

    // Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(updatedProduct); // Return ProductDTO when the product is successfully updated
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();

        boolean isDeleted = productService.deleteProduct(id); // Giả sử phương thức này trả về boolean

        if (isDeleted) {
            response.put("status", "success");
            response.put("message", "Product deletion was successful");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Product deletion failed: Product not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

}

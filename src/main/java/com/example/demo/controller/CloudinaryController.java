package com.example.demo.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RestController
@RequestMapping("/api/cloudinary")
public class CloudinaryController {

    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping(value = "/upload-product", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadProduct(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("error", "File không được để trống!");
                return ResponseEntity.badRequest().body(response);
            }
            // ✅ Upload ảnh lên Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file, "Products", "product-image");

            response.put("success", true);
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Lỗi tải ảnh!");
            return ResponseEntity.status(500).body(response);
        }
    }
    @PostMapping(value = "/upload-logo-brand", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadLogoBrand(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("error", "File không được để trống!");
                return ResponseEntity.badRequest().body(response);
            }
            // ✅ Upload ảnh lên Cloudinary
            String imageUrl = cloudinaryService.uploadFile(file, "Brands", "logo-brand-image");

            response.put("success", true);
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Lỗi tải ảnh!");
            return ResponseEntity.status(500).body(response);
        }
    }
}

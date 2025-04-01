package com.example.demo.service;


import com.example.demo.dto.ProductImageDTO;
import com.example.demo.entity.ProductImage;

import com.example.demo.mapper.ProductImageMapper;
import com.example.demo.repository.ProductImageRepository;
import com.example.demo.repository.ProductRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductImageService {

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductImageMapper productImageMapper;

    @Autowired
    private CloudinaryService cloudinaryService;


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

    public List<ProductImageDTO> importProductImagesFromExcel(MultipartFile file, List<MultipartFile> imageFiles) {
        List<ProductImageDTO> importedImages = new ArrayList<>();
        Map<String, String> uploadedImages;

        try {
            uploadedImages = uploadImagesToCloudinary(imageFiles, "ProductImages");

            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                ProductImageDTO productImageDTO = new ProductImageDTO();
                Long productId = (long) row.getCell(0).getNumericCellValue();
                boolean isPrimary = row.getCell(1).getBooleanCellValue();
                int displayOrder = (int) row.getCell(2).getNumericCellValue();

                productImageDTO.setProductId(productId);
                productImageDTO.setPrimary(isPrimary);
                productImageDTO.setDisplayOrder(displayOrder);

                // ✅ So sánh ID và Display Order với tên ảnh
                String imageUrl = "/api/images/default.jpg"; // Mặc định nếu không tìm thấy ảnh
                for (String fileName : uploadedImages.keySet()) {
                    if (fileName.matches("product_" + productId + "_" + displayOrder + "(?:_[^_.]+)?\\.(jpg|jpeg|png|webp)")) {
                        imageUrl = uploadedImages.get(fileName);
                        break; // Tìm thấy ảnh phù hợp thì dừng vòng lặp
                    }

                }

                productImageDTO.setImageUrl(imageUrl);

                // ✅ Lưu vào database
                ProductImageDTO savedImage = createProductImage(productImageDTO);
                importedImages.add(savedImage);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }

        return importedImages;
    }

    /**
     * ✅ Upload nhiều ảnh lên Cloudinary và trả về Map<Tên file, URL>
     */
    private Map<String, String> uploadImagesToCloudinary(List<MultipartFile> imageFiles, String cloudinaryFolder) {
        Map<String, String> uploadedUrls = new HashMap<>();

        try {
            uploadedUrls = cloudinaryService.uploadMultipleFiles(imageFiles, cloudinaryFolder);
        } catch (IOException e) {
            System.err.println("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage());
        }

        return uploadedUrls;
    }

}

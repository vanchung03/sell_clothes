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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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
        // Lấy đối tượng Category từ database
        Category category = categoryRepository.findById(updatedProductDTO.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        existingProduct.setCategory(category);

        // Lấy đối tượng Brand từ database
        Brand brand = brandRepository.findById(updatedProductDTO.getBrandId())
                .orElseThrow(() -> new EntityNotFoundException("Brand not found"));
        existingProduct.setBrand(brand);
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

    // Lấy danh sách sản phẩm theo categoryId
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        List<Product> products = productRepository.findByCategoryId(categoryId);
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }
    public List<ProductDTO> getProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách productIds không được để trống!");
        }

        List<Product> products = productRepository.findAllById(productIds);
        if (products.isEmpty()) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm nào với danh sách ID đã cung cấp!");
        }

        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }


    /**
     * ✅ Nhập sản phẩm từ Excel và upload ảnh từ folder lên Cloudinary
     */
    public List<ProductDTO> importProductsFromExcel(MultipartFile file, List<MultipartFile> imageFiles) {
        List<ProductDTO> importedProducts = new ArrayList<>();
        Map<String, String> uploadedImages;

        try {
            // ✅ Upload ảnh lên Cloudinary trước khi xử lý Excel
            uploadedImages = uploadImagesToCloudinary(imageFiles, "Products");

            // ✅ In log kiểm tra ảnh đã upload
            System.out.println("🔍 DEBUG - Danh sách ảnh đã upload:");
            for (Map.Entry<String, String> entry : uploadedImages.entrySet()) {
                System.out.println("Tên file: " + entry.getKey() + " -> URL: " + entry.getValue());
            }

            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                ProductDTO productDTO = new ProductDTO();
                productDTO.setCategoryId((long) row.getCell(0).getNumericCellValue());
                productDTO.setBrandId((long) row.getCell(1).getNumericCellValue());
                productDTO.setName(row.getCell(2).getStringCellValue().trim());
                productDTO.setDescription(row.getCell(3).getStringCellValue());
                productDTO.setPrice(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
                productDTO.setSalePrice(BigDecimal.valueOf(row.getCell(5).getNumericCellValue()));
                productDTO.setStatus(row.getCell(6).getBooleanCellValue());

// ✅ Chuẩn hóa tên sản phẩm để khớp với tên file ảnh
                String normalizedProductName = productDTO.getName()
                        .toLowerCase()
                        .replace("á", "a").replace("à", "a").replace("ả", "a").replace("ã", "a").replace("ạ", "a")
                        .replace("ă", "a").replace("ắ", "a").replace("ằ", "a").replace("ẳ", "a").replace("ẵ", "a").replace("ặ", "a")
                        .replace("â", "a").replace("ấ", "a").replace("ầ", "a").replace("ẩ", "a").replace("ẫ", "a").replace("ậ", "a")
                        .replace("đ", "d")
                        .replace("é", "e").replace("è", "e").replace("ẻ", "e").replace("ẽ", "e").replace("ẹ", "e")
                        .replace("ê", "e").replace("ế", "e").replace("ề", "e").replace("ể", "e").replace("ễ", "e").replace("ệ", "e")
                        .replace("í", "i").replace("ì", "i").replace("ỉ", "i").replace("ĩ", "i").replace("ị", "i")
                        .replace("ó", "o").replace("ò", "o").replace("ỏ", "o").replace("õ", "o").replace("ọ", "o")
                        .replace("ô", "o").replace("ố", "o").replace("ồ", "o").replace("ổ", "o").replace("ỗ", "o").replace("ộ", "o")
                        .replace("ơ", "o").replace("ớ", "o").replace("ờ", "o").replace("ở", "o").replace("ỡ", "o").replace("ợ", "o")
                        .replace("ú", "u").replace("ù", "u").replace("ủ", "u").replace("ũ", "u").replace("ụ", "u")
                        .replace("ư", "u").replace("ứ", "u").replace("ừ", "u").replace("ử", "u").replace("ữ", "u").replace("ự", "u")
                        .replace("ý", "y").replace("ỳ", "y").replace("ỷ", "y").replace("ỹ", "y").replace("ỵ", "y")
                        .replaceAll("\\s+", "_"); // Thay dấu cách bằng "_"

// ✅ Log để debug
                System.out.println("🔍 DEBUG - Tìm ảnh cho sản phẩm: " + productDTO.getName());
                System.out.println("🔍 DEBUG - Tên sản phẩm đã chuẩn hóa: " + normalizedProductName);

// ✅ Tạo một Map để lưu trữ điểm số khớp cho từng ảnh
                Map<String, Integer> matchScores = new HashMap<>();

// ✅ Duyệt danh sách ảnh đã upload để tìm ảnh phù hợp
                for (Map.Entry<String, String> entry : uploadedImages.entrySet()) {
                    String uploadedFileName = entry.getKey().toLowerCase();
                    String uploadedFileNameWithoutExt = uploadedFileName.replaceAll("\\.[^.]+$", ""); // Bỏ phần mở rộng

                    // ✅ Log để debug
                    System.out.println("🔍 DEBUG - So sánh với file: " + uploadedFileName);

                    // ✅ Tính toán điểm số khớp
                    int score = 0;

                    // Khớp hoàn toàn - điểm cao nhất
                    if (uploadedFileNameWithoutExt.equals(normalizedProductName)) {
                        score = 1000;
                    }
                    // Tên file chứa tên sản phẩm hoặc ngược lại
                    else if (uploadedFileNameWithoutExt.contains(normalizedProductName) ||
                            normalizedProductName.contains(uploadedFileNameWithoutExt)) {
                        score = 500;
                    }
                    // Ưu tiên ảnh có cùng loại sản phẩm (ao_thun, ao_so_mi, etc.)
                    else {
                        String[] productParts = normalizedProductName.split("_");
                        String[] fileParts = uploadedFileNameWithoutExt.split("_");

                        if (productParts.length >= 2 && fileParts.length >= 2) {
                            // So sánh loại sản phẩm (ao_thun, ao_so_mi, etc.)
                            if ((productParts[0] + "_" + productParts[1]).equals(fileParts[0] + "_" + fileParts[1])) {
                                score = 300;
                            }
                        }

                        // Tìm các từ khớp chính xác
                        for (String productWord : productParts) {
                            if (productWord.length() <= 2) continue; // Bỏ qua từ quá ngắn

                            for (String fileWord : fileParts) {
                                if (fileWord.length() <= 2) continue; // Bỏ qua từ quá ngắn

                                if (productWord.equals(fileWord)) {
                                    score += 50; // Cộng điểm cho mỗi từ khớp
                                }
                            }
                        }
                    }

                    // Lưu điểm số khớp
                    if (score > 0) {
                        matchScores.put(entry.getKey(), score);
                    }
                }

// ✅ Tìm ảnh có điểm khớp cao nhất
                String imageUrl = "/api/images/default.jpg"; // Ảnh mặc định nếu không tìm thấy
                int highestScore = 0;

                for (Map.Entry<String, Integer> scoreEntry : matchScores.entrySet()) {
                    System.out.println("🔍 DEBUG - File: " + scoreEntry.getKey() + " - Điểm khớp: " + scoreEntry.getValue());

                    if (scoreEntry.getValue() > highestScore) {
                        highestScore = scoreEntry.getValue();
                        imageUrl = uploadedImages.get(scoreEntry.getKey());
                    }
                }

                if (highestScore > 0) {
                    System.out.println("✅ Tìm thấy ảnh phù hợp nhất (điểm: " + highestScore + "): " + imageUrl);
                } else {
                    System.out.println("❌ Không tìm thấy ảnh phù hợp, sử dụng ảnh mặc định");
                }

                productDTO.setThumbnail(imageUrl);

// ✅ Log kiểm tra ảnh được gán
                System.out.println("🖼️ Gán ảnh cho sản phẩm: " + productDTO.getName() + " -> " + productDTO.getThumbnail());


                // ✅ Lưu vào database
                ProductDTO savedProduct = createProduct(productDTO);
                importedProducts.add(savedProduct);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }

        return importedProducts;
    }


    /**
     * ✅ Upload nhiều ảnh lên Cloudinary và trả về Map<Tên file, URL>
     */
    private Map<String, String> uploadImagesToCloudinary(List<MultipartFile> imageFiles, String cloudinaryFolder) {
        Map<String, String> uploadedUrls = new HashMap<>();

        try {
            uploadedUrls = cloudinaryService.uploadMultipleFiles(imageFiles, cloudinaryFolder);
        } catch (IOException e) {
            System.err.println(" Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage());
        }
        return uploadedUrls;
    }
}
package com.example.demo.service;

import com.example.demo.dto.BrandDTO;
import com.example.demo.dto.ProductVariantDTO;
import com.example.demo.entity.Brand;
import com.example.demo.entity.ProductVariant;
import com.example.demo.mapper.BrandMapper;
import com.example.demo.mapper.ProductVariantMapper;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.ProductVariantRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class ProductVariantService {

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private ProductVariantMapper productVariantMapper;

    @Autowired
    private CloudinaryService cloudinaryService;

    public BrandDTO getBrandByVariantId(Long variantId) {
        Brand brand = productVariantRepository.findBrandByVariantId(variantId);
        if (brand == null) {
            throw new RuntimeException("Không tìm thấy thương hiệu cho biến thể sản phẩm!");
        }
        return brandMapper.toDTO(brand);
    }

    // Lấy tất cả các biến thể của sản phẩm theo productId
    public List<ProductVariantDTO> getAllVariantsByProductId(Long productId) {
        return productVariantRepository.findByProduct_ProductId(productId)
                .stream()
                .map(productVariantMapper::toDTO)
                .toList();
    }
    // ✅ Lấy thông tin chi tiết của một biến thể sản phẩm theo variantId
    public ProductVariantDTO getVariantById(Long variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));
        return productVariantMapper.toDTO(variant);
    }

    // Thêm mới biến thể sản phẩm
//    public ProductVariantDTO createProductVariant(ProductVariantDTO productVariantDTO) {
//        ProductVariant productVariant = productVariantMapper.toEntity(productVariantDTO);
//        productVariant.setProduct(productRepository.findById(productVariantDTO.getProductId())
//                .orElseThrow(() -> new RuntimeException("Product not found")));
//        ProductVariant savedVariant = productVariantRepository.save(productVariant);
//        return productVariantMapper.toDTO(savedVariant);
//    }

    // Cập nhật thông tin biến thể sản phẩm
    public ProductVariantDTO updateProductVariant(Long variantId, ProductVariantDTO productVariantDTO) {
        ProductVariant existingVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found"));

        existingVariant.setSize(productVariantDTO.getSize());
        existingVariant.setColor(productVariantDTO.getColor());
        existingVariant.setSku(productVariantDTO.getSku());
        existingVariant.setPrice(productVariantDTO.getPrice());
        existingVariant.setStockQuantity(productVariantDTO.getStockQuantity());
        existingVariant.setImageUrl(productVariantDTO.getImageUrl());
        existingVariant.setStatus(productVariantDTO.isStatus());

        ProductVariant updatedVariant = productVariantRepository.save(existingVariant);
        return productVariantMapper.toDTO(updatedVariant);
    }

    // Xóa biến thể sản phẩm
    public void deleteProductVariant(Long variantId) {
        productVariantRepository.deleteById(variantId);
    }


    public List<ProductVariantDTO> importProductVariantsFromExcel(MultipartFile file, List<MultipartFile> imageFiles) {
        List<ProductVariantDTO> importedVariants = new ArrayList<>();
        Map<String, String> uploadedImages = new HashMap<>();

        try {
            // Upload ảnh lên Cloudinary nếu có
            uploadedImages = cloudinaryService.uploadMultipleFiles(imageFiles, "ProductVariants");

            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            // Giả sử dòng 0 là tiêu đề
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua tiêu đề

                // Đọc dữ liệu theo thứ tự:
                // 0: Product ID, 1: Size, 2: Color, 3: SKU (công thức tự sinh ở Excel, có thể rỗng),
                // 4: Price, 5: Stock Quantity, 6: Status (TRUE/FALSE)
                Long productId       = getLongValue(row.getCell(0));
                String size          = getStringValue(row.getCell(1));
                String color         = getStringValue(row.getCell(2));
                String sku           = getStringValue(row.getCell(3));  // Có thể là công thức đã tính
                double price         = getDoubleValue(row.getCell(4));
                int stockQuantity    = getIntValue(row.getCell(5));
                boolean status       = getBooleanValue(row.getCell(6));

                // Nếu dòng trống (chưa nhập dữ liệu quan trọng) thì bỏ qua
                if (productId == 0 && size.isEmpty() && color.isEmpty()) {
                    continue;
                }
                // Check xem có trùng không (bỏ qua SKU)
                Optional<ProductVariant> existing = productVariantRepository.findDuplicateVariant(
                        productId, size, color, price, stockQuantity, status
                );
                if (existing.isPresent()) {
                    // => đã có 1 variant giống hệt (khác SKU cũng bỏ qua)
                    System.out.println("Bỏ qua do trùng dữ liệu: productId=" + productId + ", size=" + size + ", color=" + color);
                    continue;
                }

                // Nếu SKU vẫn rỗng, tự sinh SKU fallback
                if (sku.isEmpty()) {
                    sku = generateSKU(productId, size, color);
                }

                // Tìm ảnh tương ứng theo productId và color
                String imageUrl = findImageUrl(uploadedImages, productId, color);

                // Tạo DTO
                ProductVariantDTO variantDTO = new ProductVariantDTO();
                variantDTO.setProductId(productId);
                variantDTO.setSize(size);
                variantDTO.setColor(color);
                variantDTO.setSku(sku);
                variantDTO.setPrice(price);
                variantDTO.setStockQuantity(stockQuantity);
                variantDTO.setStatus(status);
                variantDTO.setImageUrl(imageUrl);

                // (Nếu cần) Kiểm tra xem dữ liệu trùng hay không, bỏ qua nếu trùng
                // Ví dụ: if(variantAlreadyExists(...)) { continue; }

                // Lưu vào DB
                ProductVariantDTO saved = createProductVariant(variantDTO);
                importedVariants.add(saved);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }

        return importedVariants;
    }

    /**
     * Tìm URL ảnh dựa trên productId và color
     */
    private String findImageUrl(Map<String, String> uploadedImages, Long productId, String color) {
        String imageUrl = "/api/images/default.jpg"; // Giá trị fallback
        String normColor = normalizeColor(color);
        for (String fileName : uploadedImages.keySet()) {
            String pattern = "product_" + productId + "_\\d*_" + normColor + "\\.(jpg|jpeg|png|webp)";
            if (fileName.matches(pattern)) {
                imageUrl = uploadedImages.get(fileName);
                break;
            }
        }
        return imageUrl;
    }

    /**
     * Sinh SKU dựa trên productId, size, color và số ngẫu nhiên 5 chữ số
     */
    private String generateSKU(Long productId, String size, String color) {
        int random5 = 10000 + new Random().nextInt(90000);
        return "SKU_" + productId + "_" + size + "_" + color + "_" + random5;
    }

    /**
     * Tạo biến thể sản phẩm mới
     */
    public ProductVariantDTO createProductVariant(ProductVariantDTO dto) {
        ProductVariant entity = productVariantMapper.toEntity(dto);
        entity.setProduct(
                productRepository.findById(dto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"))
        );
        ProductVariant saved = productVariantRepository.save(entity);
        return productVariantMapper.toDTO(saved);
    }

    // ===================== Các hàm đọc giá trị từ Excel =====================

    private Long getLongValue(Cell cell) {
        if (cell == null) return 0L;
        if (cell.getCellType() == CellType.FORMULA) {
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            CellValue value = evaluator.evaluate(cell);
            if (value.getCellType() == CellType.NUMERIC) {
                return (long) value.getNumberValue();
            } else {
                return 0L;
            }
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> (long) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Long.parseLong(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield 0L;
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue() ? 1L : 0L;
            default -> 0L;
        };
    }

    private double getDoubleValue(Cell cell) {
        if (cell == null) return 0.0;
        if (cell.getCellType() == CellType.FORMULA) {
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            CellValue value = evaluator.evaluate(cell);
            if (value.getCellType() == CellType.NUMERIC) {
                return value.getNumberValue();
            } else {
                return 0.0;
            }
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Double.parseDouble(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield 0.0;
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue() ? 1.0 : 0.0;
            default -> 0.0;
        };
    }

    private int getIntValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.FORMULA) {
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            CellValue value = evaluator.evaluate(cell);
            if (value.getCellType() == CellType.NUMERIC) {
                return (int) value.getNumberValue();
            } else {
                return 0;
            }
        }
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                try {
                    yield Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    yield 0;
                }
            }
            case BOOLEAN -> cell.getBooleanCellValue() ? 1 : 0;
            default -> 0;
        };
    }

    private String getStringValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.FORMULA) {
            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
            CellValue value = evaluator.evaluate(cell);
            if (value.getCellType() == CellType.STRING) {
                return value.getStringValue().trim();
            } else if (value.getCellType() == CellType.NUMERIC) {
                return String.valueOf((long) value.getNumberValue());
            } else {
                return "";
            }
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    /**
     * Sử dụng DataFormatter để đảm bảo luôn lấy được giá trị chuỗi từ cell,
     * sau đó chuyển sang boolean.
     */
    private boolean getBooleanValue(Cell cell) {
        if (cell == null) return false;
        DataFormatter formatter = new DataFormatter();
        String text = formatter.formatCellValue(cell).trim();
        return Boolean.parseBoolean(text);
    }

    /**
     * Hàm chuẩn hóa chuỗi màu: chuyển về chữ thường, loại bỏ dấu và thay khoảng trắng thành dấu gạch dưới.
     */
    private String normalizeColor(String color) {
        return color.toLowerCase()
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
                .replaceAll("\\s+", "_");
    }

}

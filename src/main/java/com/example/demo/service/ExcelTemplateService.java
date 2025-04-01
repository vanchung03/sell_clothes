package com.example.demo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelTemplateService {

    private static final String[] SIZES = {"XS", "S", "M", "L", "XL", "XXL", "3XL", "4XL", "5XL"};
    private static final String[] COLORS = {
            "Đỏ", "Đen", "Trắng", "Be", "Hồng", "Xám", "Nâu", "Tím", "Vàng", "Cam", "Bạc", "Xanh"
    };

    /**
     * ✅ Tạo file Excel mẫu cho biến thể sản phẩm:
     * - Cột SKU có công thức IF(...) => chỉ hiển thị khi Product ID, Size, Color đã có.
     * - Nếu chưa đủ dữ liệu, ô SKU trống.
     */
    public byte[] generateProductVariantTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ProductVariants");

        // Tạo dòng tiêu đề
        createHeaderRow(sheet, new String[]{
                "Product ID", "Size", "Color", "SKU (Auto)", "Price", "Stock Quantity", "Status"
        });

        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        for (int i = 1; i <= 100; i++) { // Giới hạn 100 dòng
            Row row = sheet.createRow(i);

            row.createCell(0).setCellValue(""); // Product ID
            row.createCell(1).setCellValue(""); // Size
            row.createCell(2).setCellValue(""); // Color

            // Công thức tự động tạo SKU
            // Chỉ khi A, B, C đã có (Product ID, Size, Color)
            Cell skuCell = row.createCell(3);
            int rowNumber = i + 1; // Excel tính từ 1
            String formula =
                    "IF(" +
                            "AND(ISNUMBER(A" + rowNumber + "), LEN(B" + rowNumber + ")>0, LEN(C" + rowNumber + ")>0)," +
                            "CONCATENATE(\"SKU_\", A" + rowNumber + ", \"_\", B" + rowNumber + ", \"_\", C" + rowNumber + ", \"_\", RANDBETWEEN(10000,99999))," +
                            "\"\"" + // Nếu chưa đủ dữ liệu => rỗng
                            ")";
            skuCell.setCellFormula(formula);

            row.createCell(4).setCellValue(""); // Price
            row.createCell(5).setCellValue(""); // Stock Quantity
            row.createCell(6).setCellValue(""); // Status

            // Áp dụng dropdown
            applyDropdownList(sheet, validationHelper, SIZES, i, 1);  // Size
            applyDropdownList(sheet, validationHelper, COLORS, i, 2); // Color
            applyDropdownList(sheet, validationHelper, new String[]{"TRUE", "FALSE"}, i, 6); // Status
        }

        return convertWorkbookToByteArray(workbook);
    }


    /**
     * ✅ Tạo danh sách chọn (Dropdown List) cho Excel
     */
    private void applyDropdownList(Sheet sheet, DataValidationHelper validationHelper, String[] options, int rowIndex, int columnIndex) {
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(options);
        CellRangeAddressList addressList = new CellRangeAddressList(rowIndex, rowIndex, columnIndex, columnIndex);
        DataValidation validation = validationHelper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    /**
     * ✅ Tạo file Excel mẫu cho sản phẩm (Products) (KHÔNG có dữ liệu mẫu)
     */
    public byte[] generateProductTemplate(List<String> categoryIds, List<String> brandIds) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        createHeaderRow(sheet, new String[]{"Category ID", "Brand ID", "Name", "Description", "Price", "Sale Price", "Status"});

        DataValidationHelper validationHelper = sheet.getDataValidationHelper();

        for (int i = 1; i <= 100; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(""); // Category ID (Chọn từ danh sách ID)
            row.createCell(1).setCellValue(""); // Brand ID (Chọn từ danh sách ID)
            row.createCell(2).setCellValue(""); // Name
            row.createCell(3).setCellValue(""); // Description
            row.createCell(4).setCellValue(""); // Price
            row.createCell(5).setCellValue(""); // Sale Price
            row.createCell(6).setCellValue(""); // Status (TRUE/FALSE)

            // ✅ Áp dụng danh sách chọn cho Category ID
            applyDropdownList(sheet, validationHelper, categoryIds.toArray(new String[0]), i, 0);

            // ✅ Áp dụng danh sách chọn cho Brand ID
            applyDropdownList(sheet, validationHelper, brandIds.toArray(new String[0]), i, 1);

            // ✅ Áp dụng danh sách TRUE/FALSE cho Status
            applyDropdownList(sheet, validationHelper, new String[]{"TRUE", "FALSE"}, i, 6);
        }

        return convertWorkbookToByteArray(workbook);
    }


    /**
     * ✅ Tạo file Excel mẫu cho nhập ảnh sản phẩm (KHÔNG có dữ liệu mẫu)
     */
    public byte[] generateProductImageTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ProductImages");

        Row headerRow = sheet.createRow(0);
        String[] columns = {"Product ID", "Is Primary (true/false)", "Display Order"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        for (int i = 1; i <= 100; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(""); // Product ID
            row.createCell(1).setCellValue(""); // Is Primary (TRUE/FALSE)
            row.createCell(2).setCellValue(""); // Display Order

            // ✅ Áp dụng danh sách TRUE/FALSE cho Is Primary
            DataValidationHelper validationHelper = sheet.getDataValidationHelper();
            applyDropdownList(sheet, validationHelper, new String[]{"TRUE", "FALSE"}, i, 1);
        }

        return convertWorkbookToByteArray(workbook);
    }

    /**
     * ✅ Hàm tạo dòng tiêu đề
     */
    private void createHeaderRow(Sheet sheet, String[] columns) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * ✅ Chuyển Workbook thành mảng byte
     */
    private byte[] convertWorkbookToByteArray(Workbook workbook) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return outputStream.toByteArray();
    }
}

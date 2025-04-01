package com.example.demo.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Tải lên file lên Cloudinary và trả về URL.
     */
    public String uploadFile(MultipartFile file, String folder, String prefix) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Kích thước file vượt quá giới hạn 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown_file";
        }

        // Đảm bảo tên file an toàn cho URL (thay thế khoảng trắng và ký tự đặc biệt)
        String sanitizedFilename = originalFilename.replaceAll("\\s+", "_");

        String fileNameWithoutExtension;
        int lastDotIndex = sanitizedFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileNameWithoutExtension = sanitizedFilename.substring(0, lastDotIndex);
        } else {
            fileNameWithoutExtension = sanitizedFilename;
        }

        String uniqueFileName = folder + "/" + prefix + System.currentTimeMillis() + "_" + fileNameWithoutExtension;

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", uniqueFileName,
                            "resource_type", "auto",
                            "use_filename", true,
                            "unique_filename", true
                    ));

            String url = (String) uploadResult.get("secure_url"); // Sử dụng URL bảo mật (HTTPS)

            // Xóa các tham số query khỏi URL
            return cleanUrl(url);
        } catch (Exception e) {
            throw new IOException("Lỗi khi tải file lên Cloudinary", e);
        }
    }

    /**
     * Xóa file khỏi Cloudinary bằng `public_id`.
     */
    public void deleteFile(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new IOException("Lỗi khi xóa file khỏi Cloudinary", e);
        }
    }

    /**
     * Trích xuất `public_id` từ URL của Cloudinary.
     */
    public String extractPublicId(String url) {
        try {
            int uploadIndex = url.indexOf("/upload/");
            if (uploadIndex != -1) {
                String afterUpload = url.substring(uploadIndex + 8); // Bỏ qua "/upload/"
                int slashIndex = afterUpload.indexOf('/');
                if (slashIndex != -1) {
                    String publicId = afterUpload.substring(slashIndex + 1);
                    publicId = publicId.replaceAll("\\?.*$", ""); // Xóa query params
                    int dotIndex = publicId.lastIndexOf('.');
                    return (dotIndex != -1) ? publicId.substring(0, dotIndex) : publicId;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Định dạng URL Cloudinary không hợp lệ", e);
        }
        throw new IllegalArgumentException("Định dạng URL Cloudinary không hợp lệ");
    }

    /**
     * Xóa bất kỳ tham số query nào khỏi URL.
     */
    private String cleanUrl(String url) {
        int questionMarkIndex = url.indexOf('?');
        return (questionMarkIndex != -1) ? url.substring(0, questionMarkIndex) : url;
    }

    public Map<String, String> uploadMultipleFiles(List<MultipartFile> files, String cloudinaryFolder) throws IOException {
        Map<String, String> uploadedUrls = new HashMap<>();

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Danh sách file trống!");
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                System.out.println("⚠️ Bỏ qua file trống!");
                continue;
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) continue;

            // ✅ Loại bỏ thư mục, chỉ lấy tên file
            String cleanFileName = new File(originalFilename).getName();

            // ✅ Chuẩn hóa tên file: chữ thường, thay khoảng trắng thành "_"
            String sanitizedFileName = cleanFileName.toLowerCase().replaceAll("\\s+", "_");

            try {
                Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                        "public_id", cloudinaryFolder + "/" + sanitizedFileName.replaceAll("\\.[^.]+$", ""), // Bỏ phần mở rộng để tránh `.webp.webp`
                        "resource_type", "image"
                ));

                String imageUrl = (String) uploadResult.get("secure_url");
                uploadedUrls.put(sanitizedFileName, imageUrl); // Lưu vào Map đúng chuẩn

                System.out.println("Uploaded: " + sanitizedFileName + " -> " + imageUrl);
            } catch (Exception e) {
                System.err.println("Lỗi khi upload file: " + sanitizedFileName);
                e.printStackTrace();
            }
        }
        return uploadedUrls;
    }

}

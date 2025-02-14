package com.example.demo.service;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file, String folder, String prefix) throws IOException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 5MB limit");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown_file";
        }

        String fileNameWithoutExtension;
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            fileNameWithoutExtension = originalFilename.substring(0, lastDotIndex);
        } else {
            fileNameWithoutExtension = originalFilename;
        }

        String uniqueFileName = folder + "/" + prefix + System.currentTimeMillis() + "_" + fileNameWithoutExtension;

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", uniqueFileName,
                        "resource_type", "auto",
                        "use_filename", true,
                        "unique_filename", true
                ));

        String url = (String) uploadResult.get("url");

        // Remove any query parameters from the URL
        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex != -1) {
            url = url.substring(0, questionMarkIndex);
        }

        return url;
    }

    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }


    public String extractPublicId(String url) {
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex != -1) {
            String afterUpload = url.substring(uploadIndex + 8); // +8 to skip "/upload/"
            int slashIndex = afterUpload.indexOf('/');
            if (slashIndex != -1) {
                // Start from the first '/' after "/upload/"
                String publicId = afterUpload.substring(slashIndex + 1);
                // Remove file extension if present
                int dotIndex = publicId.lastIndexOf('.');
                if (dotIndex != -1) {
                    publicId = publicId.substring(0, dotIndex);
                }
                return publicId;
            }
        }
        throw new IllegalArgumentException("Invalid URL format");
    }
}
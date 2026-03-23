package com.example.taskmanager.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Async
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi upload file lên Cloudinary: " + e.getMessage());
        }
    }

    public void deleteFile(String url) {
        try {
            String publicId = extractPublicId(url);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi xóa file trên Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        int lastSlashIndex = url.lastIndexOf("/");
        int lastDotIndex = url.lastIndexOf(".");

        if (lastSlashIndex != -1 && lastDotIndex != -1 && lastSlashIndex < lastDotIndex) {
            return url.substring(lastSlashIndex + 1, lastDotIndex);
        }
        return null;
    }
}
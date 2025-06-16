package com.google.rentit.property.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    
    @Autowired
    private Cloudinary cloudinary;
    
    public Map<String, Object> uploadFile(MultipartFile file, String folder) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), 
            ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "quality", "auto",
                "fetch_format", "auto"
            )
        );
    }
    
    public Map<String, Object> uploadWithThumbnail(MultipartFile file, String folder) throws IOException {
        // Upload original image
        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), 
            ObjectUtils.asMap(
                "folder", folder,
                "resource_type", "image",
                "quality", "auto",
                "fetch_format", "auto"
            )
        );
        
        // Generate thumbnail URL by modifying the original URL
        String originalUrl = (String) uploadResult.get("secure_url");
        String thumbnailUrl = generateThumbnailUrl(originalUrl);
        
        uploadResult.put("thumbnail_url", thumbnailUrl);
        return uploadResult;
    }
    
    private String generateThumbnailUrl(String originalUrl) {
        // Insert transformation parameters into the URL
        // Example: https://res.cloudinary.com/demo/image/upload/sample.jpg
        // Becomes: https://res.cloudinary.com/demo/image/upload/w_300,h_200,c_fill/sample.jpg
        
        if (originalUrl != null && originalUrl.contains("/upload/")) {
            return originalUrl.replace("/upload/", "/upload/w_300,h_200,c_fill,q_auto,f_auto/");
        }
        return originalUrl; // Return original if transformation fails
    }
    
    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
    
    // Method to generate custom sized images
    public String generateImageUrl(String originalUrl, int width, int height) {
        if (originalUrl != null && originalUrl.contains("/upload/")) {
            String transformation = String.format("w_%d,h_%d,c_fill,q_auto,f_auto", width, height);
            return originalUrl.replace("/upload/", "/upload/" + transformation + "/");
        }
        return originalUrl;
    }
    
    // Method to generate different crop types
    public String generateImageUrl(String originalUrl, int width, int height, String crop) {
        if (originalUrl != null && originalUrl.contains("/upload/")) {
            String transformation = String.format("w_%d,h_%d,c_%s,q_auto,f_auto", width, height, crop);
            return originalUrl.replace("/upload/", "/upload/" + transformation + "/");
        }
        return originalUrl;
    }
    public void deletePhoto(String publicId) throws IOException {
    try {
        Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        System.out.println("Cloudinary delete result: " + result);
    } catch (IOException e) {
        System.err.println("Failed to delete from Cloudinary: " + e.getMessage());
        throw e;
    }
}
}


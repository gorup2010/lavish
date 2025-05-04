package com.nashrookie.lavish.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.nashrookie.lavish.exception.ImageDeleteException;
import com.nashrookie.lavish.exception.ImageUploadException;

import lombok.extern.slf4j.Slf4j;

// Reference: https://cloudinary.com/documentation/java_quickstart
@Service
@Slf4j
public class CloudinaryService {
    private final Cloudinary cloudinary;
    // https://cloudinary.com/documentation/image_upload_api_reference#upload_optional_parameters
    private final Map<?, ?> options = Map.of("use_filename", true,
            "folder", "lavish",
            "resource_type", "auto");

    public CloudinaryService(@Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    public Map<String, String> uploadFile(MultipartFile file) {
        try {
            // Refer https://cloudinary.com/documentation/java_image_and_video_upload#upload_response
            // to see the structure of the response.
            // Remember to store public_id or asset_id in the response to delete later.
            return cloudinary.uploader().upload(file.getBytes(), options);
        } catch (IOException e) {
            log.error("Error when uploading image file");
            throw new ImageUploadException(e.getMessage());
        }
    }

    public Map<String, String> deleteFile(String publicId) {
        try {
            return cloudinary.uploader().destroy(publicId, Map.of());
        } catch (IOException e) {
            log.error("Error when deleting image file {}", publicId);
            throw new ImageDeleteException(e.getMessage());
        }
    }
}

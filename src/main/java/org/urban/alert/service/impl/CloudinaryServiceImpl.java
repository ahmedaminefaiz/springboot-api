package org.urban.alert.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.urban.alert.service.CloudinaryService;

@Service
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @Override
    public void deleteResource(String url, String resourceType) {
        try {
            String publicId = extractPublicId(url);
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", resourceType));
            log.info("Cloudinary {} supprimé : {}", resourceType, publicId);
        } catch (Exception e) {
            log.error("Échec suppression Cloudinary ({}) pour l'URL {} : {}", resourceType, url, e.getMessage());
        }
    }

    private String extractPublicId(String url) {
        String[] parts = url.split("/upload/");
        if (parts.length < 2) {
            throw new IllegalArgumentException("URL Cloudinary invalide : " + url);
        }
        String afterUpload = parts[1];
        if (afterUpload.matches("v\\d+/.*")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf('/') + 1);
        }
        int lastDot = afterUpload.lastIndexOf('.');
        if (lastDot != -1) {
            afterUpload = afterUpload.substring(0, lastDot);
        }
        return afterUpload;
    }
}
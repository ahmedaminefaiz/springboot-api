package org.urban.alert.service;

public interface CloudinaryService {

    /**
     * Supprime un asset depuis Cloudinary.
     * @param url          URL complète Cloudinary de l'asset
     * @param resourceType "image" ou "video"
     */
    void deleteResource(String url, String resourceType);
}
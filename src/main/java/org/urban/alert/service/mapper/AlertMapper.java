package org.urban.alert.service.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.urban.alert.dto.alert.CreateAlertRequestDTO;
import org.urban.alert.dto.alert.AlertResponseDTO;
import org.urban.alert.dto.alert.UpdateAlertRequestDTO;
import org.urban.alert.entity.Alert;

import java.util.ArrayList;
import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {UserMapper.class, ProblemTypeMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface AlertMapper {

    // Variable implicitement 'public static final' accessible par les méthodes default
    ObjectMapper objectMapper = new ObjectMapper();

    // ========== Create DTO to Entity ==========

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "priority", defaultValue = "MEDIUM")
    @Mapping(target = "isAnonymous", defaultValue = "false")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "commentaires", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Alert createAlertRequestToEntity(CreateAlertRequestDTO request);

    // ========== Entity to Response DTO ==========

    @Mapping(source = "category", target = "category")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(target = "images", expression = "java(parseJsonArray(alert.getImages()))")
    @Mapping(target = "videos", expression = "java(parseJsonArray(alert.getVideos()))")
    AlertResponseDTO entityToAlertResponse(Alert alert);

    // ========== Update DTO to Entity ==========

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ticket", ignore = true)
    @Mapping(target = "commentaires", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateAlertFromRequest(
            UpdateAlertRequestDTO request,
            @MappingTarget Alert alert);

    // ========== Helper Methods (default) ==========

    /**
     * Convertit une chaîne JSON en liste de chaînes
     */
    default List<String> parseJsonArray(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Convertit une liste de chaînes en JSON
     */
    default String toJsonArray(List<String> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * Ajoute une image à la liste JSON
     */
    default String addImageToJsonArray(String currentImagesJson, String newImageUrl) {
        List<String> images = parseJsonArray(currentImagesJson);
        images.add(newImageUrl);
        return toJsonArray(images);
    }

    /**
     * Ajoute une vidéo à la liste JSON
     */
    default String addVideoToJsonArray(String currentVideosJson, String newVideoUrl) {
        List<String> videos = parseJsonArray(currentVideosJson);
        videos.add(newVideoUrl);
        return toJsonArray(videos);
    }

    /**
     * Supprime une image de la liste JSON
     */
    default String removeImageFromJsonArray(String currentImagesJson, String imageUrlToRemove) {
        List<String> images = parseJsonArray(currentImagesJson);
        images.remove(imageUrlToRemove);
        return images.isEmpty() ? null : toJsonArray(images);
    }

    /**
     * Supprime une vidéo de la liste JSON
     */
    default String removeVideoFromJsonArray(String currentVideosJson, String videoUrlToRemove) {
        List<String> videos = parseJsonArray(currentVideosJson);
        videos.remove(videoUrlToRemove);
        return videos.isEmpty() ? null : toJsonArray(videos);
    }
}

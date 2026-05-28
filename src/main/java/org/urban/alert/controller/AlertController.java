package org.urban.alert.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.urban.alert.dto.alert.AddMediaRequestDTO;
import org.urban.alert.dto.alert.CreateAlertRequestDTO;
import org.urban.alert.dto.alert.AlertResponseDTO;
import org.urban.alert.dto.alert.UpdateAlertRequestDTO;
import org.urban.alert.entity.enums.AlertStatusEnum;
import org.urban.alert.exception.alert.InvalidAlertException;
import org.urban.alert.exception.alert.AlertNotFoundException;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.service.AlertService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;

    // ========== CRUD Endpoints ==========

    /**
     * Crée une nouvelle alerte
     */
    @Operation(summary = "Create a new alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alert created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAlert(@Valid @RequestBody CreateAlertRequestDTO request) {
        try {
            log.info("POST /api/alerts - Creating new alert");
            Long userId = getCurrentUserId();
            AlertResponseDTO response = alertService.createAlert(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating alert", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Erreur lors de la création de l'alerte", e.getMessage()));
        }
    }

    /**
     * Récupère une alerte par son ID
     */
    @Operation(summary = "Get an alert by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAlertById(@PathVariable Long id) {
        try {
            log.info("GET /api/alerts/{} - Fetching alert", id);
            AlertResponseDTO response = alertService.getAlertById(id);
            return ResponseEntity.ok(response);
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        }
    }

    /**
     * Récupère toutes les alertes avec pagination
     */
    @Operation(summary = "Get all alerts with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of alerts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /api/alerts - Fetching all alerts");
            Pageable pageable = PageRequest.of(page, size);
            Page<AlertResponseDTO> response = alertService.getAllAlerts(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching alerts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la récupération des alertes", e.getMessage()));
        }
    }

    /**
     * Récupère les alertes de l'utilisateur actuel
     */
    @Operation(summary = "Get alerts of the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user alerts retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/user/my-alerts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /api/alerts/user/my-alerts - Fetching user alerts");
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);
            Page<AlertResponseDTO> response = alertService.getUserAlerts(userId, pageable);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        }
    }

    /**
     * Met à jour une alerte
     */
    @Operation(summary = "Update an existing alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request"),
            @ApiResponse(responseCode = "404", description = "Alert not found"),
            @ApiResponse(responseCode = "409", description = "Alert status is not NEW (modification not allowed)")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateAlert(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAlertRequestDTO request) {
        try {
            log.info("PUT /api/alerts/{} - Updating alert", id);
            Long userId = getCurrentUserId();
            AlertResponseDTO response = alertService.updateAlert(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        } catch (InvalidAlertException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("L'alerte ne peut pas être modifiée", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating alert", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Erreur lors de la mise à jour de l'alerte", e.getMessage()));
        }
    }

    /**
     * Supprime une alerte
     */
    @Operation(summary = "Delete an alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Alert deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Alert not found"),
            @ApiResponse(responseCode = "409", description = "Alert status is not NEW (deletion not allowed)")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable Long id) {
        try {
            log.info("DELETE /api/alerts/{} - Deleting alert", id);
            Long userId = getCurrentUserId();
            alertService.deleteAlert(id, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        } catch (InvalidAlertException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("L'alerte ne peut pas être supprimée", e.getMessage()));
        }
    }

    // ========== Media Endpoints ==========

    /**
     * Ajoute une image à l'alerte
     */
    @Operation(summary = "Add an image to an alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Image added successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    @PostMapping(value = "/{id}/images", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addImage(
            @PathVariable Long id,
            @Valid @RequestBody AddMediaRequestDTO request) {
        try {
            log.info("POST /api/alerts/{}/images - Adding image", id);
            AlertResponseDTO response = alertService.addImage(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding image", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Erreur lors de l'ajout de l'image", e.getMessage()));
        }
    }

    /**
     * Ajoute une vidéo à l'alerte
     */
    @Operation(summary = "Add a video to an alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Video added successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or bad request"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    @PostMapping(value = "/{id}/videos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVideo(
            @PathVariable Long id,
            @Valid @RequestBody AddMediaRequestDTO request) {
        try {
            log.info("POST /api/alerts/{}/videos - Adding video", id);
            AlertResponseDTO response = alertService.addVideo(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding video", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Erreur lors de l'ajout de la vidéo", e.getMessage()));
        }
    }

    /**
     * Supprime une image de l'alerte
     */
    @Operation(summary = "Remove an image from an alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image removed successfully"),
            @ApiResponse(responseCode = "400", description = "Image not found in alert or bad request"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    @DeleteMapping(value = "/{id}/images", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeImage(
            @PathVariable Long id,
            @RequestParam String imageUrl) {
        try {
            log.info("DELETE /api/alerts/{}/images - Removing image", id);
            AlertResponseDTO response = alertService.removeImage(id, imageUrl);
            return ResponseEntity.ok(response);
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        } catch (InvalidAlertException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Erreur lors de la suppression de l'image", e.getMessage()));
        }
    }

    /**
     * Supprime une vidéo de l'alerte
     */
    @Operation(summary = "Remove a video from an alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Video removed successfully"),
            @ApiResponse(responseCode = "400", description = "Video not found in alert or bad request"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    @DeleteMapping(value = "/{id}/videos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeVideo(
            @PathVariable Long id,
            @RequestParam String videoUrl) {
        try {
            log.info("DELETE /api/alerts/{}/videos - Removing video", id);
            AlertResponseDTO response = alertService.removeVideo(id, videoUrl);
            return ResponseEntity.ok(response);
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        } catch (InvalidAlertException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Erreur lors de la suppression de la vidéo", e.getMessage()));
        }
    }

    // ========== Search & Filter Endpoints ==========

    /**
     * Récupère les alertes par catégorie
     */
    @Operation(summary = "Get alerts by category with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of alerts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/category/{categoryId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAlertsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /api/alerts/category/{} - Fetching by category", categoryId);
            Pageable pageable = PageRequest.of(page, size);
            Page<AlertResponseDTO> response = alertService.getAlertsByCategory(categoryId, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching alerts by category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la récupération des alertes", e.getMessage()));
        }
    }

    /**
     * Récupère les alertes par statut
     */
    @Operation(summary = "Get alerts by status with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of alerts retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAlertsByStatus(
            @PathVariable AlertStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /api/alerts/status/{} - Fetching by status", status);
            Pageable pageable = PageRequest.of(page, size);
            Page<AlertResponseDTO> response = alertService.getAlertsByStatus(status, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching alerts by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la récupération des alertes", e.getMessage()));
        }
    }

    /**
     * Recherche les alertes par mot-clé
     */
    @Operation(summary = "Search alerts by keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchAlerts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /api/alerts/search - Searching with keyword: {}", keyword);
            Pageable pageable = PageRequest.of(page, size);
            Page<AlertResponseDTO> response = alertService.searchAlerts(keyword, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching alerts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors de la recherche", e.getMessage()));
        }
    }

    // ========== Status Management Endpoints ==========

    /**
     * Change le statut d'une alerte
     */
    @Operation(summary = "Change the status of an alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Alert or User not found")
    })
    @PatchMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @RequestParam AlertStatusEnum status) {
        try {
            log.info("PATCH /api/alerts/{}/status - Changing status to {}", id, status);
            Long userId = getCurrentUserId();
            alertService.verifyUserCanChangeStatus(userId);
            AlertResponseDTO response = alertService.changeStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès refusé", e.getMessage()));
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        } catch (Exception e) {
            log.error("Error changing status", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Erreur lors du changement de statut", e.getMessage()));
        }
    }

    /**
     * Obtient le statut d'une alerte
     */
    @Operation(summary = "Get the status of an alert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Alert not found")
    })
    @GetMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatus(@PathVariable Long id) {
        try {
            log.info("GET /api/alerts/{}/status - Getting status", id);
            AlertStatusEnum status = alertService.getAlertStatus(id);
            Map<String, Object> response = new HashMap<>();
            response.put("alertId", id);
            response.put("status", status);
            return ResponseEntity.ok(response);
        } catch (AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Alerte non trouvée", e.getMessage()));
        }
    }

    // ========== Statistics Endpoints ==========

    /**
     * Obtient le nombre d'alertes par statut
     */
    @Operation(summary = "Count alerts by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/stats/count-by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> countByStatus(@RequestParam AlertStatusEnum status) {
        try {
            log.info("GET /api/alerts/stats/count-by-status - Counting by status: {}", status);
            Long count = alertService.countAlertsByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("status", status);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error counting alerts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du comptage", e.getMessage()));
        }
    }

    /**
     * Obtient le nombre d'alertes de l'utilisateur actuel
     */
    @Operation(summary = "Count alerts of the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/stats/user-count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> countUserAlerts() {
        try {
            log.info("GET /api/alerts/stats/user-count - Counting user alerts");
            Long userId = getCurrentUserId();
            Long count = alertService.countUserAlerts(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        }
    }

    // ========== Helper Methods ==========

    /**
     * Obtient l'ID de l'utilisateur actuel
     */
    private Long getCurrentUserId() {
        return alertService.getCurrentUserId();
    }

    /**
     * Crée une réponse d'erreur
     */
    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}

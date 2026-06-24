package org.urban.alert.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.urban.alert.dto.problem.*;
import org.urban.alert.entity.enums.ProblemStatusEnum;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.exception.alert.AlertNotFoundException;
import org.urban.alert.exception.problem.*;
import org.urban.alert.service.ProblemService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/problems")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Problems", description = "API for managing problems (aggregated alerts)")
public class ProblemController {

    private final ProblemService problemService;

    // ========== CRUD Endpoints ==========

    /**
     * Crée un nouveau problème (accessible seulement aux SUPER_AGENT)
     * Logique:
     * 1. Vérifie que l'utilisateur est SUPER_AGENT
     * 2. Vérifie que assigned_to est AGENT
     * 3. Crée le problème avec les alertes spécifiées
     */
    @Operation(summary = "Create a new problem (SUPER_AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Problem created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or validation error"),
            @ApiResponse(responseCode = "403", description = "User is not a SUPER_AGENT"),
            @ApiResponse(responseCode = "404", description = "User or agent not found")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createProblem(@Valid @RequestBody ProblemCreateDTO request) {
        try {
            log.info("POST /v1/problems - Creating new problem");
            Long userId = getCurrentUserId();
            ProblemResponseDTO response = problemService.createProblem(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (NotSuperAgentException e) {
            log.warn("User {} is not a SUPER_AGENT", getCurrentUserId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès refusé", e.getMessage()));
        } catch (NotAgentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Agent invalide", e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        } catch (NoAlertsAssignedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Aucune alerte assignée", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating problem", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    /**
     * Récupère un problème par ID
     */
    @Operation(summary = "Get a problem by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProblemById(@PathVariable Long id) {
        try {
            log.info("GET /v1/problems/{} - Fetching problem", id);
            ProblemResponseDTO response = problemService.getProblemById(id);
            return ResponseEntity.ok(response);
        } catch (ProblemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Problème non trouvé", e.getMessage()));
        }
    }

    /**
     * Récupère tous les problèmes avec pagination
     */
    @Operation(summary = "Get all problems with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PreAuthorize("isAuthenticated()")

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /v1/problems - Fetching all problems");
            Pageable pageable = PageRequest.of(page, size);
            Page<ProblemResponseDTO> response = problemService.getAllProblems(pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching problems", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    /**
     * Récupère mes problèmes créés (pour SUPER_AGENT)
     */
    @Operation(summary = "Get my created problems")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "My problems retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @GetMapping(value = "/user/my-problems", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMyProblems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /v1/problems/user/my-problems - Fetching my problems");
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);
            Page<ProblemResponseDTO> response = problemService.getProblemsCreatedBy(userId, pageable);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        }
    }

    /**
     * Récupère les problèmes liés aux alertes du citoyen connecté
     */
    @Operation(summary = "Get problems related to my alerts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('CITOYEN')")
    @GetMapping(value = "/user/my-alert-problems", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProblemsRelatedToMyAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /v1/problems/user/my-alert-problems - Fetching problems related to my alerts");
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);
            Page<ProblemResponseDTO> response = problemService.getProblemsRelatedToMyAlerts(userId, pageable);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        }
    }

    /**
     * Met à jour un problème (ajouter/retirer des alertes)
     */
    @Operation(summary = "Update a problem (add/remove alerts, change assigned agent)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Permission denied"),
            @ApiResponse(responseCode = "404", description = "Problem not found"),
            @ApiResponse(responseCode = "409", description = "Problem cannot be modified")
    })

    @PreAuthorize("hasAnyRole('SUPER_AGENT')")

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateProblem(
            @PathVariable Long id,
            @Valid @RequestBody ProblemUpdateDTO request) {
        try {
            log.info("PUT /v1/problems/{} - Updating problem", id);
            Long userId = getCurrentUserId();
            ProblemResponseDTO response = problemService.updateProblem(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (ProblemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Problème non trouvé", e.getMessage()));
        } catch (ProblemCannotBeModifiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Problème non modifiable", e.getMessage()));
        } catch (InvalidAssignmentException | NotAgentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Assignation invalide", e.getMessage()));
        } catch (InvalidProblemException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Permission refusée", e.getMessage()));
        }
    }

    /**
     * Supprime un problème (seulement SUPER_AGENT créateur, et status doit être NEW)
     */
    @Operation(summary = "Delete a problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Problem deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Only creator can delete"),
            @ApiResponse(responseCode = "404", description = "Problem not found"),
            @ApiResponse(responseCode = "409", description = "Problem cannot be deleted")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProblem(@PathVariable Long id) {
        try {
            log.info("DELETE /v1/problems/{} - Deleting problem", id);
            Long userId = getCurrentUserId();
            problemService.deleteProblem(id, userId);
            return ResponseEntity.noContent().build();
        } catch (ProblemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Problème non trouvé", e.getMessage()));
        } catch (ProblemCannotBeModifiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Problème non supprimable", e.getMessage()));
        } catch (InvalidProblemException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Permission refusée", e.getMessage()));
        }
    }

    // ========== Alert Management Endpoints ==========

    /**
     * Ajoute une alerte au problème
     */
    @Operation(summary = "Add an alert to a problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Alert added successfully"),
            @ApiResponse(responseCode = "404", description = "Problem or alert not found"),
            @ApiResponse(responseCode = "409", description = "Alert already assigned")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @PostMapping(value = "/{problemId}/alerts/{alertId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addAlertToProblem(
            @PathVariable Long problemId,
            @PathVariable Long alertId) {
        try {
            log.info("POST /v1/problems/{}/alerts/{}", problemId, alertId);
            ProblemResponseDTO response = problemService.addAlertToProblem(problemId, alertId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProblemNotFoundException | AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ressource non trouvée", e.getMessage()));
        } catch (AlertAlreadyAssignedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Alerte déjà assignée", e.getMessage()));
        }
    }

    /**
     * Retire une alerte du problème
     */
    @Operation(summary = "Remove an alert from a problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alert removed successfully"),
            @ApiResponse(responseCode = "404", description = "Problem or alert not found")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @DeleteMapping(value = "/{problemId}/alerts/{alertId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeAlertFromProblem(
            @PathVariable Long problemId,
            @PathVariable Long alertId) {
        try {
            log.info("DELETE /v1/problems/{}/alerts/{}", problemId, alertId);
            ProblemResponseDTO response = problemService.removeAlertFromProblem(problemId, alertId);
            return ResponseEntity.ok(response);
        } catch (ProblemNotFoundException | AlertNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ressource non trouvée", e.getMessage()));
        }
    }

    // ========== Status Management Endpoints ==========

    /**
     * Change le statut d'un problème
     * Logique:
     * 1. Vérifie les permissions (SUPER_AGENT créateur ou AGENT assigné)
     * 2. Change le statut du problème
     * 3. Si RESOLVED: toutes les alertes → RESOLVED
     * 4. Si REJECTED: toutes les alertes → REJECTED
     * 5. Ajoute un enregistrement à l'historique
     * 6. Notifie les citoyens
     */
    @Operation(summary = "Change problem status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status changed successfully"),
            @ApiResponse(responseCode = "403", description = "Permission denied"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })

    @PreAuthorize("hasAnyRole('SUPER_AGENT', 'AGENT')")

    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProblemStatusChangeDTO request) {
        try {
            log.info("PATCH /v1/problems/{}/status - Changing to {}", id, request.getNewStatus());
            Long userId = getCurrentUserId();
            ProblemResponseDTO response = problemService.changeStatus(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (ProblemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Problème non trouvé", e.getMessage()));
        } catch (InvalidProblemException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Permission refusée", e.getMessage()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        } catch (Exception e) {
            log.error("Error changing status for problem {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur lors du changement de statut", e.getMessage()));
        }
    }

    /**
     * Récupère le statut actuel du problème
     */
    @Operation(summary = "Get problem status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{id}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatus(@PathVariable Long id) {
        try {
            log.info("GET /v1/problems/{}/status", id);
            ProblemStatusEnum status = problemService.getProblemStatus(id);
            return ResponseEntity.ok(Map.of("problemId", id, "status", status));
        } catch (ProblemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Problème non trouvé", e.getMessage()));
        }
    }

    /**
     * Récupère l'historique des changements de statut
     */
    @Operation(summary = "Get problem status history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @PreAuthorize("hasAnyRole('AGENT', 'SUPER_AGENT', 'ADMIN')")

    @GetMapping(value = "/{id}/status-history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatusHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /v1/problems/{}/status-history", id);
            Pageable pageable = PageRequest.of(page, size);
            Page<ProblemStatusHistoryDTO> response = problemService.getStatusHistory(id, pageable);
            return ResponseEntity.ok(response);
        } catch (ProblemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Problème non trouvé", e.getMessage()));
        }
    }

    // ========== Search & Filter Endpoints ==========

    /**
     * Récupère les problèmes par statut
     */
    @Operation(summary = "Get problems by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problems retrieved successfully")
    })
    @PreAuthorize("hasAnyRole('AGENT', 'SUPER_AGENT', 'ADMIN')")
    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByStatus(
            @PathVariable ProblemStatusEnum status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /v1/problems/status/{}", status);
            Pageable pageable = PageRequest.of(page, size);
            Page<ProblemResponseDTO> response = problemService.getProblemsByStatus(status, pageable);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching problems by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    // ========== Statistics Endpoints ==========

    /**
     * Compte les problèmes par statut
     */
    @Operation(summary = "Count problems by status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    @PreAuthorize("hasAnyRole('SUPER_AGENT', 'ADMIN')")

    @GetMapping(value = "/stats/count-by-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> countByStatus(@RequestParam ProblemStatusEnum status) {
        try {
            log.info("GET /v1/problems/stats/count-by-status?status={}", status);
            Long count = problemService.countProblemsByStatus(status);
            return ResponseEntity.ok(Map.of("status", status, "count", count));
        } catch (Exception e) {
            log.error("Error counting problems by status", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    /**
     * Compte mes problèmes créés
     */
    @Operation(summary = "Count my created problems")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @GetMapping(value = "/stats/my-count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> countMyProblems() {
        try {
            log.info("GET /v1/problems/stats/my-count");
            Long userId = getCurrentUserId();
            Long count = problemService.countProblemsCreatedBy(userId);
            return ResponseEntity.ok(Map.of("userId", userId, "count", count));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        }
    }


    // ========== Helper Methods ==========

    /**
     * Récupère l'ID de l'utilisateur actuel depuis le contexte de sécurité
     */
    private Long getCurrentUserId() {
        return problemService.getCurrentUserId();
    }

    /**
     * Crée une réponse d'erreur standardisée
     */
    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
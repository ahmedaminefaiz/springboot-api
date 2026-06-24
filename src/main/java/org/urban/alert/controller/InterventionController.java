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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.urban.alert.dto.alert.AddMediaRequestDTO;
import org.urban.alert.dto.intervention.CreateInterventionRequestDTO;
import org.urban.alert.dto.intervention.CreateInterventionUpdateRequestDTO;
import org.urban.alert.dto.intervention.InterventionResponseDTO;
import org.urban.alert.dto.intervention.InterventionUpdateResponseDTO;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.exception.intervention.InterventionCannotBeModifiedException;
import org.urban.alert.exception.intervention.InterventionNotFoundException;
import org.urban.alert.exception.problem.InvalidProblemException;
import org.urban.alert.exception.problem.NotAgentException;
import org.urban.alert.exception.problem.ProblemNotFoundException;
import org.urban.alert.service.InterventionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/interventions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Interventions", description = "API for managing interventions")
public class InterventionController {

    private final InterventionService interventionService;

    @Operation(summary = "Create a new intervention (SUPER_AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Intervention created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Problem or agent not found")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createIntervention(@Valid @RequestBody CreateInterventionRequestDTO request) {
        try {
            log.info("POST /v1/interventions - Creating intervention");
            Long userId = getCurrentUserId();
            InterventionResponseDTO response = interventionService.createIntervention(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (ProblemNotFoundException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ressource non trouvée", e.getMessage()));
        } catch (NotAgentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Agent invalide", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating intervention", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Erreur serveur", e.getMessage()));
        }
    }

    @Operation(summary = "Get my interventions (AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interventions retrieved successfully")
    })
    @PreAuthorize("hasRole('AGENT')")
    @GetMapping(value = "/my-interventions", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getMyInterventions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /v1/interventions/my-interventions");
            Long userId = getCurrentUserId();
            Pageable pageable = PageRequest.of(page, size);
            Page<InterventionResponseDTO> response = interventionService.getMyInterventions(userId, pageable);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Utilisateur non trouvé", e.getMessage()));
        }
    }

    @Operation(summary = "Get interventions for a problem")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Interventions retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Problem not found")
    })
    @PreAuthorize("hasAnyRole('AGENT', 'SUPER_AGENT')")
    @GetMapping(value = "/problem/{problemId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByProblem(
            @PathVariable Long problemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("GET /v1/interventions/problem/{}", problemId);
            Long userId = getCurrentUserId();
            String role = getCurrentUserRole();
            Pageable pageable = PageRequest.of(page, size);
            Page<InterventionResponseDTO> response = interventionService.getByProblem(problemId, userId, role, pageable);
            return ResponseEntity.ok(response);
        } catch (ProblemNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Problème non trouvé", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès refusé", e.getMessage()));
        }
    }

    @Operation(summary = "Get intervention by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervention retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Intervention not found")
    })
    @PreAuthorize("hasAnyRole('AGENT', 'SUPER_AGENT')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            log.info("GET /v1/interventions/{}", id);
            Long userId = getCurrentUserId();
            String role = getCurrentUserRole();
            InterventionResponseDTO response = interventionService.getById(id, userId, role);
            return ResponseEntity.ok(response);
        } catch (InterventionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Intervention non trouvée", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès refusé", e.getMessage()));
        }
    }

    @Operation(summary = "Update intervention — creates an InterventionUpdate (AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervention updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Intervention not found"),
            @ApiResponse(responseCode = "409", description = "Intervention is closed")
    })
    @PreAuthorize("hasRole('AGENT')")
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateIntervention(
            @PathVariable Long id,
            @Valid @RequestBody CreateInterventionUpdateRequestDTO request) {
        try {
            log.info("PATCH /v1/interventions/{}", id);
            Long userId = getCurrentUserId();
            InterventionResponseDTO response = interventionService.updateIntervention(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (InterventionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Intervention non trouvée", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès refusé", e.getMessage()));
        } catch (InterventionCannotBeModifiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Intervention clôturée", e.getMessage()));
        } catch (InvalidProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Opération invalide", e.getMessage()));
        }
    }

    @Operation(summary = "Create an intervention update (AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Intervention not found"),
            @ApiResponse(responseCode = "409", description = "Intervention is closed")
    })
    @PreAuthorize("hasRole('AGENT')")
    @PostMapping(value = "/{id}/updates", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createInterventionUpdate(
            @PathVariable Long id,
            @Valid @RequestBody CreateInterventionUpdateRequestDTO request) {
        try {
            log.info("POST /v1/interventions/{}/updates", id);
            Long userId = getCurrentUserId();
            InterventionResponseDTO response = interventionService.createInterventionUpdate(id, request, userId);
            return ResponseEntity.ok(response);
        } catch (InterventionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Intervention non trouvée", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès refusé", e.getMessage()));
        } catch (InterventionCannotBeModifiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Intervention clôturée", e.getMessage()));
        } catch (InvalidProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Opération invalide", e.getMessage()));
        }
    }

    @Operation(summary = "Get intervention update history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updates retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Intervention not found")
    })
    @PreAuthorize("hasAnyRole('AGENT', 'SUPER_AGENT')")
    @GetMapping(value = "/{id}/updates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInterventionUpdates(@PathVariable Long id) {
        try {
            log.info("GET /v1/interventions/{}/updates", id);
            Long userId = getCurrentUserId();
            String role = getCurrentUserRole();
            List<InterventionUpdateResponseDTO> response = interventionService.getInterventionUpdates(id, userId, role);
            return ResponseEntity.ok(response);
        } catch (InterventionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Intervention non trouvée", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(createErrorResponse("Accès refusé", e.getMessage()));
        }
    }

    @Operation(summary = "Add a photo to an intervention (SUPER_AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Photo added successfully"),
            @ApiResponse(responseCode = "400", description = "Max photos reached"),
            @ApiResponse(responseCode = "404", description = "Intervention not found")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @PostMapping(value = "/{id}/photos", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addPhoto(
            @PathVariable Long id,
            @Valid @RequestBody AddMediaRequestDTO request) {
        try {
            log.info("POST /v1/interventions/{}/photos", id);
            Long userId = getCurrentUserId();
            InterventionResponseDTO response = interventionService.addPhoto(id, request.getMediaUrl(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InterventionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Intervention non trouvée", e.getMessage()));
        } catch (InvalidProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Limite atteinte", e.getMessage()));
        }
    }

    @Operation(summary = "Remove a photo from an intervention (SUPER_AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo removed successfully"),
            @ApiResponse(responseCode = "400", description = "Photo not found in intervention"),
            @ApiResponse(responseCode = "404", description = "Intervention not found")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @DeleteMapping(value = "/{id}/photos", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removePhoto(
            @PathVariable Long id,
            @RequestParam String photoUrl) {
        try {
            log.info("DELETE /v1/interventions/{}/photos", id);
            Long userId = getCurrentUserId();
            InterventionResponseDTO response = interventionService.removePhoto(id, photoUrl, userId);
            return ResponseEntity.ok(response);
        } catch (InterventionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Intervention non trouvée", e.getMessage()));
        } catch (InvalidProblemException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(createErrorResponse("Photo non trouvée", e.getMessage()));
        }
    }

    @Operation(summary = "Close an intervention (SUPER_AGENT only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Intervention closed successfully"),
            @ApiResponse(responseCode = "404", description = "Intervention not found"),
            @ApiResponse(responseCode = "409", description = "Already closed")
    })
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @PatchMapping(value = "/{id}/close", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> closeIntervention(@PathVariable Long id) {
        try {
            log.info("PATCH /v1/interventions/{}/close", id);
            Long userId = getCurrentUserId();
            InterventionResponseDTO response = interventionService.closeIntervention(id, userId);
            return ResponseEntity.ok(response);
        } catch (InterventionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Intervention non trouvée", e.getMessage()));
        } catch (InterventionCannotBeModifiedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(createErrorResponse("Intervention déjà clôturée", e.getMessage()));
        }
    }

    @ExceptionHandler(org.springframework.web.method.annotation.HandlerMethodValidationException.class)
    public ResponseEntity<?> handleValidation(org.springframework.web.method.annotation.HandlerMethodValidationException ex) {
        String message = ex.getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Erreur de validation");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(createErrorResponse("Erreur de validation", message));
    }

    private Long getCurrentUserId() {
        return interventionService.getCurrentUserId();
    }

    private String getCurrentUserRole() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();
    }

    private Map<String, Object> createErrorResponse(String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("error", error);
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}

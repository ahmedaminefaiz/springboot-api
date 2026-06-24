package org.urban.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.urban.alert.dto.criticality.CriticalityCreateDTO;
import org.urban.alert.dto.criticality.CriticalityResponseDTO;
import org.urban.alert.exception.CriticalityNotFoundException;
import org.urban.alert.service.CriticalityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/criticalities")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Criticalities", description = "API for managing problem criticality levels")
public class CriticalityController {

    private final CriticalityService criticalityService;

    @Operation(summary = "Get all criticality levels")
    @PreAuthorize("isAuthenticated()")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CriticalityResponseDTO>> getAllCriticalities() {
        log.info("GET /v1/criticalities");
        return ResponseEntity.ok(criticalityService.getAllCriticalities());
    }

    @Operation(summary = "Get a criticality level by ID")
    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCriticalityById(@PathVariable Long id) {
        try {
            log.info("GET /v1/criticalities/{}", id);
            return ResponseEntity.ok(criticalityService.getCriticalityById(id));
        } catch (CriticalityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "Create a new criticality level (SUPER_AGENT only)")
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCriticality(@Valid @RequestBody CriticalityCreateDTO request) {
        try {
            log.info("POST /v1/criticalities - {}", request.getName());
            CriticalityResponseDTO response = criticalityService.createCriticality(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "Update a criticality level (SUPER_AGENT only)")
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateCriticality(@PathVariable Long id,
                                               @Valid @RequestBody CriticalityCreateDTO request) {
        try {
            log.info("PUT /v1/criticalities/{}", id);
            return ResponseEntity.ok(criticalityService.updateCriticality(id, request));
        } catch (CriticalityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse(e.getMessage()));
        }
    }

    @Operation(summary = "Delete a criticality level (SUPER_AGENT only)")
    @PreAuthorize("hasRole('SUPER_AGENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCriticality(@PathVariable Long id) {
        try {
            log.info("DELETE /v1/criticalities/{}", id);
            criticalityService.deleteCriticality(id);
            return ResponseEntity.noContent().build();
        } catch (CriticalityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", message);
        body.put("timestamp", System.currentTimeMillis());
        return body;
    }
}
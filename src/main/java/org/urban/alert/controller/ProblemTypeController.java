package org.urban.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.urban.alert.annotation.Audit;
import org.urban.alert.dto.problemtype.CreateProblemTypeRequestDTO;
import org.urban.alert.dto.problemtype.ProblemTypeResponseDTO;
import org.urban.alert.dto.problemtype.UpdateProblemTypeRequestDTO;
import org.urban.alert.service.ProblemTypeService;

import java.util.List;

@RestController
@RequestMapping("/v1/problem-types")
@RequiredArgsConstructor

public class ProblemTypeController {

    private final ProblemTypeService problemTypeService;

    @Operation(summary = "Get all problem types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Audit
    @PreAuthorize("hasRole('CITOYEN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProblemTypeResponseDTO>> getAll() {
        return ResponseEntity.ok(problemTypeService.getAll());
    }

    @Operation(summary = "Get a problem type by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem type returned successfully"),
            @ApiResponse(responseCode = "404", description = "Problem type not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Audit
    @PreAuthorize("hasRole('CITOYEN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(problemTypeService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @Operation(summary = "Create a new problem type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Problem type created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or name conflict"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Audit
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody CreateProblemTypeRequestDTO dto) {
        try {
            ProblemTypeResponseDTO response = problemTypeService.create(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Update an existing problem type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem type updated successfully"),
            @ApiResponse(responseCode = "404", description = "Problem type not found"),
            @ApiResponse(responseCode = "400", description = "Validation error or name conflict"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Audit
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody UpdateProblemTypeRequestDTO dto) {
        try {
            return ResponseEntity.ok(problemTypeService.update(id, dto));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Delete a problem type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem type deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Problem type not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Audit
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            problemTypeService.delete(id);
            return ResponseEntity.ok("Problem type deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

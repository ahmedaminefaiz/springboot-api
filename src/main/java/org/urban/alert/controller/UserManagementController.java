package org.urban.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.urban.alert.annotation.Audit;
import org.urban.alert.dto.UserSummaryResponseDTO;
import org.urban.alert.exception.ApprovalException;
import org.urban.alert.exception.UserNotFoundException;
import org.urban.alert.service.UserManagementService;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://ebd2-frontendapp-daf17h-3adb20-192-166-204-204.traefik.me/")
@RequestMapping("/v1/user-management")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Operation(summary = "Get all agents pending approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Audit
    @GetMapping(value = "/agents/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SUPER_AGENT')")
    public ResponseEntity<List<UserSummaryResponseDTO>> getPendingAgents() {
        return ResponseEntity.ok(userManagementService.getPendingAgents());
    }

    @Operation(summary = "Approve an agent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agent approved successfully"),
            @ApiResponse(responseCode = "400", description = "User is not a pending agent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Audit
    @PostMapping(value = "/agents/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SUPER_AGENT')")
    public ResponseEntity<?> approveAgent(@PathVariable Long id) {
        try {
            userManagementService.approveAgent(id);
            return ResponseEntity.ok("Agent approved successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ApprovalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Reject an agent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Agent rejected"),
            @ApiResponse(responseCode = "400", description = "User is not a pending agent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Audit
    @PostMapping(value = "/agents/{id}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('SUPER_AGENT')")
    public ResponseEntity<?> rejectAgent(@PathVariable Long id) {
        try {
            userManagementService.rejectAgent(id);
            return ResponseEntity.ok("Agent rejected");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ApprovalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Get all super-agents pending approval")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Audit
    @GetMapping(value = "/super-agents/pending", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserSummaryResponseDTO>> getPendingSuperAgents() {
        return ResponseEntity.ok(userManagementService.getPendingSuperAgents());
    }

    @Operation(summary = "Approve a super-agent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Super-agent approved successfully"),
            @ApiResponse(responseCode = "400", description = "User is not a pending super-agent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Audit
    @PostMapping(value = "/super-agents/{id}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveSuperAgent(@PathVariable Long id) {
        try {
            userManagementService.approveSuperAgent(id);
            return ResponseEntity.ok("Super-agent approved successfully");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ApprovalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Reject a super-agent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Super-agent rejected"),
            @ApiResponse(responseCode = "400", description = "User is not a pending super-agent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @Audit
    @PostMapping(value = "/super-agents/{id}/reject", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectSuperAgent(@PathVariable Long id) {
        try {
            userManagementService.rejectSuperAgent(id);
            return ResponseEntity.ok("Super-agent rejected");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (ApprovalException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
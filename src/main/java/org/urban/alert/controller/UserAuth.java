package org.urban.alert.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.urban.alert.annotation.Audit;
import org.urban.alert.dto.login.LoginRequestDTO;
import org.urban.alert.dto.signup.SignupRequestDTO;
import org.urban.alert.dto.verifyphone.VerifyPhoneRequestDTO;
import org.urban.alert.service.AuthService;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class UserAuth {

    private final AuthenticationManager authenticationManager;
    private final AuthService authService;

    @Operation(summary = "Login with phone and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
            @ApiResponse(responseCode = "401", description = "Invalid phone or password"),
            @ApiResponse(responseCode = "403", description = "Account not active")
    })
    @Audit
    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO credentials) {
        String normalizedPhone = credentials.getPhone().replaceAll("[\\s-]+", "").replaceFirst("^0([67])", "+212$1");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(normalizedPhone,
                            credentials.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid phone or password");
        }

        try {
            return ResponseEntity.ok(authService.login(normalizedPhone));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered, verification code sent via WhatsApp"),
            @ApiResponse(responseCode = "400", description = "Required field missing or invalid")
    })
    @Audit
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequestDTO credentials) {
        try {
            authService.register(credentials);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Registration successful. A verification code has been sent to your WhatsApp.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Verify phone number with OTP")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Phone verified, JWT returned (CITOYEN only)"),
            @ApiResponse(responseCode = "202", description = "Phone verified, account pending approval"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification code")
    })
    @Audit
    @PostMapping(value = "/verify-phone",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> verifyPhone(@Valid @RequestBody VerifyPhoneRequestDTO request) {
        try {
            var response = authService.verifyPhone(request.getPhone(),
                    request.getCode());
            if (response != null) {
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.accepted().body("Phone verified. Your account is pending approval.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
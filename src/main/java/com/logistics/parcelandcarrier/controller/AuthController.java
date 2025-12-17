// =============================================================================
// AuthController.java - Controller d'authentification
// =============================================================================
package com.logistics.parcelandcarrier.controller;

import com.logistics.parcelandcarrier.dto.request.LoginRequest;
import com.logistics.parcelandcarrier.dto.response.LoginResponse;
import com.logistics.parcelandcarrier.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Login", description = "Authenticate user and get JWT token")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
    @ApiResponse(responseCode = "401", description = "Invalid credentials"),
    @ApiResponse(responseCode = "400", description = "Validation error")
  })
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    log.info("Login request received for user: {}", request.getLogin());

    LoginResponse response = authService.authenticate(request);

    return ResponseEntity.ok(response);
  }
}

// =============================================================================
// AdminPackageController.java - Controller pour gestion des colis (Admin)
// =============================================================================

// =============================================================================
// TransporterPackageController.java - Controller colis (Transporteur)
// =============================================================================

// =============================================================================
// TransporterController.java - Controller gestion transporteurs (Admin)
// =============================================================================

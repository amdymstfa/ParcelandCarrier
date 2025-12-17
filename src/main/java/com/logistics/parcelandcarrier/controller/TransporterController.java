package com.logistics.parcelandcarrier.controller;

import com.logistics.parcelandcarrier.dto.request.TransporterRequest;
import com.logistics.parcelandcarrier.dto.response.UserResponse;
import com.logistics.parcelandcarrier.enums.Specialty;
import com.logistics.parcelandcarrier.service.TransporterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for transporter and user management (Admin only)
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Admin - Users & Transporters", description = "User and transporter management endpoints for admins")
public class TransporterController {

  private final TransporterService transporterService;

  @Operation(summary = "List all users", description = "Get all users (admins and transporters)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping("/users")
  public ResponseEntity<List<UserResponse>> listUsers() {
    log.debug("Listing all users");

    List<UserResponse> users = transporterService.findAllUsers();

    return ResponseEntity.ok(users);
  }

  @Operation(summary = "List transporters",
    description = "List all transporters with optional specialty filter")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Transporters retrieved successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping("/transporters")
  public ResponseEntity<Page<UserResponse>> listTransporters(
    @Parameter(description = "Filter by specialty")
    @RequestParam(required = false) Specialty specialty,

    @PageableDefault(size = 10) Pageable pageable
  ) {
    log.debug("Listing transporters with specialty filter: {}", specialty);

    Page<UserResponse> transporters = transporterService.findTransportersWithFilters(
      specialty, pageable
    );

    return ResponseEntity.ok(transporters);
  }

  @Operation(summary = "Create transporter", description = "Create a new transporter")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Transporter created successfully"),
    @ApiResponse(responseCode = "400", description = "Validation error or login already exists"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PostMapping("/transporters")
  public ResponseEntity<UserResponse> createTransporter(
    @Valid @RequestBody TransporterRequest request
  ) {
    log.info("Creating new transporter: {}", request.getLogin());

    UserResponse response = transporterService.createTransporter(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "Update transporter", description = "Update an existing transporter")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Transporter updated successfully"),
    @ApiResponse(responseCode = "404", description = "Transporter not found"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PutMapping("/transporters/{id}")
  public ResponseEntity<UserResponse> updateTransporter(
    @Parameter(description = "Transporter ID", required = true)
    @PathVariable String id,

    @Valid @RequestBody TransporterRequest request
  ) {
    log.info("Updating transporter: {}", id);

    UserResponse response = transporterService.updateTransporter(id, request);

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Delete transporter",
    description = "Deactivate a transporter (soft delete)")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Transporter deactivated successfully"),
    @ApiResponse(responseCode = "404", description = "Transporter not found"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @DeleteMapping("/transporters/{id}")
  public ResponseEntity<Void> deleteTransporter(
    @Parameter(description = "Transporter ID", required = true)
    @PathVariable String id
  ) {
    log.info("Deactivating transporter: {}", id);

    transporterService.deactivateTransporter(id);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Activate user", description = "Activate a deactivated user account")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "User activated successfully"),
    @ApiResponse(responseCode = "404", description = "User not found"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PatchMapping("/users/{id}/activate")
  public ResponseEntity<UserResponse> activateUser(
    @Parameter(description = "User ID", required = true)
    @PathVariable String id
  ) {
    log.info("Activating user: {}", id);

    UserResponse response = transporterService.activateUser(id);

    return ResponseEntity.ok(response);
  }
}

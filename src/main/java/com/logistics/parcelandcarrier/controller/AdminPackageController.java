package com.logistics.parcelandcarrier.controller;

import com.logistics.parcelandcarrier.dto.request.PackageRequest;
import com.logistics.parcelandcarrier.dto.request.UpdateStatusRequest;
import com.logistics.parcelandcarrier.dto.response.PackageResponse;
import com.logistics.parcelandcarrier.enums.PackageStatus;
import com.logistics.parcelandcarrier.enums.PackageType;
import com.logistics.parcelandcarrier.service.PackageService;
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

/**
 * REST Controller for package management (Admin only)
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/packages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Admin - Packages", description = "Package management endpoints for admins")
public class AdminPackageController {

  private final PackageService packageService;

  @Operation(summary = "Create package", description = "Create a new package")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Package created successfully"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PostMapping
  public ResponseEntity<PackageResponse> createPackage(
    @Valid @RequestBody PackageRequest request
  ) {
    log.info("Creating new package of type: {}", request.getType());

    PackageResponse response = packageService.createPackage(request);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(summary = "List packages", description = "List all packages with optional filters")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Packages retrieved successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping
  public ResponseEntity<Page<PackageResponse>> listPackages(
    @Parameter(description = "Filter by package type")
    @RequestParam(required = false) PackageType type,

    @Parameter(description = "Filter by package status")
    @RequestParam(required = false) PackageStatus status,

    @PageableDefault(size = 10) Pageable pageable
  ) {
    log.debug("Listing packages with filters - type: {}, status: {}", type, status);

    Page<PackageResponse> packages = packageService.findAllWithFilters(
      type, status, pageable
    );

    return ResponseEntity.ok(packages);
  }

  @Operation(summary = "Search packages by address",
    description = "Search packages by destination address")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Packages retrieved successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping("/search")
  public ResponseEntity<Page<PackageResponse>> searchByAddress(
    @Parameter(description = "Address to search", required = true)
    @RequestParam String address,

    @PageableDefault(size = 10) Pageable pageable
  ) {
    log.debug("Searching packages by address: {}", address);

    Page<PackageResponse> packages = packageService.searchByAddress(address, pageable);

    return ResponseEntity.ok(packages);
  }

  @Operation(summary = "Update package", description = "Update an existing package")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Package updated successfully"),
    @ApiResponse(responseCode = "404", description = "Package not found"),
    @ApiResponse(responseCode = "400", description = "Validation error"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PutMapping("/{id}")
  public ResponseEntity<PackageResponse> updatePackage(
    @Parameter(description = "Package ID", required = true)
    @PathVariable String id,

    @Valid @RequestBody PackageRequest request
  ) {
    log.info("Updating package: {}", id);

    PackageResponse response = packageService.updatePackage(id, request);

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Assign package to transporter",
    description = "Assign a package to a specific transporter")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Package assigned successfully"),
    @ApiResponse(responseCode = "404", description = "Package or transporter not found"),
    @ApiResponse(responseCode = "400", description = "Assignment not possible"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PatchMapping("/{packageId}/assign/{transporterId}")
  public ResponseEntity<PackageResponse> assignPackage(
    @Parameter(description = "Package ID", required = true)
    @PathVariable String packageId,

    @Parameter(description = "Transporter ID", required = true)
    @PathVariable String transporterId
  ) {
    log.info("Assigning package {} to transporter {}", packageId, transporterId);

    PackageResponse response = packageService.assignPackageToTransporter(
      packageId, transporterId
    );

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Update package status", description = "Change the status of a package")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Status updated successfully"),
    @ApiResponse(responseCode = "404", description = "Package not found"),
    @ApiResponse(responseCode = "400", description = "Invalid status"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PatchMapping("/{id}/status")
  public ResponseEntity<PackageResponse> updateStatus(
    @Parameter(description = "Package ID", required = true)
    @PathVariable String id,

    @Valid @RequestBody UpdateStatusRequest request
  ) {
    log.info("Updating status of package {} to {}", id, request.getStatus());

    PackageResponse response = packageService.changeStatus(id, request.getStatus());

    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Delete package", description = "Delete a package")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "204", description = "Package deleted successfully"),
    @ApiResponse(responseCode = "404", description = "Package not found"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletePackage(
    @Parameter(description = "Package ID", required = true)
    @PathVariable String id
  ) {
    log.info("Deleting package: {}", id);

    packageService.deletePackage(id);

    return ResponseEntity.noContent().build();
  }
}

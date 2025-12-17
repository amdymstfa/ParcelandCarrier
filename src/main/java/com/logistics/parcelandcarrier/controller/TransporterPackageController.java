package com.logistics.parcelandcarrier.controller;

import com.logistics.parcelandcarrier.dto.request.UpdateStatusRequest;
import com.logistics.parcelandcarrier.dto.response.PackageResponse;
import com.logistics.parcelandcarrier.enums.PackageStatus;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for package management (Transporter only)
 */
@Slf4j
@RestController
@RequestMapping("/api/transporter/packages")
@RequiredArgsConstructor
@PreAuthorize("hasRole('TRANSPORTER')")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "Transporter - Packages", description = "Package management endpoints for transporters")
public class TransporterPackageController {

  private final PackageService packageService;

  @Operation(summary = "List my packages",
    description = "List packages assigned to the authenticated transporter")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Packages retrieved successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping
  public ResponseEntity<Page<PackageResponse>> listMyPackages(
    @Parameter(description = "Filter by package status")
    @RequestParam(required = false) PackageStatus status,

    @PageableDefault(size = 10) Pageable pageable,

    Authentication authentication
  ) {
    String transporterId = extractTransporterId(authentication);
    log.debug("Listing packages for transporter: {}", transporterId);

    Page<PackageResponse> packages = packageService.findMyPackagesWithFilters(
      transporterId, status, pageable
    );

    return ResponseEntity.ok(packages);
  }

  @Operation(summary = "Search my packages by address",
    description = "Search transporter's packages by destination address")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Packages retrieved successfully"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @GetMapping("/search")
  public ResponseEntity<Page<PackageResponse>> searchMyPackages(
    @Parameter(description = "Address to search", required = true)
    @RequestParam String address,

    @PageableDefault(size = 10) Pageable pageable,

    Authentication authentication
  ) {
    String transporterId = extractTransporterId(authentication);
    log.debug("Searching packages for transporter {} by address: {}",
      transporterId, address);

    Page<PackageResponse> packages = packageService.searchMyPackagesByAddress(
      transporterId, address, pageable
    );

    return ResponseEntity.ok(packages);
  }

  @Operation(summary = "Update my package status",
    description = "Update the status of a package assigned to the transporter")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Status updated successfully"),
    @ApiResponse(responseCode = "404", description = "Package not found"),
    @ApiResponse(responseCode = "400", description = "Invalid status or not authorized"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  @PatchMapping("/{id}/status")
  public ResponseEntity<PackageResponse> updateMyPackageStatus(
    @Parameter(description = "Package ID", required = true)
    @PathVariable String id,

    @Valid @RequestBody UpdateStatusRequest request,

    Authentication authentication
  ) {
    String transporterId = extractTransporterId(authentication);
    log.info("Transporter {} updating status of package {} to {}",
      transporterId, id, request.getStatus());

    PackageResponse response = packageService.changeStatusMyPackage(
      id, transporterId, request.getStatus()
    );

    return ResponseEntity.ok(response);
  }

  /**
   * Extract transporter ID from authentication
   * Note: In a real implementation, you would extract this from the JWT token
   * For now, we use the username as the transporter login
   */
  private String extractTransporterId(Authentication authentication) {
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();
      // In a real scenario, you'd fetch the user ID from UserDetails or JWT
      // For this implementation, return the username (login)
      return userDetails.getUsername();
    }
    throw new IllegalStateException("Unable to extract transporter ID from authentication");
  }
}

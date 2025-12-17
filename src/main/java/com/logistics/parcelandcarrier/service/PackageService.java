package com.logistics.parcelandcarrier.service;

import com.logistics.parcelandcarrier.dto.request.PackageRequest;
import com.logistics.parcelandcarrier.dto.response.PackageResponse;
import com.logistics.parcelandcarrier.entity.Package;
import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.enums.PackageStatus;
import com.logistics.parcelandcarrier.enums.PackageType;
import com.logistics.parcelandcarrier.exception.ResourceNotFoundException;
import com.logistics.parcelandcarrier.exception.SpecialtyIncompatibleException;
import com.logistics.parcelandcarrier.exception.TransporterUnavailableException;
import com.logistics.parcelandcarrier.mapper.PackageMapper;
import com.logistics.parcelandcarrier.repository.PackageRepository;
import com.logistics.parcelandcarrier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for package management operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PackageService {

  private final PackageRepository packageRepository;
  private final UserRepository userRepository;
  private final PackageMapper packageMapper;

  /**
   * Create a new package
   * @param request package creation request
   * @return created package
   */
  @Transactional
  public PackageResponse createPackage(PackageRequest request) {
    log.info("Creating new package of type: {}", request.getType());

    // Validate business rules
    request.validate();

    // Convert DTO to entity
    Package packageEntity = packageMapper.toEntity(request);

    // Save package
    Package savedPackage = packageRepository.save(packageEntity);

    log.info("Package created successfully with ID: {}", savedPackage.getId());

    return packageMapper.toResponse(savedPackage);
  }

  /**
   * Find all packages with filters and pagination (Admin view)
   * @param type package type filter (optional)
   * @param status package status filter (optional)
   * @param pageable pagination info
   * @return page of packages
   */
  public Page<PackageResponse> findAllWithFilters(
    PackageType type,
    PackageStatus status,
    Pageable pageable
  ) {
    log.debug("Finding packages with filters - type: {}, status: {}", type, status);

    Page<Package> packages;

    if (type != null && status != null) {
      packages = packageRepository.findByTypeAndStatus(status, type, pageable);
    } else if (type != null) {
      packages = packageRepository.findByType(type, pageable);
    } else if (status != null) {
      packages = packageRepository.findByStatus(status, pageable);
    } else {
      packages = packageRepository.findAll(pageable);
    }

    return packageMapper.toResponsePage(packages);
  }

  /**
   * Search packages by destination address
   * @param address address to search
   * @param pageable pagination info
   * @return page of packages
   */
  public Page<PackageResponse> searchByAddress(String address, Pageable pageable) {
    log.debug("Searching packages by address: {}", address);

    Page<Package> packages = packageRepository
      .findByDestinationAddressContainingIgnoreCase(address, pageable);

    return packageMapper.toResponsePage(packages);
  }

  /**
   * Update an existing package
   * @param id package ID
   * @param request update request
   * @return updated package
   */
  @Transactional
  public PackageResponse updatePackage(String id, PackageRequest request) {
    log.info("Updating package: {}", id);

    // Validate business rules
    request.validate();

    // Find existing package
    Package packageEntity = findPackageById(id);

    // Update entity from DTO
    packageMapper.updateEntityFromDto(request, packageEntity);

    // Save updated package
    Package updatedPackage = packageRepository.save(packageEntity);

    log.info("Package updated successfully: {}", id);

    return packageMapper.toResponse(updatedPackage);
  }

  /**
   * Assign package to a transporter
   * @param packageId package ID
   * @param transporterId transporter ID
   * @return assigned package
   */
  @Transactional
  public PackageResponse assignPackageToTransporter(String packageId, String transporterId) {
    log.info("Assigning package {} to transporter {}", packageId, transporterId);

    // Find package and transporter
    Package packageEntity = findPackageById(packageId);
    User transporter = findTransporterById(transporterId);

    // Verify package can be assigned
    if (!packageEntity.canBeAssigned()) {
      throw new IllegalStateException(
        "Package cannot be assigned in status: " + packageEntity.getStatus()
      );
    }

    // Verify specialty compatibility
    verifySpecialtyCompatibility(packageEntity, transporter);

    // Verify transporter availability
    verifyTransporterAvailability(transporter);

    // Assign package to transporter
    packageEntity.assignToTransporter(transporterId);

    // Update transporter status to ON_DELIVERY
    transporter.setOnDelivery();

    // Save both entities
    packageRepository.save(packageEntity);
    userRepository.save(transporter);

    log.info("Package {} assigned successfully to transporter {}",
      packageId, transporterId);

    return packageMapper.toResponse(packageEntity);
  }

  /**
   * Change package status
   * @param id package ID
   * @param newStatus new status
   * @return updated package
   */
  @Transactional
  public PackageResponse changeStatus(String id, PackageStatus newStatus) {
    log.info("Changing status of package {} to {}", id, newStatus);

    Package packageEntity = findPackageById(id);

    // Change status
    packageEntity.changeStatus(newStatus);

    // If package is delivered or cancelled, release transporter
    if (packageEntity.isFinished() && packageEntity.getTransporterId() != null) {
      releaseTransporter(packageEntity.getTransporterId());
    }

    // Save package
    Package updatedPackage = packageRepository.save(packageEntity);

    log.info("Package status changed successfully: {} -> {}", id, newStatus);

    return packageMapper.toResponse(updatedPackage);
  }

  /**
   * Delete a package
   * @param id package ID
   */
  @Transactional
  public void deletePackage(String id) {
    log.info("Deleting package: {}", id);

    Package packageEntity = findPackageById(id);

    // If package is assigned, release transporter
    if (packageEntity.isAssigned()) {
      releaseTransporter(packageEntity.getTransporterId());
    }

    packageRepository.delete(packageEntity);

    log.info("Package deleted successfully: {}", id);
  }

  /**
   * Find packages assigned to a transporter (Transporter view)
   * @param transporterId transporter ID
   * @param status status filter (optional)
   * @param pageable pagination info
   * @return page of packages
   */
  public Page<PackageResponse> findMyPackagesWithFilters(
    String transporterId,
    PackageStatus status,
    Pageable pageable
  ) {
    log.debug("Finding packages for transporter: {}", transporterId);

    Page<Package> packages;

    if (status != null) {
      packages = packageRepository.findByTransporterIdAndStatus(
        transporterId, status, pageable
      );
    } else {
      packages = packageRepository.findByTransporterId(transporterId, pageable);
    }

    return packageMapper.toResponsePage(packages);
  }

  /**
   * Search transporter's packages by address
   * @param transporterId transporter ID
   * @param address address to search
   * @param pageable pagination info
   * @return page of packages
   */
  public Page<PackageResponse> searchMyPackagesByAddress(
    String transporterId,
    String address,
    Pageable pageable
  ) {
    log.debug("Searching packages for transporter {} by address: {}",
      transporterId, address);

    Page<Package> packages = packageRepository
      .findByTransporterIdAndDestinationAddressContainingIgnoreCase(
        transporterId, address, pageable
      );

    return packageMapper.toResponsePage(packages);
  }

  /**
   * Change status of transporter's package
   * @param packageId package ID
   * @param transporterId transporter ID
   * @param newStatus new status
   * @return updated package
   */
  @Transactional
  public PackageResponse changeStatusMyPackage(
    String packageId,
    String transporterId,
    PackageStatus newStatus
  ) {
    log.info("Transporter {} changing status of package {} to {}",
      transporterId, packageId, newStatus);

    Package packageEntity = findPackageById(packageId);

    // Verify package ownership
    verifyPackageOwnership(packageEntity, transporterId);

    // Change status
    packageEntity.changeStatus(newStatus);

    // If package is delivered or cancelled, release transporter
    if (packageEntity.isFinished()) {
      releaseTransporter(transporterId);
    }

    // Save package
    Package updatedPackage = packageRepository.save(packageEntity);

    log.info("Package status changed successfully by transporter: {} -> {}",
      packageId, newStatus);

    return packageMapper.toResponse(updatedPackage);
  }

  // =========================================================================
  // Private helper methods
  // =========================================================================

  private Package findPackageById(String id) {
    return packageRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));
  }

  private User findTransporterById(String id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    if (!user.isTransporter()) {
      throw new IllegalArgumentException("User is not a transporter");
    }

    return user;
  }

  private void verifySpecialtyCompatibility(Package packageEntity, User transporter) {
    if (!transporter.canHandlePackage(packageEntity.getType())) {
      throw new SpecialtyIncompatibleException(
        packageEntity.getType(),
        transporter.getSpecialty()
      );
    }
  }

  private void verifyTransporterAvailability(User transporter) {
    if (!transporter.canTakeNewPackage()) {
      throw new TransporterUnavailableException(
        transporter.getId(),
        transporter.getStatus()
      );
    }
  }

  private void releaseTransporter(String transporterId) {
    userRepository.findById(transporterId).ifPresent(transporter -> {
      transporter.setAvailable();
      userRepository.save(transporter);
      log.info("Transporter {} released and set to AVAILABLE", transporterId);
    });
  }

  private void verifyPackageOwnership(Package packageEntity, String transporterId) {
    if (!transporterId.equals(packageEntity.getTransporterId())) {
      throw new IllegalArgumentException(
        "Package does not belong to this transporter"
      );
    }
  }
}

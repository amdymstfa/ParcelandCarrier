package com.logistics.parcelandcarrier.repository ;

import com.logistics.parcelandcarrier.entity.Package;
import com.logistics.parcelandcarrier.enums.PackageStatus;
import com.logistics.parcelandcarrier.enums.PackageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PackageRepository extends MongoRepository<Package, String> {

    /**
     * Find package by type with pagination
     * @param type of Package
     * @param pageable of Pagination
     * @return Page of package
     */
    Page<Package> findByType(PackageType type, Pageable pageable);

    /**
     * Find packages wih status by pagination
     * @param status of PackageStatus
     * @param pageable of Pagination
     * @return Page of packages
     */
    Page<Package> findByStatus(PackageStatus status, Pageable pageable);

    /**
     * Find package by type and status
     * @param status of package
     * @param type of package
     * @param pageable of package
     * @return Page of package
     */
    Page<Package> findByTypeAndStatus(PackageStatus status, PackageType type, Pageable pageable);

    /**
     * Find packages assigned to a specific transporter
     * @param transporterId the transporter ID
     * @param pageable pagination information
     * @return Page of packages
     */
    Page<Package> findByTransporterId(String transporterId, Pageable pageable);

    /**
     * Find packages by transporter and status
     * @param transporterId the transporter ID
     * @param status the package status
     * @param pageable pagination information
     * @return Page of packages
     */
    Page<Package> findByTransporterIdAndStatus(
            String transporterId,
            PackageStatus status,
            Pageable pageable
    );

    /**
     * Search packages by destination address (case-insensitive, contains)
     * @param address the address to search
     * @param pageable pagination information
     * @return Page of packages
     */
    Page<Package> findByDestinationAddressContainingIgnoreCase(
            String address,
            Pageable pageable
    );

    /**
     * Search packages by transporter and destination address
     * @param transporterId the transporter ID
     * @param address the address to search
     * @param pageable pagination information
     * @return Page of packages
     */
    Page<Package> findByTransporterIdAndDestinationAddressContainingIgnoreCase(
            String transporterId,
            String address,
            Pageable pageable
    );

    /**
     * Find all packages for a transporter (no pagination)
     * @param transporterId the transporter ID
     * @return List of packages
     */
    List<Package> findByTransporterId(String transporterId);

    /**
     * Count packages by transporter and status
     * @param transporterId the transporter ID
     * @param status the package status
     * @return number of packages
     */
    long countByTransporterIdAndStatus(String transporterId, PackageStatus status);

    /**
     * Count packages by status
     * @param status the package status
     * @return number of packages
     */
    long countByStatus(PackageStatus status);

    /**
     * Find packages created between dates
     * @param startDate start date
     * @param endDate end date
     * @param pageable pagination information
     * @return Page of packages
     */
    Page<Package> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * Find unassigned package
     * @param status pending
     * @param pageable pagination information
     * @return Page of package
     */
    @Query("{ 'status': ?0, $or: [ { 'transporterId': null }, { 'transporterId': '' } ] }")
    Page<Package> findUnassignedPackages(PackageStatus status, Pageable pageable);

    /**
     * Find packages by multiple statuses
     * @param statuses list of statuses
     * @param pageable pagination information
     * @return Page of packages
     */
    Page<Package> findByStatusIn(List<PackageStatus> statuses, Pageable pageable);

    /**
     * Custom query: Find packages needing attention (pending for more than X hours)
     * @param status should be PENDING
     * @param dateThreshold date threshold
     * @return List of packages
     */
    @Query("{'status' : ?0 , 'createAt' : {$lt : ?1}}")
    List<Package> findPackagesNeedingAttention(PackageStatus status, LocalDateTime dateThreshold);

    /**
     * Find packages by type with unassigned status
     * @param type the package type
     * @param status should be PENDING
     * @param pageable pagination information
     * @return Page of packages
     */
    @Query("{ 'type': ?0, 'status': ?1, $or: [ { 'transporterId': null }, { 'transporterId': '' } ] }")
    Page<Package> findUnassignedPackagesByType(
            PackageType type,
            PackageStatus status,
            Pageable pageable
    );
}
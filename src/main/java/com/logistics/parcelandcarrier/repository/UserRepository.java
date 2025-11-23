package com.logistics.parcelandcarrier.repository;

import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.enums.Role;
import com.logistics.parcelandcarrier.enums.Specialty;
import com.logistics.parcelandcarrier.enums.TransporterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations
 * Provides CRUD operations and custom queries for User management
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by login (unique)
     * @param login the user login
     * @return Optional containing the user if found
     */
    Optional<User> findByLogin(String login);

    /**
     * Check if a user exists with the given login
     * @param login the login to check
     * @return true if exists, false otherwise
     */
    boolean existsByLogin(String login);

    /**
     * Find users by role with pagination
     * @param role the role to filter
     * @param pageable pagination information
     * @return Page of users
     */
    Page<User> findByRole(Role role, Pageable pageable);

    /**
     * Find all users by role (without pagination)
     * @param role the role to filter
     * @return List of users
     */
    List<User> findByRole(Role role);

    /**
     * Find users by role and specialty (for transporters)
     * @param role the role (should be TRANSPORTER)
     * @param specialty the specialty to filter
     * @param pageable pagination information
     * @return Page of transporters
     */
    Page<User> findByRoleAndSpecialty(Role role, Specialty specialty, Pageable pageable);

    /**
     * Find users by role and status (for transporters)
     * @param role the role (should be TRANSPORTER)
     * @param status the transporter status
     * @param pageable pagination information
     * @return Page of transporters
     */
    Page<User> findByRoleAndStatus(Role role, TransporterStatus status, Pageable pageable);

    /**
     * Find active users by role
     * @param role the role to filter
     * @param active the active status
     * @param pageable pagination information
     * @return Page of users
     */
    Page<User> findByRoleAndActive(Role role, boolean active, Pageable pageable);

    /**
     * Find available transporters with specific specialty
     * @param role should be TRANSPORTER
     * @param status should be AVAILABLE
     * @param specialty the required specialty
     * @return List of available transporters
     */
    List<User> findByRoleAndStatusAndSpecialty(
            Role role,
            TransporterStatus status,
            Specialty specialty
    );

    /**
     * Count transporters by status
     * @param role should be TRANSPORTER
     * @param status the status to count
     * @return number of transporters
     */
    long countByRoleAndStatus(Role role, TransporterStatus status);

    /**
     * Find all active transporters
     * @param role should be TRANSPORTER
     * @param active should be true
     * @return List of active transporters
     */
    List<User> findByRoleAndActive(Role role, boolean active);

    /**
     * Custom query: Find available transporters that can handle a specific package type
     * This is a convenience method that combines multiple filters
     */
    @Query("{ 'role': ?0, 'status': ?1, 'specialty': ?2, 'active': true }")
    List<User> findAvailableTransportersForPackageType(
            Role role,
            TransporterStatus status,
            Specialty specialty
    );
}
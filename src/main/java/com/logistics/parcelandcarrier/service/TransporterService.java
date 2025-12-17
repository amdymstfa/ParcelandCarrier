package com.logistics.parcelandcarrier.service;

import com.logistics.parcelandcarrier.dto.request.TransporterRequest;
import com.logistics.parcelandcarrier.dto.response.UserResponse;
import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.enums.Role;
import com.logistics.parcelandcarrier.enums.Specialty;
import com.logistics.parcelandcarrier.exception.ResourceNotFoundException;
import com.logistics.parcelandcarrier.mapper.UserMapper;
import com.logistics.parcelandcarrier.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for transporter and user management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransporterService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  /**
   * Find all users (Admin and Transporters)
   * @return list of users
   */
  public List<UserResponse> findAllUsers() {
    log.debug("Finding all users");

    List<User> users = userRepository.findAll();

    return userMapper.toResponseList(users);
  }

  /**
   * Find transporters with filters and pagination
   * @param specialty specialty filter (optional)
   * @param pageable pagination info
   * @return page of transporters
   */
  public Page<UserResponse> findTransportersWithFilters(
    Specialty specialty,
    Pageable pageable
  ) {
    log.debug("Finding transporters with specialty filter: {}", specialty);

    Page<User> transporters;

    if (specialty != null) {
      transporters = userRepository.findByRoleAndSpecialty(
        Role.TRANSPORTER, specialty, pageable
      );
    } else {
      transporters = userRepository.findByRole(Role.TRANSPORTER, pageable);
    }

    return userMapper.toResponsePage(transporters);
  }

  /**
   * Create a new transporter
   * @param request transporter creation request
   * @return created transporter
   */
  @Transactional
  public UserResponse createTransporter(TransporterRequest request) {
    log.info("Creating new transporter: {}", request.getLogin());

    // Verify login is unique
    verifyUniqueLogin(request.getLogin());

    // Hash password
    String hashedPassword = hashPassword(request.getPassword());

    // Convert DTO to entity
    User transporter = userMapper.toEntity(request, hashedPassword);

    // Save transporter
    User savedTransporter = userRepository.save(transporter);

    log.info("Transporter created successfully: {}", savedTransporter.getId());

    return userMapper.toResponse(savedTransporter);
  }

  /**
   * Update an existing transporter
   * @param id transporter ID
   * @param request update request
   * @return updated transporter
   */
  @Transactional
  public UserResponse updateTransporter(String id, TransporterRequest request) {
    log.info("Updating transporter: {}", id);

    // Find existing transporter
    User transporter = verifyTransporterExists(id);

    // Verify new login is unique (if changed)
    if (!transporter.getLogin().equals(request.getLogin())) {
      verifyUniqueLogin(request.getLogin());
    }

    // Hash password if provided
    String hashedPassword = null;
    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
      hashedPassword = hashPassword(request.getPassword());
    }

    // Update entity from DTO
    userMapper.updateEntityFromDto(request, hashedPassword, transporter);

    // Save updated transporter
    User updatedTransporter = userRepository.save(transporter);

    log.info("Transporter updated successfully: {}", id);

    return userMapper.toResponse(updatedTransporter);
  }

  /**
   * Deactivate a transporter (soft delete)
   * @param id transporter ID
   */
  @Transactional
  public void deactivateTransporter(String id) {
    log.info("Deactivating transporter: {}", id);

    User transporter = verifyTransporterExists(id);

    // Deactivate user
    transporter.deactivate();

    userRepository.save(transporter);

    log.info("Transporter deactivated successfully: {}", id);
  }

  /**
   * Activate a user account
   * @param id user ID
   * @return activated user
   */
  @Transactional
  public UserResponse activateUser(String id) {
    log.info("Activating user: {}", id);

    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    // Activate user
    user.activate();

    User activatedUser = userRepository.save(user);

    log.info("User activated successfully: {}", id);

    return userMapper.toResponse(activatedUser);
  }

  // =========================================================================
  // Private helper methods
  // =========================================================================

  /**
   * Verify that login is unique
   */
  public void verifyUniqueLogin(String login) {
    if (userRepository.existsByLogin(login)) {
      throw new IllegalArgumentException("Login already exists: " + login);
    }
  }

  /**
   * Hash password using BCrypt
   */
  private String hashPassword(String password) {
    return passwordEncoder.encode(password);
  }

  /**
   * Verify transporter exists and is a transporter
   */
  private User verifyTransporterExists(String id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

    if (!user.isTransporter()) {
      throw new IllegalArgumentException("User is not a transporter");
    }

    return user;
  }
}

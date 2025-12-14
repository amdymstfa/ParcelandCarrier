package com.logistics.parcelandcarrier.mapper;

import com.logistics.parcelandcarrier.dto.request.TransporterRequest;
import com.logistics.parcelandcarrier.dto.response.UserResponse;
import com.logistics.parcelandcarrier.entity.User;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

/**
 * MapStruct mapper for User entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  /**
   * Convert TransporterRequest to User entity
   * Used for creating new transporters
   * @param request the transporter request DTO
   * @param hashedPassword the pre-hashed password
   * @return User entity configured as TRANSPORTER
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", source = "hashedPassword")
  @Mapping(target = "role", constant = "TRANSPORTER")
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "status", constant = "AVAILABLE")
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  User toEntity(TransporterRequest request, String hashedPassword);

  /**
   * Convert User entity to UserResponse
   * Excludes password for security
   */
  UserResponse toResponse(User entity);

  /**
   * Update existing User entity from TransporterRequest
   * Preserves: id, role, active, status, createdAt
   * @param request the transporter request DTO
   * @param hashedPassword the pre-hashed password (can be null to keep existing)
   * @param entity the entity to update
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "password", source = "hashedPassword")
  @Mapping(target = "role", ignore = true)
  @Mapping(target = "active", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  void updateEntityFromDto(TransporterRequest request, String hashedPassword, @MappingTarget User entity);

  /**
   * Create Admin user
   * @param login admin login
   * @param hashedPassword pre-hashed password
   * @return User entity configured as ADMIN
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "role", constant = "ADMIN")
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "specialty", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
  @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
  User createAdmin(String login, String hashedPassword);

  /**
   * Convert Page of User to Page of UserResponse
   */
  default Page<UserResponse> toResponsePage(Page<User> page) {
    return page.map(this::toResponse);
  }
}

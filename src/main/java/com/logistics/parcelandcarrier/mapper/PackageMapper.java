package com.logistics.parcelandcarrier.mapper;

import com.logistics.parcelandcarrier.dto.request.PackageRequest;
import com.logistics.parcelandcarrier.dto.response.PackageResponse;
import com.logistics.parcelandcarrier.entity.Package;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

/**
 * MapStruct mapper for Package entity and DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PackageMapper {

    /**
     * Convert PackageRequest to Package entity
     * Used for creating new packages
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "transporterId", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Package toEntity(PackageRequest request);

    /**
     * Convert Package entity to PackageResponse
     * Note: transporterLogin must be set manually in service layer
     */
    @Mapping(target = "transporterLogin", ignore = true)
    PackageResponse toResponse(Package entity);

    /**
     * Update existing Package entity from PackageRequest
     * Preserves: id, status, transporterId, createdAt
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "transporterId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromDto(PackageRequest request, @MappingTarget Package entity);

    /**
     * Convert Page of Package to Page of PackageResponse
     */
    default Page<PackageResponse> toResponsePage(Page<Package> page) {
        return page.map(this::toResponse);
    }
}

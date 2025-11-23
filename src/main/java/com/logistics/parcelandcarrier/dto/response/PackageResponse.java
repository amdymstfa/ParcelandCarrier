package com.logistics.parcelandcarrier.dto.response;

import com.logistics.parcelandcarrier.enums.PackageStatus;
import com.logistics.parcelandcarrier.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for package response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {

    private String id;
    private PackageType type;
    private double weight;
    private String destinationAddress;
    private PackageStatus status;
    private String transporterId;
    private String transporterLogin;
    private String handlingInstructions;
    private Double minTemperature;
    private Double maxTemperature;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

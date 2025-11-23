package com.logistics.parcelandcarrier.dto.request;

import com.logistics.parcelandcarrier.enums.PackageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating package status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private PackageStatus status;
}
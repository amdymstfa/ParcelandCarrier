package com.logistics.parcelandcarrier.entity;

import com.logistics.parcelandcarrier.enums.PackageStatus;
import com.logistics.parcelandcarrier.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "packages")
public class Package {

    @Id
    private String id;

    @Field("type")
    private PackageType type;

    @Field("weight")
    private double weight;

    @Indexed
    @Field("destination_address")
    private String destinationAddress;

    @Indexed
    @Field("status")
    private PackageStatus status;

    @Indexed
    @Field("transporter_id")
    private String transporterId;

    // For FRAGILE packages
    @Field("handling_instructions")
    private String handlingInstructions;

    // For REFRIGERATED packages
    @Field("min_temperature")
    private Double minTemperature;

    @Field("max_temperature")
    private Double maxTemperature;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // =========================================================================
    // Business Logic Methods
    // =========================================================================

    /**
     * Check if package is assigned to a transporter
     */
    public boolean isAssigned() {
        return transporterId != null && !transporterId.isEmpty();
    }

    /**
     * Check if package is delivered
     */
    public boolean isDelivered() {
        return status != null && status.isDelivered();
    }

    /**
     * Check if package is cancelled
     */
    public boolean isCancelled() {
        return status != null && status.isCancelled();
    }

    /**
     * Check if package is pending
     */
    public boolean isPending() {
        return status != null && status.isPending();
    }

    /**
     * Check if package is in transit
     */
    public boolean isInTransit() {
        return status != null && status.isInTransit();
    }

    /**
     * Check if package delivery is finished (delivered or cancelled)
     */
    public boolean isFinished() {
        return status != null && status.isFinished();
    }

    /**
     * Check if package can be assigned to a transporter
     */
    public boolean canBeAssigned() {
        return status != null && status.canAssign();
    }

    /**
     * Assign package to a transporter
     */
    public void assignToTransporter(String transporterId) {
        if (!canBeAssigned()) {
            throw new IllegalStateException(
                    "Package cannot be assigned in status: " + status
            );
        }
        this.transporterId = transporterId;
        this.status = PackageStatus.IN_TRANSIT;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Change package status
     */
    public void changeStatus(PackageStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancel package delivery
     */
    public void cancel() {
        this.status = PackageStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark package as delivered
     */
    public void markAsDelivered() {
        this.status = PackageStatus.DELIVERED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Validate temperature range for refrigerated packages
     */
    public boolean validateTemperature() {
        if (!type.isRefrigerated()) {
            return true;
        }

        if (minTemperature == null || maxTemperature == null) {
            return false;
        }

        return minTemperature < maxTemperature &&
                minTemperature >= -30.0 &&
                maxTemperature <= 30.0;
    }

    /**
     * Check if package requires special handling
     */
    public boolean requiresSpecialHandling() {
        return type != null && type.requiresSpecialHandling();
    }

    /**
     * Check if package is fragile
     */
    public boolean isFragile() {
        return type != null && type.isFragile();
    }

    /**
     * Check if package is refrigerated
     */
    public boolean isRefrigerated() {
        return type != null && type.isRefrigerated();
    }

    /**
     * Check if package is standard
     */
    public boolean isStandard() {
        return type != null && type.isStandard();
    }

    /**
     * Validate handling instructions for fragile packages
     */
    public boolean hasValidHandlingInstructions() {
        if (!isFragile()) {
            return true;
        }
        return handlingInstructions != null && !handlingInstructions.trim().isEmpty();
    }
}
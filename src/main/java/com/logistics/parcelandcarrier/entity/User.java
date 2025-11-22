package com.logistics.parcelandcarrier.entity;

import com.logistics.parcelandcarrier.enums.Role;
import com.logistics.parcelandcarrier.enums.Specialty;
import com.logistics.parcelandcarrier.enums.TransporterStatus;
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
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    @Field("login")
    private String login;

    @Field("password")
    private String password;

    @Field("role")
    private Role role;

    @Field("active")
    private boolean active;

    // Fields for TRANSPORTER only
    @Field("specialty")
    private Specialty specialty;

    @Field("status")
    private TransporterStatus status;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // =========================================================================
    // Business Logic Methods
    // =========================================================================

    /**
     * Check if user is a transporter
     */
    public boolean isTransporter() {
        return role != null && role.isTransporter();
    }

    /**
     * Check if user is an admin
     */
    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

    /**
     * Check if transporter is available for delivery
     */
    public boolean isAvailable() {
        return isTransporter() &&
                status != null &&
                status.isAvailable();
    }

    /**
     * Check if transporter is on delivery
     */
    public boolean isOnDelivery() {
        return isTransporter() &&
                status != null &&
                status.isOnDelivery();
    }

    /**
     * Check if transporter can handle a specific package type
     */
    public boolean canHandlePackage(PackageType packageType) {
        if (!isTransporter() || specialty == null) {
            return false;
        }
        return specialty.matchesPackageType(packageType);
    }

    /**
     * Activate user account
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivate user account
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set transporter status to ON_DELIVERY
     */
    public void setOnDelivery() {
        if (isTransporter()) {
            this.status = TransporterStatus.ON_DELIVERY;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Set transporter status to AVAILABLE
     */
    public void setAvailable() {
        if (isTransporter()) {
            this.status = TransporterStatus.AVAILABLE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Check if transporter can take a new package
     */
    public boolean canTakeNewPackage() {
        return isTransporter() &&
                active &&
                status != null &&
                status.canTransport();
    }
}


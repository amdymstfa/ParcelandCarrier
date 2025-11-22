package com.logistics.parcelandcarrier.entity;

import com.logistics.parcelandcarrier.enums.PackageStatus;
import com.logistics.parcelandcarrier.enums.PackageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PackageTest {

    private Package standardPackage;
    private Package fragilePackage;
    private Package refrigeratedPackage;

    @BeforeEach
    void setUp() {
        standardPackage = Package.builder()
                .id("1")
                .type(PackageType.STANDARD)
                .weight(5.5)
                .destinationAddress("123 Main St, Paris")
                .status(PackageStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        fragilePackage = Package.builder()
                .id("2")
                .type(PackageType.FRAGILE)
                .weight(2.3)
                .destinationAddress("456 Avenue, Lyon")
                .status(PackageStatus.PENDING)
                .handlingInstructions("Handle with care")
                .createdAt(LocalDateTime.now())
                .build();

        refrigeratedPackage = Package.builder()
                .id("3")
                .type(PackageType.REFRIGERATED)
                .weight(10.0)
                .destinationAddress("789 Boulevard, Marseille")
                .status(PackageStatus.PENDING)
                .minTemperature(2.0)
                .maxTemperature(8.0)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should check if package is assigned")
    void testIsAssigned() {
        assertFalse(standardPackage.isAssigned());

        standardPackage.setTransporterId("transporter123");
        assertTrue(standardPackage.isAssigned());
    }

    @Test
    @DisplayName("Should check package status correctly")
    void testPackageStatus() {
        assertTrue(standardPackage.isPending());
        assertFalse(standardPackage.isInTransit());
        assertFalse(standardPackage.isDelivered());
        assertFalse(standardPackage.isCancelled());
        assertFalse(standardPackage.isFinished());
    }

    @Test
    @DisplayName("Should determine if package can be assigned")
    void testCanBeAssigned() {
        assertTrue(standardPackage.canBeAssigned());

        standardPackage.setStatus(PackageStatus.IN_TRANSIT);
        assertFalse(standardPackage.canBeAssigned());

        standardPackage.setStatus(PackageStatus.DELIVERED);
        assertFalse(standardPackage.canBeAssigned());
    }

    @Test
    @DisplayName("Should assign package to transporter")
    void testAssignToTransporter() {
        String transporterId = "trans123";

        standardPackage.assignToTransporter(transporterId);

        assertEquals(transporterId, standardPackage.getTransporterId());
        assertEquals(PackageStatus.IN_TRANSIT, standardPackage.getStatus());
        assertNotNull(standardPackage.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception when assigning non-pending package")
    void testAssignToTransporterInvalidStatus() {
        standardPackage.setStatus(PackageStatus.DELIVERED);

        assertThrows(IllegalStateException.class, () -> {
            standardPackage.assignToTransporter("trans123");
        });
    }

    @Test
    @DisplayName("Should change package status")
    void testChangeStatus() {
        standardPackage.changeStatus(PackageStatus.IN_TRANSIT);
        assertEquals(PackageStatus.IN_TRANSIT, standardPackage.getStatus());
        assertNotNull(standardPackage.getUpdatedAt());
    }

    @Test
    @DisplayName("Should throw exception for null status")
    void testChangeStatusNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            standardPackage.changeStatus(null);
        });
    }

    @Test
    @DisplayName("Should cancel package")
    void testCancel() {
        standardPackage.cancel();

        assertEquals(PackageStatus.CANCELLED, standardPackage.getStatus());
        assertTrue(standardPackage.isCancelled());
        assertTrue(standardPackage.isFinished());
        assertNotNull(standardPackage.getUpdatedAt());
    }

    @Test
    @DisplayName("Should mark package as delivered")
    void testMarkAsDelivered() {
        standardPackage.markAsDelivered();

        assertEquals(PackageStatus.DELIVERED, standardPackage.getStatus());
        assertTrue(standardPackage.isDelivered());
        assertTrue(standardPackage.isFinished());
        assertNotNull(standardPackage.getUpdatedAt());
    }

    @Test
    @DisplayName("Should validate temperature for refrigerated packages")
    void testValidateTemperature() {
        // Standard package - always valid
        assertTrue(standardPackage.validateTemperature());

        // Refrigerated package with valid temperature
        assertTrue(refrigeratedPackage.validateTemperature());

        // Invalid temperature range (min >= max)
        refrigeratedPackage.setMinTemperature(10.0);
        refrigeratedPackage.setMaxTemperature(5.0);
        assertFalse(refrigeratedPackage.validateTemperature());

        // Missing temperature values
        refrigeratedPackage.setMinTemperature(null);
        assertFalse(refrigeratedPackage.validateTemperature());

        // Temperature out of acceptable range
        refrigeratedPackage.setMinTemperature(-40.0);
        refrigeratedPackage.setMaxTemperature(-35.0);
        assertFalse(refrigeratedPackage.validateTemperature());
    }

    @Test
    @DisplayName("Should check if package requires special handling")
    void testRequiresSpecialHandling() {
        assertFalse(standardPackage.requiresSpecialHandling());
        assertTrue(fragilePackage.requiresSpecialHandling());
        assertTrue(refrigeratedPackage.requiresSpecialHandling());
    }

    @Test
    @DisplayName("Should identify package types correctly")
    void testPackageTypes() {
        assertTrue(standardPackage.isStandard());
        assertFalse(standardPackage.isFragile());
        assertFalse(standardPackage.isRefrigerated());

        assertFalse(fragilePackage.isStandard());
        assertTrue(fragilePackage.isFragile());
        assertFalse(fragilePackage.isRefrigerated());

        assertFalse(refrigeratedPackage.isStandard());
        assertFalse(refrigeratedPackage.isFragile());
        assertTrue(refrigeratedPackage.isRefrigerated());
    }

    @Test
    @DisplayName("Should validate handling instructions for fragile packages")
    void testHasValidHandlingInstructions() {
        // Standard package - always valid
        assertTrue(standardPackage.hasValidHandlingInstructions());

        // Fragile package with instructions
        assertTrue(fragilePackage.hasValidHandlingInstructions());

        // Fragile package without instructions
        fragilePackage.setHandlingInstructions(null);
        assertFalse(fragilePackage.hasValidHandlingInstructions());

        fragilePackage.setHandlingInstructions("   ");
        assertFalse(fragilePackage.hasValidHandlingInstructions());
    }

    @Test
    @DisplayName("Should verify finished status")
    void testIsFinished() {
        assertFalse(standardPackage.isFinished());

        standardPackage.setStatus(PackageStatus.DELIVERED);
        assertTrue(standardPackage.isFinished());

        standardPackage.setStatus(PackageStatus.CANCELLED);
        assertTrue(standardPackage.isFinished());

        standardPackage.setStatus(PackageStatus.IN_TRANSIT);
        assertFalse(standardPackage.isFinished());
    }
}
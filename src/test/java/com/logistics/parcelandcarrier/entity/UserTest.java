package com.logistics.parcelandcarrier.entity;

import com.logistics.parcelandcarrier.enums.PackageType;
import com.logistics.parcelandcarrier.enums.Role;
import com.logistics.parcelandcarrier.enums.Specialty;
import com.logistics.parcelandcarrier.enums.TransporterStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User admin;
    private User transporter;

    @BeforeEach
    void setUp() {
        admin = User.builder()
                .id("1")
                .login("admin")
                .password("hashedPassword")
                .role(Role.ADMIN)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        transporter = User.builder()
                .id("2")
                .login("transporter1")
                .password("hashedPassword")
                .role(Role.TRANSPORTER)
                .active(true)
                .specialty(Specialty.STANDARD)
                .status(TransporterStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should identify admin role correctly")
    void testIsAdmin() {
        assertTrue(admin.isAdmin());
        assertFalse(transporter.isAdmin());
    }

    @Test
    @DisplayName("Should identify transporter role correctly")
    void testIsTransporter() {
        assertFalse(admin.isTransporter());
        assertTrue(transporter.isTransporter());
    }

    @Test
    @DisplayName("Should check transporter availability correctly")
    void testIsAvailable() {
        assertTrue(transporter.isAvailable());

        transporter.setStatus(TransporterStatus.ON_DELIVERY);
        assertFalse(transporter.isAvailable());

        // Admin has no status
        assertFalse(admin.isAvailable());
    }

    @Test
    @DisplayName("Should check transporter on delivery status")
    void testIsOnDelivery() {
        assertFalse(transporter.isOnDelivery());

        transporter.setStatus(TransporterStatus.ON_DELIVERY);
        assertTrue(transporter.isOnDelivery());
    }

    @Test
    @DisplayName("Should verify package handling capability")
    void testCanHandlePackage() {
        // Transporter with STANDARD specialty
        assertTrue(transporter.canHandlePackage(PackageType.STANDARD));
        assertFalse(transporter.canHandlePackage(PackageType.FRAGILE));
        assertFalse(transporter.canHandlePackage(PackageType.REFRIGERATED));

        // Transporter with FRAGILE specialty
        transporter.setSpecialty(Specialty.FRAGILE);
        assertFalse(transporter.canHandlePackage(PackageType.STANDARD));
        assertTrue(transporter.canHandlePackage(PackageType.FRAGILE));
        assertFalse(transporter.canHandlePackage(PackageType.REFRIGERATED));

        // Admin cannot handle packages
        assertFalse(admin.canHandlePackage(PackageType.STANDARD));
    }

    @Test
    @DisplayName("Should activate user account")
    void testActivate() {
        User inactiveUser = User.builder()
                .login("inactive")
                .role(Role.TRANSPORTER)
                .active(false)
                .build();

        assertFalse(inactiveUser.isActive());
        inactiveUser.activate();
        assertTrue(inactiveUser.isActive());
        assertNotNull(inactiveUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should deactivate user account")
    void testDeactivate() {
        assertTrue(transporter.isActive());
        transporter.deactivate();
        assertFalse(transporter.isActive());
        assertNotNull(transporter.getUpdatedAt());
    }

    @Test
    @DisplayName("Should set transporter to ON_DELIVERY status")
    void testSetOnDelivery() {
        assertEquals(TransporterStatus.AVAILABLE, transporter.getStatus());
        transporter.setOnDelivery();
        assertEquals(TransporterStatus.ON_DELIVERY, transporter.getStatus());
        assertNotNull(transporter.getUpdatedAt());

        // Admin status should not change
        admin.setOnDelivery();
        assertNull(admin.getStatus());
    }

    @Test
    @DisplayName("Should set transporter to AVAILABLE status")
    void testSetAvailable() {
        transporter.setStatus(TransporterStatus.ON_DELIVERY);
        transporter.setAvailable();
        assertEquals(TransporterStatus.AVAILABLE, transporter.getStatus());
        assertNotNull(transporter.getUpdatedAt());
    }

    @Test
    @DisplayName("Should check if transporter can take new package")
    void testCanTakeNewPackage() {
        assertTrue(transporter.canTakeNewPackage());

        // Transporter on delivery cannot take new package
        transporter.setStatus(TransporterStatus.ON_DELIVERY);
        assertFalse(transporter.canTakeNewPackage());

        // Inactive transporter cannot take package
        transporter.setStatus(TransporterStatus.AVAILABLE);
        transporter.setActive(false);
        assertFalse(transporter.canTakeNewPackage());

        // Admin cannot take packages
        assertFalse(admin.canTakeNewPackage());
    }
}   
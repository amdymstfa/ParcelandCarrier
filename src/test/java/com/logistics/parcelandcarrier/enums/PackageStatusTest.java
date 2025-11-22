package com.logistics.parcelandcarrier.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PackageStatusTest {

    @Test
    void testIsPending(){
        assertTrue(PackageStatus.PENDING.isPending());
        assertFalse(PackageStatus.IN_TRANSIT.isPending());
        assertFalse(PackageStatus.DELIVERED.isPending());
        assertFalse(PackageStatus.CANCELLED.isPending());
    }

    @Test
    void testIsInTransit(){
        assertTrue(PackageStatus.IN_TRANSIT.isInTransit());
        assertFalse(PackageStatus.DELIVERED.isInTransit());
        assertFalse(PackageStatus.PENDING.isInTransit());
        assertFalse(PackageStatus.CANCELLED.isInTransit());
    }


    @Test
    void testIsDelivered(){
        assertFalse(PackageStatus.IN_TRANSIT.isDelivered());
        assertTrue(PackageStatus.DELIVERED.isDelivered());
        assertFalse(PackageStatus.PENDING.isDelivered());
        assertFalse(PackageStatus.CANCELLED.isDelivered());
    }

    @Test
    void testIsCancelled(){
        assertFalse(PackageStatus.IN_TRANSIT.isCancelled());
        assertFalse(PackageStatus.DELIVERED.isCancelled());
        assertFalse(PackageStatus.PENDING.isCancelled());
        assertTrue(PackageStatus.CANCELLED.isCancelled());
    }
}

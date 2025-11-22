package com.logistics.parcelandcarrier.enums;

public enum StatutTransporteur {

    AVAILABLE,
    ON_DELIVERY;

    // Granular checks
    public boolean isAvailable() { return this == AVAILABLE; }
    public boolean isOnDelivery() { return this == ON_DELIVERY; }

    // Business rule: transporter can take a new package only when available
    public boolean canTransport() { return isAvailable(); }
}

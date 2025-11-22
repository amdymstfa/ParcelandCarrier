package com.logistics.parcelandcarrier.enums;

public enum Specialty {
    STANDARD, FRAGILE, REFRIGERATED;

    public boolean matchesPackageType(PackageType packageType) {
        return switch (this) {
            case STANDARD -> packageType.isStandard();
            case FRAGILE -> packageType.isFragile();
            case REFRIGERATED -> packageType.isRefrigerated();
        };
    }
}

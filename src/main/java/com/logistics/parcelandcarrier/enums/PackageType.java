package com.logistics.parcelandcarrier.enums ;

public enum PackageType {
    STANDARD ,FRAGILE ,REFRIGERATED ;

    // special trainmen
    public boolean requiresSpecialHandling(){return this == FRAGILE || this == REFRIGERATED ;}

    // matching package and transporter
    public boolean matchesTransporterSpecialty(Specialty specialty) {
        return switch (this) {
            case STANDARD -> specialty == Specialty.STANDARD;
            case FRAGILE -> specialty == Specialty.FRAGILE;
            case REFRIGERATED -> specialty == Specialty.REFRIGERATED;
        };
    }


    public boolean isStandard(){return this == STANDARD ;}
    public boolean isFragile(){return this == FRAGILE ;}
    public boolean isRefrigerated(){return this == REFRIGERATED ;}
}
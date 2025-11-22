package com.logistics.parcelandcarrier.enums ;

public enum PackageStatus {
    PENDING ,IN_TRANSIT ,DELIVERED, CANCELLED;

    // business logic
    public boolean isPending(){return this == PENDING ;}
    public boolean isInTransit(){return this == IN_TRANSIT ;}
    public boolean isDelivered(){return this == DELIVERED ;}
    public boolean isCancelled(){return this == CANCELLED ;}

    // define when a delivery is finished
    public boolean isFinished(){return isDelivered() || isCancelled() ;}

    // define when can make assignment to transporter

    public boolean canAssign(){return isPending() ;}
}
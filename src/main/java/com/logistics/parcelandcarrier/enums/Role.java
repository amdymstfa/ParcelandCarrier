package com.logistics.parcelandcarrier.enums ;

public enum Role {

    ADMIN, TRANSPORTER ;

    // admin role checker
    public boolean isAdmin(){ return this == ADMIN ;}

    // transporter role checker
    public boolean isTransporter(){return this == TRANSPORTER ;}

    // create an transporter
    public boolean createTransporter(){return isAdmin();}

    // assign a package
    public boolean canAssignPackage() {return isAdmin();}

    // update package status
    public boolean canUpdateStatusPackage() {return true ;}

}
package com.grabid.models;

/**
 * Created by vinod on 11/8/2016.
 */
public class VehicleInfo {


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String createdBy;
    String companyId;
    String updatedBy;

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    String registrationNumber;
    String id;

}

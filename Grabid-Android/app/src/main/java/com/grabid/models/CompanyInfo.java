package com.grabid.models;

/**
 * Created by vinod on 11/8/2016.
 */
public class CompanyInfo {
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String officeNumber;

    public String getCompany_logo() {
        return company_logo;
    }

    public void setCompany_logo(String company_logo) {
        this.company_logo = company_logo;
    }

    String company_logo;

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    String abnNumber;
    String contactPerson;
    String vehicleInfleet;
    String postalCode;

    public String getVehiclesInfo() {
        return vehiclesInfo;
    }

    public void setVehiclesInfo(String vehiclesInfo) {
        this.vehiclesInfo = vehiclesInfo;
    }

    String vehiclesInfo;

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }

    public String getAbnNumber() {
        return abnNumber;
    }

    public void setAbnNumber(String abnNumber) {
        this.abnNumber = abnNumber;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getVehicleInfleet() {
        return vehicleInfleet;
    }

    public void setVehicleInfleet(String vehicleInfleet) {
        this.vehicleInfleet = vehicleInfleet;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getVehicleNnsurance() {
        return vehicleNnsurance;
    }

    public void setVehicleNnsurance(String vehicleNnsurance) {
        this.vehicleNnsurance = vehicleNnsurance;
    }

    public String getFreightInsurancePolicy() {
        return freightInsurancePolicy;
    }

    public void setFreightInsurancePolicy(String freightInsurancePolicy) {
        this.freightInsurancePolicy = freightInsurancePolicy;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getVehicleQtyType() {
        return vehicleQtyType;
    }

    public void setVehicleQtyType(String vehicleQtyType) {
        this.vehicleQtyType = vehicleQtyType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getCompanyMobile() {
        return companyMobile;
    }

    public void setCompanyMobile(String companyMobile) {
        this.companyMobile = companyMobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String stateName;
    String countryName;
    String vehicleNnsurance;
    String freightInsurancePolicy;

    public String getFreightInsuranceCover() {
        return freightInsuranceCover;
    }

    public void setFreightInsuranceCover(String freightInsuranceCover) {
        this.freightInsuranceCover = freightInsuranceCover;
    }

    String freightInsuranceCover;

    public String getCountryID() {
        return countryID;
    }

    public void setCountryID(String countryID) {
        this.countryID = countryID;
    }

    String countryID;
    String stateId, vehicleQtyType, address, suburb, companyMobile, name;
}

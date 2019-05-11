package com.grabid.models;

/**
 * Created by vinod on 11/8/2016.
 */
public class DriverInfo {
    String id;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getInsurancePolicy() {
        return insurancePolicy;
    }

    public void setInsurancePolicy(String insurancePolicy) {
        this.insurancePolicy = insurancePolicy;
    }

    public String getFreightInsurance() {
        return freightInsurance;
    }

    public void setFreightInsurance(String freightInsurance) {
        this.freightInsurance = freightInsurance;
    }

    public String getFreightInsuranceCover() {
        return freightInsuranceCover;
    }

    public void setFreightInsuranceCover(String freightInsuranceCover) {
        this.freightInsuranceCover = freightInsuranceCover;
    }

    public String getLicenceTypeId() {
        return licenceTypeId;
    }

    public void setLicenceTypeId(String licenceTypeId) {
        this.licenceTypeId = licenceTypeId;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public void setLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public String getLicenceValidTill() {
        return licenceValidTill;
    }

    public void setLicenceValidTill(String licenceValidTill) {
        this.licenceValidTill = licenceValidTill;
    }

    public String getLicenceImage() {
        return licenceImage;
    }

    public void setLicenceImage(String licenceImage) {
        this.licenceImage = licenceImage;
    }

    public String getCanDriveTaxi() {
        return canDriveTaxi;
    }

    public void setCanDriveTaxi(String canDriveTaxi) {
        this.canDriveTaxi = canDriveTaxi;
    }

    public String getTaxiDriverLicence() {
        return taxiDriverLicence;
    }

    public void setTaxiDriverLicence(String taxiDriverLicence) {
        this.taxiDriverLicence = taxiDriverLicence;
    }

    public String getTaxiLicenceImage() {
        return taxiLicenceImage;
    }

    public void setTaxiLicenceImage(String taxiLicenceImage) {
        this.taxiLicenceImage = taxiLicenceImage;
    }

    public String getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    String availabilityStatus;
    String insurancePolicy;
    String freightInsurance;
    String freightInsuranceCover;
    String licenceTypeId;

    public String getMedicareImage() {
        return medicareImage;
    }

    public void setMedicareImage(String medicareImage) {
        this.medicareImage = medicareImage;
    }

    public String getNationalImage() {
        return nationalImage;
    }

    public void setNationalImage(String nationalImage) {
        this.nationalImage = nationalImage;
    }

    String licenceNumber;
    String licenceValidTill;
    String licenceImage;

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    String medicareImage;
    String nationalImage;
    String canDriveTaxi;
    String medicareNumber;

    public String getCertificate_image() {
        return certificate_image;
    }

    public void setCertificate_image(String certificate_image) {
        this.certificate_image = certificate_image;
    }

    public String getCertificate_number() {
        return certificate_number;
    }

    public void setCertificate_number(String certificate_number) {
        this.certificate_number = certificate_number;
    }

    public String getCertificate_valid_till() {
        return certificate_valid_till;
    }

    public void setCertificate_valid_till(String certificate_valid_till) {
        this.certificate_valid_till = certificate_valid_till;
    }

    String taxiDriverLicence;
    String taxiLicenceImage;
    String currentLatitude;
    String currentLongitude;
    String city;
    String certificate_image;
    String certificate_number;
    String certificate_valid_till;
}

package com.grabid.models;

/**
 * Created by vinod on 11/17/2016.
 */
public class HomeData {
    String id;
    String Liftequipement;

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    String dLiftequipement;
    String DeliveryStatus;
    String doBuildType;
    String companyLogo;

    public String getItemtype() {
        return itemtype;
    }

    public void setItemtype(String itemtype) {
        this.itemtype = itemtype;
    }

    public boolean isAsap() {
        return IsAsap;
    }

    public void setAsap(boolean asap) {
        IsAsap = asap;
    }

    public boolean isRoundTrip() {
        return IsRoundTrip;
    }

    public void setRoundTrip(boolean roundTrip) {
        IsRoundTrip = roundTrip;
    }

    String userID;
    String bidStatus;
    String itemtype;
    boolean IsAsap, IsRoundTrip;

    public String getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(String bidStatus) {
        this.bidStatus = bidStatus;
    }

    public String getUserID() {

        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    public String getDoBuildType() {
        return doBuildType;
    }

    public void setDoBuildType(String doBuildType) {
        this.doBuildType = doBuildType;
    }

    public String getPuBuildType() {
        return puBuildType;
    }

    public void setPuBuildType(String puBuildType) {
        this.puBuildType = puBuildType;
    }

    String puBuildType;
    String Job_ID;

    public String getJob_ID() {
        return Job_ID;
    }

    public void setJob_ID(String job_ID) {
        Job_ID = job_ID;
    }

    public String getdLiftequipement() {
        return dLiftequipement;
    }

    public void setdLiftequipement(String dLiftequipement) {
        this.dLiftequipement = dLiftequipement;
    }

    public String getDeliveryStatus() {
        return DeliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        DeliveryStatus = deliveryStatus;
    }

    public String getLiftequipement() {
        return Liftequipement;
    }

    public void setLiftequipement(String liftequipement) {
        Liftequipement = liftequipement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuctionBid() {
        return auctionBid;
    }

    public void setAuctionBid(String auctionBid) {
        this.auctionBid = auctionBid;
    }

    public String getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(String deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    public String getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(String pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public String getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(String pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getItemDeliveryTitle() {
        return itemDeliveryTitle;
    }

    public void setItemDeliveryTitle(String itemDeliveryTitle) {
        this.itemDeliveryTitle = itemDeliveryTitle;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getItemMoreDetails() {
        return itemMoreDetails;
    }

    public void setItemMoreDetails(String itemMoreDetails) {
        this.itemMoreDetails = itemMoreDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGeoZone() {
        return geoZone;
    }

    public void setGeoZone(String geoZone) {
        this.geoZone = geoZone;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDriverDistance() {
        return driverDistance;
    }

    public void setDriverDistance(String driverDistance) {
        this.driverDistance = driverDistance;
    }

    String auctionBid;
    String deliveryTypeId;
    String pickupLatitude;
    String pickupLongitude;
    String itemDeliveryTitle;
    String pickupAddress;
    String itemMoreDetails;
    String status;

    public String getDeliveryStatusType() {
        return deliveryStatusType;
    }

    public void setDeliveryStatusType(String deliveryStatusType) {
        this.deliveryStatusType = deliveryStatusType;
    }

    String deliveryStatusType;
    String geoZone;
    String radius;
    String distance;
    String driverDistance;

    public String getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(String itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    String itemPhoto;

    public String getMaximumOpeningBid() {
        return maximumOpeningBid;
    }

    public void setMaximumOpeningBid(String maximumOpeningBid) {
        this.maximumOpeningBid = maximumOpeningBid;
    }

    public String getFixedOffer() {
        return fixedOffer;
    }

    public void setFixedOffer(String fixedOffer) {
        this.fixedOffer = fixedOffer;
    }

    public String getPickupDay() {
        return pickupDay;
    }

    public void setPickupDay(String pickupDay) {
        this.pickupDay = pickupDay;
    }

    public String getDropoffDay() {
        return dropoffDay;
    }

    public void setDropoffDay(String dropoffDay) {
        this.dropoffDay = dropoffDay;
    }

    String maximumOpeningBid;
    String fixedOffer;
    String pickupDay;
    String dropoffDay;
    String pickUpState;
    String pick_up_city;

    public String getPickUpState() {
        return pickUpState;
    }

    public void setPickUpState(String pickUpState) {
        this.pickUpState = pickUpState;
    }

    public String getPick_up_city() {
        return pick_up_city;
    }

    public void setPick_up_city(String pick_up_city) {
        this.pick_up_city = pick_up_city;
    }
}

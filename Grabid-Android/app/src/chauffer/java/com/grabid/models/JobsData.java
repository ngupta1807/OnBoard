package com.grabid.models;

import java.io.Serializable;

/**
 * Created by graycell on 15/11/17.
 */


/**
 * Created by vinod on 10/26/2016.
 */
public class JobsData implements Serializable {
    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    boolean isBookmarked;

    public boolean hasShipperFeedback() {
        return hasShipperFeedback;
    }

    public void setHasShipperFeedback(boolean hasShipperFeedback) {
        this.hasShipperFeedback = hasShipperFeedback;
    }

    public boolean hasDriverFeedback() {
        return hasDriverFeedback;
    }

    public void setHasDriverFeedback(boolean hasDriverFeedback) {
        this.hasDriverFeedback = hasDriverFeedback;
    }

    boolean hasShipperFeedback;
    boolean hasDriverFeedback;

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public String getObjData() {
        return objData;
    }

    public void setObjData(String objData) {
        this.objData = objData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getBidID() {
        return bidID;
    }

    public void setBidID(String bidID) {
        this.bidID = bidID;
    }

    public String getItemPhoto() {
        return itemPhoto;
    }

    public void setItemPhoto(String itemPhoto) {
        this.itemPhoto = itemPhoto;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public String getDropoffAdress() {
        return dropoffAdress;
    }

    public void setDropoffAdress(String dropoffAdress) {
        this.dropoffAdress = dropoffAdress;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDropOffDate() {
        return dropOffDate;
    }

    public void setDropOffDate(String dropOffDate) {
        this.dropOffDate = dropOffDate;
    }

    public String getPickUpDate() {
        return pickUpDate;
    }

    public void setPickUpDate(String pickUpDate) {
        this.pickUpDate = pickUpDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getReceiverSign() {
        return receiverSign;
    }

    public void setReceiverSign(String receiverSign) {
        this.receiverSign = receiverSign;
    }

    public String getAuctionBid() {
        return auctionBid;
    }

    public void setAuctionBid(String auctionBid) {
        this.auctionBid = auctionBid;
    }

    public String getBookmarkID() {
        return bookmarkID;
    }

    public void setBookmarkID(String bookmarkID) {
        this.bookmarkID = bookmarkID;
    }

    public String getItemEquipName() {
        return itemEquipName;
    }

    public void setItemEquipName(String itemEquipName) {
        this.itemEquipName = itemEquipName;
    }

    public String getPuContactPerson() {
        return puContactPerson;
    }

    public void setPuContactPerson(String puContactPerson) {
        this.puContactPerson = puContactPerson;
    }

    public String getPuMobile() {
        return puMobile;
    }

    public void setPuMobile(String puMobile) {
        this.puMobile = puMobile;
    }

    public String getDoContactPerson() {
        return doContactPerson;
    }

    public void setDoContactPerson(String doContactPerson) {
        this.doContactPerson = doContactPerson;
    }

    public String getItemEquipId() {
        return itemEquipId;
    }

    public void setItemEquipId(String itemEquipId) {
        this.itemEquipId = itemEquipId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDoMobile() {
        return doMobile;
    }

    public void setDoMobile(String doMobile) {
        this.doMobile = doMobile;
    }

    public String getAuctionStart() {
        return auctionStart;
    }

    public void setAuctionStart(String auctionStart) {
        this.auctionStart = auctionStart;
    }

    public String getAuctionEnd() {
        return auctionEnd;
    }

    public void setAuctionEnd(String auctionEnd) {
        this.auctionEnd = auctionEnd;
    }

    public String getMaxOpeningBid() {
        return maxOpeningBid;
    }

    public void setMaxOpeningBid(String maxOpeningBid) {
        this.maxOpeningBid = maxOpeningBid;
    }

    public String getFixedOffer() {
        return fixedOffer;
    }

    public void setFixedOffer(String fixedOffer) {
        this.fixedOffer = fixedOffer;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public String getPuBuildType() {
        return puBuildType;
    }

    public void setPuBuildType(String puBuildType) {
        this.puBuildType = puBuildType;
    }

    public String getPuLiftEquipment() {
        return puLiftEquipment;
    }

    public void setPuLiftEquipment(String puLiftEquipment) {
        this.puLiftEquipment = puLiftEquipment;
    }

    public String getPuLat() {
        return puLat;
    }

    public void setPuLat(String puLat) {
        this.puLat = puLat;
    }

    public String getPuLng() {
        return puLng;
    }

    public void setPuLng(String puLng) {
        this.puLng = puLng;
    }

    public String getDoLat() {
        return doLat;
    }

    public void setDoLat(String doLat) {
        this.doLat = doLat;
    }

    public String getDoLng() {
        return doLng;
    }

    public void setDoLng(String doLng) {
        this.doLng = doLng;
    }

    public String getDoBuildType() {
        return doBuildType;
    }

    public void setDoBuildType(String doBuildType) {
        this.doBuildType = doBuildType;
    }

    public String getDoCall() {
        return doCall;
    }

    public void setDoCall(String doCall) {
        this.doCall = doCall;
    }

    public String getDoAppoint() {
        return doAppoint;
    }

    public void setDoAppoint(String doAppoint) {
        this.doAppoint = doAppoint;
    }

    public String getDoLiftEquipment() {
        return doLiftEquipment;
    }

    public void setDoLiftEquipment(String doLiftEquipment) {
        this.doLiftEquipment = doLiftEquipment;
    }

    public String getDeliveryTypeID() {
        return deliveryTypeID;
    }

    public void setDeliveryTypeID(String deliveryTypeID) {
        this.deliveryTypeID = deliveryTypeID;
    }

    public String getDeliveryName() {
        return deliveryName;
    }

    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public String getAnimalQty() {
        return animalQty;
    }

    public void setAnimalQty(String animalQty) {
        this.animalQty = animalQty;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAnimalWeight() {
        return animalWeight;
    }

    public void setAnimalWeight(String animalWeight) {
        this.animalWeight = animalWeight;
    }

    public String getStackable() {
        return stackable;
    }

    public void setStackable(String stackable) {
        this.stackable = stackable;
    }

    public String getHazardous() {
        return hazardous;
    }

    public void setHazardous(String hazardous) {
        this.hazardous = hazardous;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public String getAnimalCarrier() {
        return animalCarrier;
    }

    public void setAnimalCarrier(String animalCarrier) {
        this.animalCarrier = animalCarrier;
    }

    public String getCurrentVaccination() {
        return currentVaccination;
    }

    public void setCurrentVaccination(String currentVaccination) {
        this.currentVaccination = currentVaccination;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    String radius;
    String title;
    String receiver;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderSign() {
        return senderSign;
    }

    public void setSenderSign(String senderSign) {
        this.senderSign = senderSign;
    }

    String sender;

    public String getItemsData() {
        return itemsData;
    }

    public void setItemsData(String itemsData) {
        this.itemsData = itemsData;
    }

    String itemsData;
    String completedAt;
    String pickUpDate;
    String objData;
    String dropOffDate;
    String dropoffAdress;
    String id;
    String userID;
    String deliveryStatus;

    public String getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(String bidStatus) {
        this.bidStatus = bidStatus;
    }

    String bidStatus;
    String driverID;
    String bidID;
    String itemPhoto;
    String pickUpAddress;
    String paymentStatus;
    String paymentAmount;
    String receiverSign, senderSign;
    String auctionBid;
    String bookmarkID;

    public String getRelistNotification() {
        return relistNotification;
    }

    public void setRelistNotification(String relistNotification) {
        this.relistNotification = relistNotification;
    }

    String itemEquipName;
    String puContactPerson;
    String puMobile;
    String doContactPerson;
    String itemEquipId;
    String status;
    String doMobile;
    String auctionStart;
    String auctionEnd;
    String maxOpeningBid;
    String fixedOffer;
    String submit;
    String puBuildType;
    String puLiftEquipment;
    String puLat;
    String puLng;
    String doLat;
    String doLng;
    String relistNotification;

    public String getPuLiftEquipmentText() {
        return puLiftEquipmentText;
    }

    public void setPuLiftEquipmentText(String puLiftEquipmentText) {
        this.puLiftEquipmentText = puLiftEquipmentText;
    }

    public String getPuLiftEquipmentNeededText() {
        return puLiftEquipmentNeededText;
    }

    public void setPuLiftEquipmentNeededText(String puLiftEquipmentNeededText) {
        this.puLiftEquipmentNeededText = puLiftEquipmentNeededText;
    }

    public String getDoLiftEquipmentText() {
        return doLiftEquipmentText;
    }

    public void setDoLiftEquipmentText(String doLiftEquipmentText) {
        this.doLiftEquipmentText = doLiftEquipmentText;
    }

    public String getDoLiftEquipmentNeededText() {
        return doLiftEquipmentNeededText;
    }

    public void setDoLiftEquipmentNeededText(String doLiftEquipmentNeededText) {
        this.doLiftEquipmentNeededText = doLiftEquipmentNeededText;
    }

    String doBuildType;
    String doCall;
    String doAppoint;
    String doLiftEquipment;
    String puLiftEquipmentText;
    String puLiftEquipmentNeededText;
    String doLiftEquipmentText;
    String doLiftEquipmentNeededText;

    public String getSuitableVehicles() {
        return suitableVehicles;
    }

    public void setSuitableVehicles(String suitableVehicles) {
        this.suitableVehicles = suitableVehicles;
    }

    String suitableVehicles;

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }

    String geo;

    public String getDeliveryTypeSubID() {
        return deliveryTypeSubID;
    }

    public void setDeliveryTypeSubID(String deliveryTypeSubID) {
        this.deliveryTypeSubID = deliveryTypeSubID;
    }

    String deliveryTypeSubID;

    public String getDeliveryTypeSubSubID() {
        return deliveryTypeSubSubID;
    }

    public void setDeliveryTypeSubSubID(String deliveryTypeSubSubID) {
        this.deliveryTypeSubSubID = deliveryTypeSubSubID;
    }

    String deliveryTypeSubSubID;

    public String getDeliveryTypeSubSubName() {
        return deliveryTypeSubSubName;
    }

    public void setDeliveryTypeSubSubName(String deliveryTypeSubSubName) {
        this.deliveryTypeSubSubName = deliveryTypeSubSubName;
    }

    String deliveryTypeSubSubName;
    String deliveryTypeID;
    String deliveryName;

    public String getDeliverySubName() {
        return deliverySubName;
    }

    public void setDeliverySubName(String deliverySubName) {
        this.deliverySubName = deliverySubName;
    }

    String deliverySubName;
    String qty;
    String weight;
    String length;
    String width;
    String height;
    String more;
    String animalQty;
    String animalName;
    String breed;
    String animalWeight;
    String stackable;
    String hazardous;
    String animalType;
    String animalCarrier;
    String currentVaccination;

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getShipperRating() {
        return shipperRating;
    }

    public void setShipperRating(String shipperRating) {
        this.shipperRating = shipperRating;
    }

    public String getShipperID() {
        return shipperID;
    }

    public void setShipperID(String shipperID) {
        this.shipperID = shipperID;
    }

    String shipperName;

    public String getShipperImage() {
        return shipperImage;
    }

    public void setShipperImage(String shipperImage) {
        this.shipperImage = shipperImage;
    }

    String shipperImage;
    String shipperRating;
    String shipperID;

    public String getBidArray() {
        return bidArray;
    }

    public void setBidArray(String bidArray) {
        this.bidArray = bidArray;
    }

    String bidArray;

    public String getChoosedBidsArray() {
        return choosedBidsArray;
    }

    public void setChoosedBidsArray(String choosedBidsArray) {
        this.choosedBidsArray = choosedBidsArray;
    }

    String choosedBidsArray;
    String dropoffDateType;
    String pickupDateType;
    String dropOffEndDate;
    String pickupEndDate;
    String pickupinductionRequire;
    String pickupSpecialRestriction;
    String dropoffinductionRequire;
    String dropoffSpecialRestriction;
    String specialPermit;
    String specialPermitDetail;
    String suitabelVehicle;
    String pickupCountry;
    String pickupState;
    String pickupCity;
    String dropoffCountry;
    String dropoffState;
    String dropoffCity;
    String pickUpLiftEquiAvailableIds;
    String dropOffLiftEquiAvailableIds;
    String pickUpLiftEquiNeededIds;
    String dropOffLiftEquiNeededIds;
    String liftequipement;
    String dliftequipement;
    String fromPickUpAt;
    String completed_at;
    boolean isDriver;


    public String getFromPickUpAt() {
        return fromPickUpAt;
    }

    public void setFromPickUpAt(String fromPickUpAt) {
        this.fromPickUpAt = fromPickUpAt;
    }

    public String getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(String completed_at) {
        this.completed_at = completed_at;
    }

    public String getDliftequipement() {
        return dliftequipement;
    }

    public void setDliftequipement(String dliftequipement) {
        this.dliftequipement = dliftequipement;
    }

    public boolean getIsDriver() {
        return isDriver;
    }

    public void setIsDriver(boolean isDriver) {
        this.isDriver = isDriver;
    }

    public String getLiftequipement() {
        return liftequipement;
    }

    public void setLiftequipement(String liftequipement) {
        this.liftequipement = liftequipement;
    }

    public String getPickUpLiftEquiNeededIds() {
        return pickUpLiftEquiNeededIds;
    }

    public void setPickUpLiftEquiNeededIds(String pickUpLiftEquiNeededIds) {
        this.pickUpLiftEquiNeededIds = pickUpLiftEquiNeededIds;
    }

    public String getDropOffLiftEquiNeededIds() {
        return dropOffLiftEquiNeededIds;
    }

    public void setDropOffLiftEquiNeededIds(String dropOffLiftEquiNeededIds) {
        this.dropOffLiftEquiNeededIds = dropOffLiftEquiNeededIds;
    }

    public String getDropOffLiftEquiAvailableIds() {
        return dropOffLiftEquiAvailableIds;
    }

    public void setDropOffLiftEquiAvailableIds(String dropOffLiftEquiAvailableIds) {
        this.dropOffLiftEquiAvailableIds = dropOffLiftEquiAvailableIds;
    }

    public String getPickUpLiftEquiAvailableIds() {
        return pickUpLiftEquiAvailableIds;
    }

    public void setPickUpLiftEquiAvailableIds(String pickUpLiftEquiAvailableIds) {
        this.pickUpLiftEquiAvailableIds = pickUpLiftEquiAvailableIds;
    }

    public String getDropoffDateType() {
        return dropoffDateType;
    }

    public void setDropoffDateType(String dropoffDateType) {
        this.dropoffDateType = dropoffDateType;
    }

    public String getPickupDateType() {
        return pickupDateType;
    }

    public void setPickupDateType(String pickupDateType) {
        this.pickupDateType = pickupDateType;
    }

    public String getPickupEndDate() {
        return pickupEndDate;
    }

    public void setPickupEndDate(String pickupEndDate) {
        this.pickupEndDate = pickupEndDate;
    }

    public String getDropOffEndDate() {
        return dropOffEndDate;
    }

    public void setDropOffEndDate(String dropOffEndDate) {
        this.dropOffEndDate = dropOffEndDate;
    }

    public String getPickupinductionRequire() {
        return pickupinductionRequire;
    }

    public void setPickupinductionRequire(String pickupinductionRequire) {
        this.pickupinductionRequire = pickupinductionRequire;
    }


    public String getPickupSpecialRestriction() {
        return pickupSpecialRestriction;
    }

    public void setPickupSpecialRestriction(String pickupSpecialRestriction) {
        this.pickupSpecialRestriction = pickupSpecialRestriction;
    }

    public String getDropoffinductionRequire() {
        return dropoffinductionRequire;
    }

    public void setDropoffinductionRequire(String dropoffinductionRequire) {
        this.dropoffinductionRequire = dropoffinductionRequire;
    }

    public String getDropoffSpecialRestriction() {
        return dropoffSpecialRestriction;
    }

    public void setDropoffSpecialRestriction(String dropoffSpecialRestriction) {
        this.dropoffSpecialRestriction = dropoffSpecialRestriction;
    }

    public String getSpecialPermit() {
        return specialPermit;
    }

    public void setSpecialPermit(String specialPermit) {
        this.specialPermit = specialPermit;
    }

    public String getSpecialPermitDetail() {
        return specialPermitDetail;
    }

    public String getPickupCity() {
        return pickupCity;
    }

    public void setPickupCity(String pickupCity) {
        this.pickupCity = pickupCity;
    }

    public String getPickupState() {
        return pickupState;
    }

    public void setPickupState(String pickupState) {
        this.pickupState = pickupState;
    }

    public String getPickupCountry() {
        return pickupCountry;
    }

    public void setPickupCountry(String pickupCountry) {
        this.pickupCountry = pickupCountry;
    }

    public String getDropoffCountry() {
        return dropoffCountry;
    }

    public void setDropoffCountry(String dropoffCountry) {
        this.dropoffCountry = dropoffCountry;
    }

    public String getDropoffState() {
        return dropoffState;
    }

    public void setDropoffState(String dropoffState) {
        this.dropoffState = dropoffState;
    }

    public String getDropoffCity() {
        return dropoffCity;
    }

    public void setDropoffCity(String dropoffCity) {
        this.dropoffCity = dropoffCity;
    }

    public void setSpecialPermitDetail(String specialPermitDetail) {
        this.specialPermitDetail = specialPermitDetail;
    }

    public String getSuitabelVehicle() {
        return suitabelVehicle;
    }

    public void setSuitabelVehicle(String suitabelVehicle) {
        this.suitabelVehicle = suitabelVehicle;
    }


}

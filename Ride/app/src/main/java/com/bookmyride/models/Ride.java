package com.bookmyride.models;

import java.io.Serializable;

/**
 * Created by vinod on 2017-01-08.
 */
public class Ride implements Serializable {
    String id;
    String status;
    String pickup;
    String dropoff;
    String doDate;
    String puDate;
    String paymentStatus;
    String statusId;

    public String getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(String discountAmt) {
        this.discountAmt = discountAmt;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    String discountType = "";
    String discountAmt = "";
    String totalBill = "";
    String totalPaid = "";
    String distance = "";
    String duration = "";
    String waitTime = "";
    String tipAmount="$0";
    String puLat;
    String puLng;
    String doLat;
    String doLng;
    String puInfo;
    String doInfo;

    public double getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(double driverRating) {
        this.driverRating = driverRating;
    }

    public double getPassengerRating() {
        return passengerRating;
    }

    public void setPassengerRating(double passengerRating) {
        this.passengerRating = passengerRating;
    }

    double driverRating, passengerRating;

    public String getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo) {
        this.statusInfo = statusInfo;
    }

    String statusInfo;

    public boolean isDiscount() {
        return isDiscount;
    }

    public void setDiscount(boolean discount) {
        isDiscount = discount;
    }

    boolean isDiscount;

    public String getFareInfo() {
        return fareInfo;
    }

    public void setFareInfo(String fareInfo) {
        this.fareInfo = fareInfo;
    }

    String fareInfo = "";

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    String cardNumber = "";

    public String getPaidVia() {
        return paidVia;
    }

    public void setPaidVia(String paidVia) {
        this.paidVia = paidVia;
    }

    String paidVia;

    public String getWaitingCharge() {
        return waitingCharge;
    }

    public void setWaitingCharge(String waitingCharge) {
        this.waitingCharge = waitingCharge;
    }

    String waitingCharge;
    String pPhone;

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpPhone() {
        return pPhone;
    }

    public void setpPhone(String pPhone) {
        this.pPhone = pPhone;
    }

    String pName;

    public String getUsedWalletAmount() {
        return usedWalletAmount;
    }

    public void setUsedWalletAmount(String usedWalletAmount) {
        this.usedWalletAmount = usedWalletAmount;
    }

    String usedWalletAmount = "0";

    public String getDoInfo() {
        return doInfo;
    }

    public void setDoInfo(String doInfo) {
        this.doInfo = doInfo;
    }

    public String getPuInfo() {
        return puInfo;
    }

    public void setPuInfo(String puInfo) {
        this.puInfo = puInfo;
    }

    String doInf, message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDriverCategory() {
        return driverCategory;
    }

    public void setDriverCategory(String driverCategory) {
        this.driverCategory = driverCategory;
    }

    public boolean isDriverFavourited() {
        return isDriverFavourited;
    }

    public void setDriverFavourited(boolean driverFavourited) {
        isDriverFavourited = driverFavourited;
    }

    boolean isDriverFavourited;
    String driverCategory;
    String cardStatus;

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public String getRefunAmount() {
        return refunAmount;
    }

    public void setRefunAmount(String refunAmount) {
        this.refunAmount = refunAmount;
    }

    public String getRefundReason() {
        return refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    String refunAmount, refundReason;

    public String getCardDetail() {
        return cardDetail;
    }

    public void setCardDetail(String cardDetail) {
        this.cardDetail = cardDetail;
    }

    String cardDetail;

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    String bookingType;
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
    public String getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(String tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getDoDate() {
        return doDate;
    }

    public void setDoDate(String doDate) {
        this.doDate = doDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getTotalBill() {
        return totalBill;
    }

    public void setTotalBill(String totalBill) {
        this.totalBill = totalBill;
    }

    public String getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(String totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDropoff() {
        return dropoff;
    }

    public void setDropoff(String dropoff) {
        this.dropoff = dropoff;
    }

    public String getPuDate() {
        return puDate;
    }

    public void setPuDate(String puDate) {
        this.puDate = puDate;
    }

}

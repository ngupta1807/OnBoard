package com.bookmyride.models;

/**
 * Created by vinod on 11/11/2016.
 */
public class PaymentHistory {
    String id;
    String userId;
    public int getPayable() {
        return payable;
    }

    public void setPayable(int payable) {
        this.payable = payable;
    }

    int payable;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayToDriverAmount() {
        return payToDriverAmount;
    }

    public void setPayToDriverAmount(String payToDriverAmount) {
        this.payToDriverAmount = payToDriverAmount;
    }

    public String getPayToDriverStatus() {
        return payToDriverStatus;
    }

    public void setPayToDriverStatus(String payToDriverStatus) {
        this.payToDriverStatus = payToDriverStatus;
    }

    public String getPayToDriverAt() {
        return payToDriverAt;
    }

    public void setPayToDriverAt(String payToDriverAt) {
        this.payToDriverAt = payToDriverAt;
    }

    public String getDeliveryTitle() {
        return deliveryTitle;
    }

    public void setDeliveryTitle(String deliveryTitle) {
        this.deliveryTitle = deliveryTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    String deliveryId;
    String amount;
    String transactionId;
    String cardType;
    String cardNumber;
    String status;
    String payToDriverAmount;
    String payToDriverStatus;
    String payToDriverAt;
    String deliveryTitle;
    String type;
    String date;
}

package com.grabid.models;

/**
 * Created by graycell on 30/3/18.
 */

public class CollectJob {
    String amountcollected;
    String paymodeid;

    public String getAmountcollected() {
        return amountcollected;
    }

    public void setAmountcollected(String amountcollected) {
        this.amountcollected = amountcollected;
    }

    public String getPaymodeid() {
        return paymodeid;
    }

    public void setPaymodeid(String paymodeid) {
        this.paymodeid = paymodeid;
    }

    public String getItemdeliverytitle() {
        return itemdeliverytitle;
    }

    public void setItemdeliverytitle(String itemdeliverytitle) {
        this.itemdeliverytitle = itemdeliverytitle;
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymenttype() {
        return paymenttype;
    }

    public void setPaymenttype(String paymenttype) {
        this.paymenttype = paymenttype;
    }

    String itemdeliverytitle;
    String deliveryId;
    String createdAt;
    String deliveryStatus;
    String amount;
    String paymenttype;
}

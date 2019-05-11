package com.grabid.models;

/**
 * Created by vinod on 11/4/2016.
 */
public class Shipment {
    String deliveryTypeID;

    public String getDeliveryItem() {
        return deliveryItem;
    }

    public void setDeliveryItem(String deliveryItem) {
        this.deliveryItem = deliveryItem;
    }

    String deliveryItem;
    String deliverySubTypeID;

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    String other;

    public String getDeliverySubSubTypeID() {
        return deliverySubSubTypeID;
    }

    public void setDeliverySubSubTypeID(String deliverySubSubTypeID) {
        this.deliverySubSubTypeID = deliverySubSubTypeID;
    }

    String deliverySubSubTypeID;
    String qty;
    String weight;
    String length;
    String width;

    public String getVaccination() {
        return vaccination;
    }

    public void setVaccination(String vaccination) {
        this.vaccination = vaccination;
    }

    public String getDeliveryTypeId() {
        return deliveryTypeID;
    }

    public void setDeliveryTypeId(String delivery) {
        this.deliveryTypeID = delivery;
    }

    public String getDeliverySubTypeID() {
        return deliverySubTypeID;
    }

    public void setDeliverySubTypeID(String deliveryType) {
        this.deliverySubTypeID = deliveryType;
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

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    String height;
    String stackable;
    String hazardous;
    String more;
    String carrier;
    String name;
    String breed;
    String vaccination;
}

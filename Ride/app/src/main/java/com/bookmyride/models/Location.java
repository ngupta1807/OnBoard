package com.bookmyride.models;

/**
 * Created by vinod on 11/25/2016.
 */
public class Location {
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    String location;
    String locationID;
    String category;
    String categoryID;
    String expiry;

    public String getIsPassenger() {
        return isPassenger;
    }

    public void setIsPassenger(String isPassenger) {
        this.isPassenger = isPassenger;
    }

    String isPassenger;
}

package com.grabid.models;

import java.io.Serializable;

/**
 * Created by graycell on 13/12/17.
 */

public class SearchAddFavModel implements Serializable {
    String id, firstName, lastName, userName, Mobile, driver_rating, shipper_rating, email, favouriteuserid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFavouriteuserid() {
        return favouriteuserid;
    }

    public void setFavouriteuserid(String favouriteuserid) {
        this.favouriteuserid = favouriteuserid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getDriver_rating() {
        return driver_rating;
    }

    public void setDriver_rating(String driver_rating) {
        this.driver_rating = driver_rating;
    }

    public String getShipper_rating() {
        return shipper_rating;
    }

    public void setShipper_rating(String shipper_rating) {
        this.shipper_rating = shipper_rating;
    }
}

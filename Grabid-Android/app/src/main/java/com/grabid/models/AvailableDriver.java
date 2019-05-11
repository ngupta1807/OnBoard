package com.grabid.models;

/**
 * Created by vinod on 2/21/2018.
 */

public class AvailableDriver {
    public String getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(String driverRating) {
        this.driverRating = driverRating;
    }

    String driverRating;
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String username;

    public String getMoible() {
        return moible;
    }

    public void setMoible(String moible) {
        this.moible = moible;
    }

    String moible= "";
    String id;

}

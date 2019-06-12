package com.bookmyride.models;

import java.io.Serializable;

public class CarType implements Serializable {

    private String driver_lat, driver_long, driver_ID;
    private String cat_name, cat_time, cat_image, cat_id;
    private String Selected_Cat;
    private String icon_normal, icon_active;
    private String rate_cartype, rate_note, minfare_amt, minfare_km, afterfare_amt, afterfare_km, otherfare_amt, otherfare_km;
    private String currencyCode;
    private String other_ride_options, guarantee_status, guarantee_msg, other_cat_drivers_status, other_cat_driver_msg;

    public String getOther_ride_options() {
        return other_ride_options;
    }

    public void setOther_ride_options(String other_ride_options) {
        this.other_ride_options = other_ride_options;
    }

    public String getGuarantee_status() {
        return guarantee_status;
    }

    public void setGuarantee_status(String guarantee_status) {
        this.guarantee_status = guarantee_status;
    }

    public String getGuarantee_msg() {
        return guarantee_msg;
    }

    public void setGuarantee_msg(String guarantee_msg) {
        this.guarantee_msg = guarantee_msg;
    }

    public String getOther_cat_drivers_status() {
        return other_cat_drivers_status;
    }

    public void setOther_cat_drivers_status(String other_cat_drivers_status) {
        this.other_cat_drivers_status = other_cat_drivers_status;
    }

    public String getOther_cat_driver_msg() {
        return other_cat_driver_msg;
    }

    public void setOther_cat_driver_msg(String other_cat_driver_msg) {
        this.other_cat_driver_msg = other_cat_driver_msg;
    }


    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getDriver_lat() {
        return driver_lat;
    }


    public String getDriver_ID() {
        return driver_ID;
    }

    public void setDriver_ID(String driver_ID) {
        this.driver_ID = driver_ID;
    }

    public void setDriver_lat(String driver_lat) {
        this.driver_lat = driver_lat;
    }

    public String getDriver_long() {
        return driver_long;
    }

    public void setDriver_long(String driver_long) {
        this.driver_long = driver_long;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getCat_time() {
        return cat_time;
    }

    public void setCat_time(String cat_time) {
        this.cat_time = cat_time;
    }

    public String getCat_image() {
        return cat_image;
    }

    public void setCat_image(String cat_image) {
        this.cat_image = cat_image;
    }

    public String getSelected_Cat() {
        return Selected_Cat;
    }

    public void setSelected_Cat(String selected_Cat) {
        Selected_Cat = selected_Cat;
    }

    public String getIcon_normal() {
        return icon_normal;
    }

    public void setIcon_normal(String icon_normal) {
        this.icon_normal = icon_normal;
    }

    public String getIcon_active() {
        return icon_active;
    }

    public void setIcon_active(String icon_active) {
        this.icon_active = icon_active;
    }

    public String getRate_cartype() {
        return rate_cartype;
    }

    public void setRate_cartype(String rate_cartype) {
        this.rate_cartype = rate_cartype;
    }

    public String getRate_note() {
        return rate_note;
    }

    public void setRate_note(String rate_note) {
        this.rate_note = rate_note;
    }

    public String getMinfare_amt() {
        return minfare_amt;
    }

    public void setMinfare_amt(String minfare_amt) {
        this.minfare_amt = minfare_amt;
    }

    public String getMinfare_km() {
        return minfare_km;
    }

    public void setMinfare_km(String minfare_km) {
        this.minfare_km = minfare_km;
    }

    public String getAfterfare_amt() {
        return afterfare_amt;
    }

    public void setAfterfare_amt(String afterfare_amt) {
        this.afterfare_amt = afterfare_amt;
    }

    public String getAfterfare_km() {
        return afterfare_km;
    }

    public void setAfterfare_km(String afterfare_km) {
        this.afterfare_km = afterfare_km;
    }

    public String getOtherfare_amt() {
        return otherfare_amt;
    }

    public void setOtherfare_amt(String otherfare_amt) {
        this.otherfare_amt = otherfare_amt;
    }

    public String getOtherfare_km() {
        return otherfare_km;
    }

    public void setOtherfare_km(String otherfare_km) {
        this.otherfare_km = otherfare_km;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}

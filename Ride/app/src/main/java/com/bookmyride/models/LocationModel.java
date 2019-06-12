package com.bookmyride.models;

import java.io.Serializable;
import java.util.ArrayList;


public class LocationModel implements Serializable
{
    private String rate_cartype, rate_note, minfare_amt, minfare_km, afterfare_amt, afterfare_km, otherfare_amt, otherfare_km;
    private String currencyCode;

    private ArrayList<LocationModel> estimate_pojo;

    public LocationModel()
    {
    }

    public LocationModel(String dummy, ArrayList<LocationModel> data) {
        this.estimate_pojo = data;
    }

    public ArrayList<LocationModel> getEstimatePojo() {
        return this.estimate_pojo;
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

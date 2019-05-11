package com.grabid.models;

/**
 * Created by vinod on 11/4/2016.
 */
public class ShipmentAddress {
    String puLat, puLang, doLat, doLang,suitablevehicle;

    public String getDoLang() {
        return doLang;
    }

    public String getSuitablevehicle() {
        return suitablevehicle;
    }

    public void setSuitablevehicle(String suitablevehicle) {
        this.suitablevehicle = suitablevehicle;
    }

    public void setDoLang(String doLang) {
        this.doLang = doLang;
    }

    public void String(String doLang) {
        this.doLang = doLang;
    }

    public String getPuLat() {
        return puLat;
    }

    public void setPuLat(String puLat) {
        this.puLat = puLat;
    }

    public String getPuLang() {
        return puLang;
    }

    public void setPuLang(String puLang) {
        this.puLang = puLang;
    }

    public String getDoLat() {
        return doLat;
    }

    public void setDoLat(String doLat) {
        this.doLat = doLat;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPuContact() {
        return puContact;
    }

    public void setPuContact(String puContact) {
        this.puContact = puContact;
    }

    public String getPuMobile() {
        return puMobile;
    }

    public void setPuMobile(String puMobile) {
        this.puMobile = puMobile;
    }

    public String getPuAddress() {
        return puAddress;
    }

    public void setPuAddress(String puAddress) {
        this.puAddress = puAddress;
    }

    public String getPuDate() {
        return puDate;
    }

    public void setPuDate(String puDate) {
        this.puDate = puDate;
    }

    public String getDoContact() {
        return doContact;
    }

    public void setDoContact(String doContact) {
        this.doContact = doContact;
    }

    public String getDoMobile() {
        return doMobile;
    }

    public void setDoMobile(String doMobile) {
        this.doMobile = doMobile;
    }

    public String getDoAddress() {
        return doAddress;
    }

    public void setDoAddress(String doAddress) {
        this.doAddress = doAddress;
    }

    public String getDoDate() {
        return doDate;
    }

    public void setDoDate(String doDate) {
        this.doDate = doDate;
    }

    public boolean getAuctionBid() {
        return auctionBid;
    }

    public void setAuctionBid(boolean auctionBid) {
        this.auctionBid = auctionBid;
    }

    public String getMaxBid() {
        return maxBid;
    }

    public void setMaxBid(String maxBid) {
        this.maxBid = maxBid;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getFixedOffer() {
        return fixedOffer;
    }

    public void setFixedOffer(String fixedOffer) {
        this.fixedOffer = fixedOffer;
    }

    String title, puContact, puMobile;
    String puAddress, puDate, puEndDate, puDateType, doContact, doMobile, doAddress;
    String doDate;
    String doEndDate;

    public String getPuEndDate() {
        return puEndDate;
    }

    public void setPuEndDate(String puEndDate) {
        this.puEndDate = puEndDate;
    }

    public String getPuDateType() {
        return puDateType;
    }

    public void setPuDateType(String puDateType) {
        this.puDateType = puDateType;
    }

    public String getDoEndDate() {
        return doEndDate;
    }

    public void setDoEndDate(String doEndDate) {
        this.doEndDate = doEndDate;
    }

    public String getDoDateType() {
        return doDateType;
    }

    public void setDoDateType(String doDateType) {
        this.doDateType = doDateType;
    }

    String doDateType;
    String maxBid;
    String startDate;
    String endDate;
    String fixedOffer;

    public String getPuCity() {
        return puCity;
    }

    public void setPuCity(String puCity) {
        this.puCity = puCity;
    }

    public String getPuState() {
        return puState;
    }

    public void setPuState(String puState) {
        this.puState = puState;
    }

    public String getPuCountry() {
        return puCountry;
    }

    public void setPuCountry(String puCountry) {
        this.puCountry = puCountry;
    }

    public String getDoCity() {
        return doCity;
    }

    public void setDoCity(String doCity) {
        this.doCity = doCity;
    }

    public String getDoState() {
        return doState;
    }

    public void setDoState(String doState) {
        this.doState = doState;
    }

    public String getDoCountry() {
        return doCountry;
    }

    public void setDoCountry(String doCountry) {
        this.doCountry = doCountry;
    }

    public String getPuDateTimeChoose() {
        return puDateTimeChoose;
    }

    public void setPuDateTimeChoose(String puDateTimeChoose) {
        this.puDateTimeChoose = puDateTimeChoose;
    }

    public String getDoDateTimeChoose() {
        return doDateTimeChoose;
    }

    public void setDoDateTimeChoose(String doDateTimeChoose) {
        this.doDateTimeChoose = doDateTimeChoose;
    }

    String puCity;
    String puState;
    String puCountry;
    String doCity;
    String doState;
    String doCountry;
    String doDateTimeChoose;
    String puDateTimeChoose;
    boolean auctionBid;
}

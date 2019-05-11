package com.grabid.models;

/**
 * Created by vinod on 10/28/2016.
 */
public class UserInfo {
    String id;

    public boolean isLogin() {
        return IsLogin;
    }

    public String getHas_unread_notifications() {
        return has_unread_notifications;
    }

    public void setHas_unread_notifications(String has_unread_notifications) {
        this.has_unread_notifications = has_unread_notifications;
    }

    public void setLogin(boolean login) {
        IsLogin = login;

    }


    String userName, has_unread_notifications;
    String email;
    boolean IsLogin;

    public String getRegistergst() {
        return registergst;
    }

    public void setRegistergst(String registergst) {
        this.registergst = registergst;
    }

    String bankDetail, CreditCard;
    String gender, dob;
    String registergst;

    public String getApp_token() {
        return app_token;
    }

    public void setApp_token(String app_token) {
        this.app_token = app_token;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    String app_token;

    public String getIsprofileCompleted() {
        return IsprofileCompleted;
    }

    public void setIsprofileCompleted(String isprofileCompleted) {
        IsprofileCompleted = isprofileCompleted;
    }

    String IsprofileCompleted;
    String Islaststep;

    public String getIslaststep() {
        return Islaststep;
    }

    public void setIslaststep(String islaststep) {
        Islaststep = islaststep;
    }

    public String getBankDetail() {
        return bankDetail;
    }

    public void setBankDetail(String bankDetail) {
        this.bankDetail = bankDetail;
    }

    public String getCreditCard() {
        return CreditCard;
    }

    public void setCreditCard(String creditCard) {
        CreditCard = creditCard;
    }

    public String getUsedReferCode() {
        return usedReferCode;
    }

    public void setUsedReferCode(String usedReferCode) {
        this.usedReferCode = usedReferCode;
    }

    public String getAuth() {
        return auth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLaseName() {
        return laseName;
    }

    public void setLaseName(String laseName) {
        this.laseName = laseName;
    }

    public String getBidderID() {
        return bidderID;
    }

    public void setBidderID(String bidderID) {
        this.bidderID = bidderID;
    }

    public String getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(String verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
    }

    public String getAdminApprovalStatus() {
        return adminApprovalStatus;
    }

    public void setAdminApprovalStatus(String adminApprovalStatus) {
        this.adminApprovalStatus = adminApprovalStatus;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public String getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(String driverRating) {
        this.driverRating = driverRating;
    }

    public String getShipperRating() {
        return shipperRating;
    }

    public void setShipperRating(String shipperRating) {
        this.shipperRating = shipperRating;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    String usedReferCode;
    String user_payment_modes;

    public String getUser_payment_modes() {
        return user_payment_modes;
    }

    public void setUser_payment_modes(String user_payment_modes) {
        this.user_payment_modes = user_payment_modes;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    String mobile;

    public String getHasMultipleVehicle() {
        return hasMultipleVehicle;
    }

    public void setHasMultipleVehicle(String hasMultipleVehicle) {
        this.hasMultipleVehicle = hasMultipleVehicle;
    }

    public String getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(String isOwner) {
        this.isOwner = isOwner;
    }

    String hasMultipleVehicle;
    String isOwner;
    String auth, firstName, laseName, bidderID, verifiedStatus, adminApprovalStatus;
    String userType, profileImage, referCode, driverRating, shipperRating, deviceOS;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }
}

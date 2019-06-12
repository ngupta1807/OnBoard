package com.bookmyride.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.bookmyride.api.Key;
import com.bookmyride.models.Address;
import com.bookmyride.models.DriverCategory;
import com.bookmyride.models.Location;
import com.bookmyride.models.Profile;
import com.bookmyride.models.WaitingInfo;

import org.json.JSONArray;

import java.util.HashMap;

/**
 * Created by vinod on 1/5/2017.
 */
public class SessionHandler {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private boolean serviceState;

    @SuppressLint("CommitPrefEdits")
    public SessionHandler(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Key.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public String getIP() {
        //Demo Server
        //return pref.getString(Key.KEY_IP, "54.252.133.133");

        //Ride247 Server
        //return pref.getString(Key.KEY_IP, "app.ride247.com");

        //Demo Server
        //return pref.getString(Key.KEY_IP, "demo.ride247.com");

        //Dev Server
        //return pref.getString(Key.KEY_IP, "dev.ride247.com");

        //Ride24:7 Server
        return pref.getString(Key.KEY_IP, "103.43.154.123");

        //Parveen System
        //return pref.getString(Key.KEY_IP, "192.168.100.15/Ride247V2/html");
    }

    public void saveCurrentBookingId(String key) {
        editor.putString("CurrentBookingId", key);
        editor.commit();
    }

    public String getCurrentBookingId() {
        return pref.getString("CurrentBookingId", "");
    }

    public void saveGCMKey(String key) {
        editor.putString(Key.GCM_KEY, key);
        editor.commit();
    }

    public String getGCMKey() {
        return pref.getString(Key.GCM_KEY, "");
    }

    public void saveToken(String token) {
        editor.putString(Key.TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(Key.TOKEN, "");
    }

    public String getUserType() {
        return pref.getString(Key.USER_TYPE, "");
    }

    public void saveUserType(String type) {
        editor.putString(Key.USER_TYPE, type);
        editor.commit();
    }

    public String getSessionCode() {
        return pref.getString(Key.SESSION_CODE, "");
    }

    public void saveSessionCode(String type) {
        editor.putString(Key.SESSION_CODE, type);
        editor.commit();
    }

    public String getReferralCode() {
        return pref.getString(Key.REFERRAL_CODE, "");
    }

    public void saveReferralCode(String type) {
        editor.putString(Key.REFERRAL_CODE, type);
        editor.commit();
    }

    public String getUser() {
        return pref.getString(Key.USERNAME, "");
    }

    public String getUserNo() {
        String phone = pref.getString(Key.PHONE, "");
        if (pref.getString(Key.PHONE, "").startsWith("0")) {
            phone = phone.substring(1, phone.length());
        }
        return pref.getString(Key.DIAL_CODE, "") + phone;
    }

    public String getUserImg() {
        return pref.getString(Key.IMAGE, "");
    }

    public void saveUserImg(String img) {
        editor.putString(Key.IMAGE, img);
        editor.commit();
    }

    public String getUserID() {
        return pref.getString(Key.ID, "");
    }

    public void saveLocation(String isCustomer, String location, String locationID,
                             JSONArray category,
                             String expiry) {
        editor.putString(Key.IS_PASSENGER, isCustomer);
        editor.putString(Key.LOCATION, location);
        editor.putString(Key.LOCATION_ID, locationID);
        editor.putString(Key.CATEGORY, category.toString());
        //editor.putString(Key.CATEGORY_ID, categoryId);
        editor.putString(Key.EXPIRY, expiry);
        editor.commit();
    }

    public Location getLocation() {
        Location loc = new Location();
        loc.setIsPassenger(pref.getString(Key.IS_PASSENGER, "0"));
        loc.setLocation(pref.getString(Key.LOCATION, ""));
        loc.setLocationID(pref.getString(Key.LOCATION_ID, ""));
        loc.setCategory(pref.getString(Key.CATEGORY, ""));
        //loc.setCategoryID(pref.getString(Keys.CATEGORY_ID, ""));
        loc.setExpiry(pref.getString(Key.EXPIRY, ""));
        return loc;
    }

    public void clearLocation() {
        editor.putString(Key.LOCATION, "");
        editor.putString(Key.LOCATION_ID, "");
        editor.putString(Key.CATEGORY, "");
        //editor.putString(Key.CATEGORY_ID, categoryId);
        editor.putString(Key.EXPIRY, "");
        editor.commit();
    }

    public void saveAddress(String address, String city, String state, String postalCode,
                            String country, String countryCode, String dialCode,
                            String mobile, String otp, String latitude, String longitude) {
        editor.putString(Key.ADDRESS, address);
        editor.putString(Key.CITY, city);
        editor.putString(Key.STATE, state);
        editor.putString(Key.POSTALCODE, postalCode);
        editor.putString(Key.COUNTRY, country);
        editor.putString(Key.COUNTRY_CODE, countryCode);
        editor.putString(Key.DIAL_CODE, dialCode);
        editor.putString(Key.MOBILE, mobile);
        editor.putString(Key.OTP, otp);
        editor.putString(Key.LATITUDE, latitude);
        editor.putString(Key.LONGITUDE, longitude);
        editor.commit();
    }

    public void clearAddress() {
        editor.putString(Key.ADDRESS, "");
        editor.putString(Key.CITY, "");
        editor.putString(Key.STATE, "");
        editor.putString(Key.POSTALCODE, "");
        editor.putString(Key.COUNTRY, "");
        editor.putString(Key.COUNTRY_CODE, "");
        editor.putString(Key.DIAL_CODE, "");
        editor.putString(Key.MOBILE, "");
        editor.putString(Key.OTP, "");
        editor.putString(Key.LATITUDE, "");
        editor.putString(Key.LONGITUDE, "");
        editor.commit();
    }

    public Address getAddress() {
        Address address = new Address();
        address.setAddress(pref.getString(Key.ADDRESS, ""));
        address.setCity(pref.getString(Key.CITY, ""));
        address.setState(pref.getString(Key.STATE, ""));
        address.setPostalCode(pref.getString(Key.POSTALCODE, ""));
        address.setCountry(pref.getString(Key.COUNTRY, ""));
        address.setCountryCode(pref.getString(Key.COUNTRY_CODE, ""));
        address.setDialCode(pref.getString(Key.DIAL_CODE, ""));
        address.setMobile(pref.getString(Key.MOBILE, ""));
        address.setOtp(pref.getString(Key.OTP, ""));
        address.setLatitude(pref.getString(Key.LATITUDE, ""));
        address.setLongitude(pref.getString(Key.LONGITUDE, ""));
        return address;
    }

    public void saveProfile(String fName, String lName, String userName, String email, String password,
                            String licenceNumber, String expiry) {
        editor.putString(Key.FIRST_NAME, fName);
        editor.putString(Key.LAST_NAME, lName);
        editor.putString(Key.USERNAME, userName);
        editor.putString(Key.EMAIL, email);
        editor.putString(Key.PASSWORD, password);
        editor.putString(Key.LICENCE_EXPIRY, expiry);
        editor.putString(Key.LICENCE_NUMBER, licenceNumber);
        editor.commit();
    }

    public void clearProfile() {
        //editor.putString(Key.FIRST_NAME, "");
        //editor.putString(Key.LAST_NAME, "");
        //editor.putString(Key.USERNAME, "");
        //editor.putString(Key.EMAIL, "");
        //editor.putString(Key.PASSWORD, "");
        //editor.putString(Key.LICENCE_EXPIRY, "");
        //editor.putString(Key.LICENCE_NUMBER, "");
        //editor.commit();
    }

    public Profile getProfile() {
        Profile info = new Profile();
        info.setFirstName(pref.getString(Key.FIRST_NAME, ""));
        info.setLastName(pref.getString(Key.LAST_NAME, ""));
        info.setUserName(pref.getString(Key.USERNAME, ""));
        info.setEmail(pref.getString(Key.EMAIL, ""));
        info.setPassword(pref.getString(Key.PASSWORD, ""));
        info.setLicenceExpiry(pref.getString(Key.LICENCE_EXPIRY, ""));
        info.setLicenceNumber(pref.getString(Key.LICENCE_NUMBER, ""));
        return info;
    }

    public void savePassengerData(String userID, String userType, String userName, String img, String email, String token,
                                  String firstName, String lastName, String phone, String dialCode) {
        editor.putString(Key.ID, userID);
        editor.putString(Key.USER_TYPE, userType);
        editor.putString(Key.USERNAME, userName);
        editor.putString(Key.IMAGE, img);
        editor.putString(Key.EMAIL, email);
        editor.putString(Key.TOKEN, token);
        editor.putString(Key.FIRST_NAME, firstName);
        editor.putString(Key.LAST_NAME, lastName);
        editor.putString(Key.PHONE, phone);
        editor.putString(Key.DIAL_CODE, dialCode);
        editor.commit();
    }

    public Profile getPassengerData() {
        Profile profile = new Profile();
        profile.setUserName(pref.getString(Key.USERNAME, ""));
        profile.setImage(pref.getString(Key.IMAGE, ""));
        profile.setEmail(pref.getString(Key.EMAIL, ""));
        profile.setFirstName(pref.getString(Key.FIRST_NAME, ""));
        profile.setLastName(pref.getString(Key.LAST_NAME, ""));
        profile.setPhone(pref.getString(Key.PHONE, ""));
        profile.setDialCode(pref.getString(Key.DIAL_CODE, ""));
        return profile;
    }

    public void saveDriverData(String userID, String userType, String userName, String img, String email, String token,
                               String firstName, String lastName, String phone, String dialCode, String profile,
                               String isOnline, String isCustomer) {
        editor.putString(Key.ID, userID);
        editor.putString(Key.USER_TYPE, userType);
        editor.putString(Key.USERNAME, userName);
        editor.putString(Key.EMAIL, email);
        editor.putString(Key.TOKEN, token);
        editor.putString(Key.IMAGE, img);
        editor.putString(Key.FIRST_NAME, firstName);
        editor.putString(Key.LAST_NAME, lastName);
        editor.putString(Key.PHONE, phone);
        editor.putString(Key.DIAL_CODE, dialCode);
        editor.putString(Key.PROFILE, profile);
        editor.putString(Key.IS_ONLINE, isOnline);
        editor.putString(Key.IS_CUSTOMER, isCustomer);
        editor.commit();
    }

    public void saveDriverDialogOpen(boolean isEngage) {
        editor.putBoolean(Key.DRIVER_DIALOG, isEngage);
        editor.commit();
    }

    public boolean isDriverEngage() {
        return pref.getBoolean(Key.DRIVER_DIALOG, false);
    }


    public void saveOnlineStatus(boolean isOnline) {
        editor.putBoolean(Key.ONLINE_STATUS, isOnline);
        editor.commit();
    }

    public boolean isDriverOnline() {
        return pref.getBoolean(Key.ONLINE_STATUS, false);
    }

    public String getLastCategory() {
        return pref.getString(Key.CATEGORY_ID, "");
    }

    public void saveLastCategory(String catId) {
        editor.putString(Key.CATEGORY_ID, catId);
        editor.commit();
    }

    public void saveRideData(String rideData) {
        editor.putString(Key.RIDE_DATA, rideData);
        editor.commit();
    }

    public String getRideData() {
        return pref.getString(Key.RIDE_DATA, "");
    }

    public HashMap<String, String> getDriverProfile() {
        HashMap<String, String> driverInfo = new HashMap<String, String>();
        driverInfo.put(Key.ID, pref.getString(Key.ID, ""));
        driverInfo.put(Key.USER_TYPE, pref.getString(Key.USER_TYPE, ""));
        driverInfo.put(Key.USERNAME, pref.getString(Key.USERNAME, ""));
        driverInfo.put(Key.EMAIL, pref.getString(Key.EMAIL, ""));
        driverInfo.put(Key.PHONE, pref.getString(Key.PHONE, ""));
        driverInfo.put(Key.PROFILE, pref.getString(Key.PROFILE, ""));
        return driverInfo;
    }

    public String getDriverID() {
        return pref.getString(Key.DRIVER_ID, "");
    }

    public String isPassenger() {
        return pref.getString(Key.IS_CUSTOMER, "");
    }

    public void saveDriverID(String key) {
        editor.putString(Key.DRIVER_ID, key);
        editor.commit();
    }

    public void savePicPath(String key) {
        editor.putString(Key.PIC_PATH, key);
        editor.commit();
    }

    public String getPicPath() {
        return pref.getString(Key.PIC_PATH, "");
    }

    public void saveTaxiImgPath(String key) {
        editor.putString(Key.TAXI_PATH, key);
        editor.commit();
    }

    public String getTaxiImgPath() {
        return pref.getString(Key.TAXI_PATH, "");
    }

    public void saveLicenceImgPath(String key) {
        editor.putString(Key.LICENCE_PATH, key);
        editor.commit();
    }

    public String getLicenceImgPath() {
        return pref.getString(Key.LICENCE_PATH, "");
    }

    public void saveProfileImgPath(String key) {
        editor.putString(Key.PROFILE_PATH, key);
        editor.commit();
    }

    public String getProfileImgPath() {
        return pref.getString(Key.PROFILE_PATH, "");
    }

    public void saveWaitingInfo(long updatedTime, long timeInMilliseconds, long startTime,
                                long timeSwapBuff, int mins, int secs) {
        editor.putLong("updatedTime", updatedTime);
        editor.putLong("timeInMilliseconds", timeInMilliseconds);
        editor.putLong("startTime", startTime);
        editor.putLong("timeSwapBuff", timeSwapBuff);
        editor.putInt("mins", mins);
        editor.putInt("secs", secs);
        editor.commit();
    }

    public WaitingInfo getWaitingInfo() {
        WaitingInfo waitingData = new WaitingInfo();
        waitingData.setMins(pref.getInt("mins", 0));
        waitingData.setSecs(pref.getInt("secs", 0));
        waitingData.setStartTime(pref.getLong("startTime", 0L));
        waitingData.setTimeInMilliseconds(pref.getLong("timeInMilliseconds", 0L));
        waitingData.setTimeSwapBuff(pref.getLong("timeSwapBuff", 0L));
        waitingData.setUpdatedTime(pref.getLong("updatedTime", 0L));
        return waitingData;
    }

    public DriverCategory getOnlineDriverData() {
        DriverCategory dc = new DriverCategory();
        dc.setDriverCateogry(pref.getString("category_id", ""));
        dc.setVehicleId(pref.getString("vehicleID", "0"));
        dc.setVehicleNum(pref.getString("vehicleNum", "0"));
        return dc;
    }

    public void saveDriverDategory(String category, String vehcileID, String vehicleNum) {
        editor.putString("category_id", category);
        editor.putString("vehicleID", vehcileID);
        editor.putString("vehicleNum", vehicleNum);
        editor.commit();
    }

    public String getEventIds() {
        return pref.getString(Key.EVENT_IDs, "");
    }

    public void saveEventIds(String key) {
        editor.putString(Key.EVENT_IDs, key);
        editor.commit();
    }

    public String getLat() {
        return pref.getString(Key.LATITUDE, "");
    }

    public void saveLat(String key) {
        editor.putString(Key.LATITUDE, key);
        editor.commit();
    }

    public String getLng() {
        return pref.getString(Key.LONGITUDE, "");
    }

    public void saveLng(String key) {
        editor.putString(Key.LONGITUDE, key);
        editor.commit();
    }

    public void saveUserActualType(String userType) {
        editor.putString(Key.IS_BOTH_TYPE, userType);
        editor.commit();
    }

    public boolean isBothTypeUser() {
        String userActualType = pref.getString(Key.IS_BOTH_TYPE, "");
        if (userActualType.equalsIgnoreCase("5"))
            return true;
        else return false;
    }

    public void saveCardExist(boolean hasCard) {
        editor.putBoolean(Key.IS_CARD_SAVED, hasCard);
        editor.commit();
    }

    public boolean isCardExist() {
        return pref.getBoolean(Key.IS_CARD_SAVED, false);
    }

    public String getCardAddress() {
        return pref.getString(Key.CARD_ADDRESS, "");
    }

    public void saveCardAddress(String address) {
        editor.putString(Key.CARD_ADDRESS, address);
        editor.commit();
    }

    public void setFineLocation(Boolean status) {
        editor.putBoolean(Key.FINE_LOCATION, status);
        editor.commit();
    }

    public boolean getFineLocation() {
        return pref.getBoolean(Key.FINE_LOCATION, false);
    }
}

package com.grabid.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.grabid.api.Config;
import com.grabid.api.Keys;
import com.grabid.models.CompanyInfo;
import com.grabid.models.DriverInfo;
import com.grabid.models.Shipment;
import com.grabid.models.ShipmentAddress;
import com.grabid.models.UserInfo;
import com.grabid.models.VehicleInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vinod on 11/2/2016.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(Config.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void saveAdminStatus(String status) {
        editor.putString(Keys.ADMIN_APPROVAL_STATUS, status);
        editor.commit();
    }

    public void saveEmailStatus(String status) {
        editor.putString(Keys.VERIFIED_STATUS, status);
        editor.commit();

    }

    public void saveTimeZoneId(String timezoneid) {
        editor.putString(Keys.TIME_ZONE_ID, timezoneid);
        editor.commit();
    }

    public String getTimeZoneId() {
        return pref.getString(Keys.TIME_ZONE_ID, "");
    }

    public void saveUserDate(String userID, String userName, String email, String image, String token,
                             String userInfo, String driverInfo, String licenceImg, String companyInfo,
                             String vehicleInfo, String sRating, String dRating, String ChargeMe, String Payme, String adminapproval, String emailverified, boolean IsLogin) {
        editor.putBoolean(Config.IS_LOGIN, true);
        editor.putString(Config.USER_ID, userID);
        editor.putString(Config.USER_NAME, userName);
        editor.putString(Config.EMAIL, email);
        editor.putString(Keys.KEY_IMAGE, image);
        editor.putString(Config.TOKEN, token);
        editor.putString(Config.USER_INFO, userInfo);
        editor.putString(Config.DRIVER_INFO, driverInfo);
        editor.putString(Keys.LICENCE_IMAGE, licenceImg);
        editor.putString(Config.COMPANY_INFO, companyInfo);
        editor.putString(Config.VEHICLE_INFO, vehicleInfo);
        editor.putString(Config.SHIPPER_RATING, sRating);
        editor.putString(Config.DRIVER_RATING, dRating);
        editor.putString(Config.CREDIT_CARDIN, ChargeMe);
        editor.putString(Config.BANK_DETAILIN, Payme);
        editor.putString(Keys.ADMIN_APPROVAL_STATUS, adminapproval);
        editor.putString(Keys.VERIFIED_STATUS, emailverified);
        editor.putBoolean(Keys.IS_LOGIN, IsLogin);
        editor.commit();
    }

    public void saveIsLogin(boolean IsLogin) {
        editor.putBoolean(Keys.IS_LOGIN, IsLogin);
        editor.commit();
    }

    public void saveShipmentAddress(String title, String puContact, String puMobile,
                                    String puAddress, String puDate, String puDateType, String puEndDate, String doContact, String doMobile, String doAddress,
                                    String doDate, String doDateType, String doEndDate, boolean auctionBid, String maxBid, String startDate, String endDate,
                                    String fixedOffer, String puLat, String puLng, String doLat, String doLng,
                                    String puCity, String puState, String puCountry, String doCity, String doState, String doCountry, String puDateTimeChoose, String doDateTimeChoose) {
        editor.putString(Keys.TITLE, title);
        editor.putString(Keys.PICKUP_CONTACT_PERSON, puContact);
        editor.putString(Keys.PICKUP_MOBILE, puMobile);
        editor.putString(Keys.PICKUP_ADDRESS, puAddress);
        editor.putString(Keys.PICKUP_DATE, puDate);
        editor.putString(Keys.PICKUP_DAY_TYPE, puDateType);
        editor.putString(Keys.PICKUP_END_DAY, puEndDate);
        editor.putString(Keys.DROPOFF_CONTACT, doContact);
        editor.putString(Keys.DROPOFF_MOBILE, doMobile);
        editor.putString(Keys.DROPOFF_ADDRESS, doAddress);
        editor.putString(Keys.DROPOFF_DATE, doDate);
        editor.putString(Keys.DROPOFF_DAY_TYPE, doDateType);
        editor.putString(Keys.DROPOFF_END_DAY, doEndDate);
        editor.putBoolean(Keys.AUCTION_BID, auctionBid);
        editor.putString(Keys.MAX_AUCTION_BID, maxBid);
        editor.putString(Keys.AUCTION_START_TIME, startDate);
        editor.putString(Keys.AUCTION_END_TIME, endDate);
        editor.putString(Keys.FIXED_OFFER, fixedOffer);
        editor.putString(Keys.PICKUP_LATITUDE, puLat);
        editor.putString(Keys.PICKUP_LONGITUDE, puLng);
        editor.putString(Keys.DROPOFF_LATITUDE, doLat);
        editor.putString(Keys.DROPOFF_LONGITUDE, doLng);
        editor.putString(Keys.PICKUP_CITY, puCity);
        editor.putString(Keys.PICKUP_STATE, puState);
        editor.putString(Keys.PICKUP_COUNTRY, puCountry);
        editor.putString(Keys.DROPOFF_CITY, doCity);
        editor.putString(Keys.DROPOFF_STATE, doState);
        editor.putString(Keys.DROPOFF_COUNTRY, doCountry);
        editor.putString(Keys.PICKUP_DATETIME, puDateTimeChoose);
        editor.putString(Keys.DROPOFF_DATETIME, doDateTimeChoose);
        editor.commit();
    }

    public ShipmentAddress getShipmentAddress() {
        ShipmentAddress address = new ShipmentAddress();
        address.setTitle(pref.getString(Keys.TITLE, ""));
        address.setPuContact(pref.getString(Keys.PICKUP_CONTACT_PERSON, ""));
        address.setPuMobile(pref.getString(Keys.PICKUP_MOBILE, ""));
        address.setPuAddress(pref.getString(Keys.PICKUP_ADDRESS, ""));
        address.setPuDate(pref.getString(Keys.PICKUP_DATE, ""));
        address.setPuDateType(pref.getString(Keys.PICKUP_DAY_TYPE, ""));
        address.setPuEndDate(pref.getString(Keys.PICKUP_END_DAY, ""));
        address.setDoContact(pref.getString(Keys.DROPOFF_CONTACT, ""));
        address.setDoMobile(pref.getString(Keys.DROPOFF_MOBILE, ""));
        address.setDoAddress(pref.getString(Keys.DROPOFF_ADDRESS, ""));
        address.setDoDate(pref.getString(Keys.DROPOFF_DATE, ""));
        address.setDoDateType(pref.getString(Keys.DROPOFF_DAY_TYPE, ""));
        address.setDoEndDate(pref.getString(Keys.DROPOFF_END_DAY, ""));
        address.setAuctionBid(pref.getBoolean(Keys.AUCTION_BID, false));
        address.setMaxBid(pref.getString(Keys.MAX_AUCTION_BID, ""));
        address.setStartDate(pref.getString(Keys.AUCTION_START_TIME, ""));
        address.setEndDate(pref.getString(Keys.AUCTION_END_TIME, ""));
        address.setFixedOffer(pref.getString(Keys.FIXED_OFFER, ""));
        address.setPuLat(pref.getString(Keys.PICKUP_LATITUDE, ""));
        address.setPuLang(pref.getString(Keys.PICKUP_LONGITUDE, ""));
        address.setDoLat(pref.getString(Keys.DROPOFF_LATITUDE, ""));
        address.setDoLang(pref.getString(Keys.DROPOFF_LONGITUDE, ""));
        address.setPuCity(pref.getString(Keys.PICKUP_CITY, ""));
        address.setPuState(pref.getString(Keys.PICKUP_STATE, ""));
        address.setPuCountry(pref.getString(Keys.PICKUP_COUNTRY, ""));
        address.setDoCity(pref.getString(Keys.DROPOFF_CITY, ""));
        address.setDoState(pref.getString(Keys.DROPOFF_STATE, ""));
        address.setDoCountry(pref.getString(Keys.DROPOFF_COUNTRY, ""));
        address.setDoDateTimeChoose(pref.getString(Keys.DROPOFF_DATETIME, ""));
        address.setPuDateTimeChoose(pref.getString(Keys.PICKUP_DATETIME, ""));
        return address;
    }

    public void saveShipMent(String delivery, String deliveryType, String qty, String weight,
                             String length, String width, String height, String stackable,
                             String hazardous, String more, String carrier, String name,
                             String breed, String vaccination, String other, String subsubid,
                             String deliveryItem) {
        editor.putString(Keys.DELIVERY_TYPE_ID, delivery);
        editor.putString(Keys.ITEM_DELIVERY_TYPE_SUB_ID, deliveryType);
        editor.putString(Keys.ITEM_QTY, qty);
        editor.putString(Keys.ITEM_WEIGHT, weight);
        editor.putString(Keys.ITEM_LENGTH, length);
        editor.putString(Keys.ITEM_WIDTH, width);
        editor.putString(Keys.ITEM_HEIGHT, height);
        editor.putString(Keys.ITEM_STACKABLE, stackable);
        editor.putString(Keys.ITEM_HAZARDOUS, hazardous);
        editor.putString(Keys.ITEM_MORE, more);
        editor.putString(Keys.ITEM_ANIMAL_CARRIER, carrier);
        editor.putString(Keys.ITEM_ANIMAL_NAME, name);
        editor.putString(Keys.ITEM_ANIMAL_BREED, breed);
        editor.putString(Keys.ITEM_CURRENT_VACCINATIONS, vaccination);
        editor.putString(Keys.ITEM_DELIVAR_OTHER, other);
        editor.putString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID, subsubid);
        editor.putString(Keys.DELIVERY_ITEM, deliveryItem);
        editor.commit();
    }

    public Shipment getShipment() {
        Shipment shipment = new Shipment();
        shipment.setDeliveryItem(pref.getString(Keys.DELIVERY_ITEM, ""));
        shipment.setDeliveryTypeId(pref.getString(Keys.DELIVERY_TYPE_ID, ""));
        shipment.setDeliverySubTypeID(pref.getString(Keys.ITEM_DELIVERY_TYPE_SUB_ID, ""));
        shipment.setQty(pref.getString(Keys.ITEM_QTY, ""));
        shipment.setWeight(pref.getString(Keys.ITEM_WEIGHT, ""));
        shipment.setLength(pref.getString(Keys.ITEM_LENGTH, ""));
        shipment.setWidth(pref.getString(Keys.ITEM_WIDTH, ""));
        shipment.setHeight(pref.getString(Keys.ITEM_HEIGHT, ""));
        shipment.setStackable(pref.getString(Keys.ITEM_STACKABLE, ""));
        shipment.setHazardous(pref.getString(Keys.ITEM_HAZARDOUS, ""));
        shipment.setMore(pref.getString(Keys.ITEM_MORE, ""));
        shipment.setCarrier(pref.getString(Keys.ITEM_ANIMAL_CARRIER, ""));
        shipment.setName(pref.getString(Keys.ITEM_ANIMAL_NAME, ""));
        shipment.setBreed(pref.getString(Keys.ITEM_ANIMAL_BREED, ""));
        shipment.setVaccination(pref.getString(Keys.ITEM_CURRENT_VACCINATIONS, ""));
        shipment.setOther(pref.getString(Keys.ITEM_DELIVAR_OTHER, ""));
        shipment.setDeliverySubSubTypeID(pref.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID, ""));
        shipment.setVaccination(pref.getString(Keys.ITEM_CURRENT_VACCINATIONS, ""));
        return shipment;
    }

    public CompanyInfo getCompanyInfo() {
        String companyInfo = pref.getString(Config.COMPANY_INFO, "");
        CompanyInfo info = new CompanyInfo();
        try {
            JSONObject data = new JSONObject(companyInfo);
            info.setId(data.getString(Keys.KEY_ID));
            info.setName(data.getString(Keys.NAME));
            info.setAbnNumber(data.getString(Keys.ABN_NUMBER));
            info.setAddress(data.getString(Keys.ADDRESS));
            // info.setCompanyMobile(data.getString(Keys.COMPANY_MOBILE));
            // info.setContactPerson(data.getString(Keys.CONTACT_PERSON));
            info.setCountryName(data.getString(Keys.COUNTRY_NAME));
            info.setFreightInsuranceCover(data.getString(Keys.FREIGHT_INSURANCE_COVER));
            info.setFreightInsurancePolicy(data.getString(Keys.FREIGHT_INSURANCE_POLICY));
            info.setOfficeNumber(data.getString(Keys.OFFICE_NUMBER));
            info.setStateId(data.getString(Keys.STATE_ID));
            info.setCountryID(data.getString(Keys.COUNTRY_ID));
            info.setStateName(data.getString(Keys.STATE_NAME));
            info.setSuburb(data.getString(Keys.SUBURB));
            info.setVehicleInfleet(data.getString(Keys.VEHICLE_IN_FLEET));
            info.setVehiclesInfo(data.getString(Keys.VEHICLE));
            info.setVehicleNnsurance(data.getString(Keys.VEHICLE_INSURANCE_POLICY));
            info.setVehicleQtyType(data.getString(Keys.VEHICLE_QTY));
            info.setPostalCode(data.getString(Keys.POSTALCODE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return info;
    }

    public DriverInfo getDriverInfo() {
        DriverInfo data = new DriverInfo();
        String driverInfo = pref.getString(Config.DRIVER_INFO, "");
        try {
            JSONObject info = new JSONObject(driverInfo);
            data.setLicenceNumber(info.getString(Keys.LICENCE_NUMBER));
            data.setLicenceTypeId(info.getString(Keys.LICENCE_TYPE_ID));
            data.setMedicareNumber(info.getString(Keys.MEDICARE_NUMBER));
            data.setMedicareImage(info.getString(Keys.MEDICARE_IMAGE));
            data.setNationalImage(info.getString(Keys.NATIONAL_IMAGE));
            data.setLicenceValidTill(info.getString(Keys.LICENCE_VALID_TILL));
            data.setLicenceImage(pref.getString(Keys.LICENCE_IMAGE, ""));
            data.setTaxiDriverLicence(info.getString(Keys.TAXI_DRIVER_LICENCE));
            data.setTaxiLicenceImage(info.getString(Keys.TAXI_LICENCE_IMAGE));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public ArrayList<VehicleInfo> getVehicleInfo() {
        String vehicleInfo = pref.getString(Config.VEHICLE_INFO, "");
        ArrayList<VehicleInfo> data = new ArrayList<VehicleInfo>();
        try {
            JSONArray info = new JSONArray(vehicleInfo);
            data.clear();
            for (int i = 0; i < info.length(); i++) {
                JSONObject obj = info.getJSONObject(i);
                VehicleInfo model = new VehicleInfo();
                model.setId(obj.getString(Keys.KEY_ID));
                model.setRegistrationNumber(obj.getString(Keys.REGISTRATION_NUMBER));
                model.setCompanyId(obj.getString(Keys.COMPANY_ID));
                model.setCreatedBy(obj.getString(Keys.CREATED_BY));
                model.setUpdatedBy(obj.getString(Keys.UPDATED_BY));
                data.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void saveCreditCard(String creditCard) {
        editor.putString(Config.CREDIT_CARDIN, creditCard);
        editor.commit();
    }

    public void saveBankDetail(String bankDetail) {
        editor.putString(Config.BANK_DETAILIN, bankDetail);
        editor.commit();
    }

    public void saveRatings(String sRating, String dRating) {
        editor.putString(Config.SHIPPER_RATING, sRating);
        editor.putString(Config.DRIVER_RATING, dRating);
        editor.commit();
    }

    public void saveCount(String count) {
        editor.putString(Config.COUNT, count);
        editor.commit();
    }

    public String getCount() {
        return pref.getString(Config.COUNT, "");
    }

    public UserInfo getUserDetails() {
        String userInfo = pref.getString(Config.USER_INFO, "");
        UserInfo user = new UserInfo();
        try {
            JSONObject info = new JSONObject(userInfo);
            JSONArray jArray = new JSONArray(pref.getString(Config.VEHICLE_INFO, ""));
            JSONObject driverInfo = new JSONObject(pref.getString(Config.DRIVER_INFO, ""));
            JSONObject compInfo = new JSONObject(pref.getString(Config.COMPANY_INFO, ""));
            //  user.setHasMultipleVehicle("" + jArray.length());
            //   user.setHasMultipleVehicle(compInfo.getString("vehicle_qty_type"));
            user.setIsOwner(driverInfo.getString(Keys.IS_OWNER));
            user.setId(info.getString(Keys.KEY_ID));
            user.setEmail(info.getString(Keys.EMAIL));
            user.setProfileImage(pref.getString(Keys.KEY_IMAGE, ""));
            user.setUserName(info.getString(Keys.USERNAME));
            user.setFirstName(info.getString(Keys.FIRST_NAME));
            user.setLaseName(info.getString(Keys.LAST_NAME));
            // user.setAdminApprovalStatus(info.getString(Keys.ADMIN_APPROVAL_STATUS));
            user.setAdminApprovalStatus(pref.getString(Keys.ADMIN_APPROVAL_STATUS, ""));
            user.setLogin(pref.getBoolean(Keys.IS_LOGIN, false));
            user.setHas_unread_notifications(info.getString(Keys.HAS_UNREAD_NOTIFICATIONS));

            user.setIsprofileCompleted(info.getString(Keys.IS_PROFILE_COMPLETED));
            user.setIslaststep(info.getString(Keys.IS_LAST_STEP));
            user.setBidderID(info.getString(Keys.BIDDER_ID));
            user.setDeviceOS(info.getString(Keys.DEVICE_OS));
//            user.setDriverRating(info.getString(Keys.DRIVER_RATING));
//            user.setShipperRating(info.getString(Keys.SHIPPER_RATING));
            user.setBankDetail(pref.getString(Config.BANK_DETAILIN, ""));
            user.setCreditCard(pref.getString(Config.CREDIT_CARDIN, ""));
            user.setDriverRating(pref.getString(Config.DRIVER_RATING, ""));
            user.setShipperRating(pref.getString(Config.SHIPPER_RATING, ""));
            user.setReferCode(info.getString(Keys.REFER_CODE));
            user.setUsedReferCode(info.getString(Keys.USED_REFER_CODE));
            //    user.setVerifiedStatus(info.getString(Keys.VERIFIED_STATUS));
            user.setVerifiedStatus(pref.getString(Keys.VERIFIED_STATUS, ""));
            user.setUserType(info.getString(Keys.USER_TYPE));
            user.setMobile(info.getString(Keys.MOBILE));
            user.setGender(info.getString(Keys.GENDER));
            user.setDob(info.getString(Keys.DATE_OF_BIRTH));
            user.setRegistergst(info.getString(Keys.REGISTER_GST));
            if (info.has(Keys.APP_TOKEN))
                user.setApp_token(info.getString(Keys.APP_TOKEN));


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }

    public void saveTempUserData(String token) {
        editor.putString(Config.TEMP_USER_DATA, token);
        editor.commit();
    }

    public String getTempUserData() {
        return pref.getString(Config.TEMP_USER_DATA, "");
    }

    public void saveTempToken(String token) {
        editor.putString(Config.TEMP_TOKEN, token);
        editor.commit();
    }

    public String getTempToken() {
        return pref.getString(Config.TEMP_TOKEN, "");
    }


    public void saveToken(String token) {
        editor.putString(Config.TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(Config.TOKEN, "");
    }

    public void saveAvailability(String status) {
        editor.putString(Keys.AVAILABLE_STATUS, status);
        editor.commit();
    }

    public String getAvailability() {
        return pref.getString(Keys.AVAILABLE_STATUS, "");
    }

    public void saveIP(String IP) {
        editor.putString(Keys.KEY_IP, IP);
        editor.commit();
    }

    public String getIP() {

        return pref.getString(Keys.KEY_IP, Config.BASE_URL);
    }

    public void saveGCMKey(String key) {
        editor.putString(Keys.GCM_KEY, key);
        editor.commit();
    }

    public String getGCMKey() {
        return pref.getString(Keys.GCM_KEY, "");
    }

    public String getPosition() {
        return pref.getString(Keys.POSITION, "");
    }


    public void saveCurrentScreen(String key) {
        editor.putString(Keys.CURRENT_SCREEN, key);
        editor.commit();
}

    public String getCurrentScreen() {
        return pref.getString(Keys.CURRENT_SCREEN, "");
    }

    public String getProfileImage() {
        return pref.getString("profile_photo", "");
    }

    public String getLicenceImage() {
        return pref.getString("licence_photo", "");
    }

    public void saveNotificationID(String key) {
        editor.putString(Keys.NOTIFICATION_ID, key);
        editor.commit();
    }

    public String getNotificationID() {
        return pref.getString(Keys.NOTIFICATION_ID, "");
    }

    public void saveReadStatus(String status) {
        editor.putString(Keys.READSTATUS, status);
        editor.commit();
    }

    public String getReadStatus() {
        return pref.getString(Keys.READSTATUS, "");
    }

    public void setLocation(Boolean status) {
        editor.putBoolean(Keys.FINE_LOCATION, status);
        editor.commit();
    }

    public boolean getLocation() {
        return pref.getBoolean(Keys.FINE_LOCATION, false);
    }

    public void setCamera(Boolean status) {
        editor.putBoolean(Keys.CAMERA, status);
        editor.commit();
    }

    public boolean getCamera() {
        return pref.getBoolean(Keys.CAMERA, false);
    }


}

package com.bookmyride.api;

/**
 * Created by vinod on 1/5/2017.
 */
public interface Config {
    String TAG = "bookmyride";

        //String BASE_URL = "/api/web/index.php/v1/";/Ride247V2/html/
    String BASE_URL = "/bookmyride/html/api/web/index.php/v1/";

    String SOCIAL_LOGIN = BASE_URL + "users/social-login";
    String FORGOT_PASSWORD = BASE_URL + "users/temp-password";
    String LOCATION = BASE_URL + "locations";
    String CREATE_USER = BASE_URL + "users";
    String SEND_INVOICE = CREATE_USER + "/send-invoice";
    String REPORT_ISSUE = CREATE_USER + "/send-report";
    String VALIDATE_USER = BASE_URL + "users/validateuser";
    String LOGIN_URL = BASE_URL + "users/login";
    String UPGRADE_USER = BASE_URL + "users/upgrade-user";
    String ACTIVATE_PROFILE = BASE_URL + "users/active-profile?login_by=";
    String CAR_TYPE = BASE_URL + "vehicle-types";
    String VEHICLE_LIST = BASE_URL + "vehicle-types";
    String MAKER_LIST = BASE_URL + "brands";
    String MODEL_LIST = BASE_URL + "car-models";
    String GET_PROFILE = BASE_URL + "profiles/";
    String BOOKING_REQUEST = BASE_URL + "bookings";
    String DRIVER_AVAILABLITY = BASE_URL + "onlines/";
    String ACCEPT_BOOKING = BASE_URL + "booking-accepts/";
    String BOOKING_STATUS = BASE_URL + "booking-statuses/";
    String LOGOUT = BASE_URL + "users/logout";
    String DEACTIVATE = BASE_URL + "users/deactivate?id=";
    String BOOKING_LIST = BASE_URL + "bookings";
    String DRIVERS_LIST = BASE_URL + "profiles/get-drivers";
    String FAVOURITE_DRIVERS_LIST = BASE_URL + "rating/getfavourite";
    String ADD_FAVOURITE = BASE_URL + "rating/do-favourite";
    String REMOVE_FAVOURITE = BASE_URL + "rating/un-favourite";
    String CREDIT_CARD = BASE_URL + "credit-cards";
    String CHANGE_PASSWORD = BASE_URL + "users/changepassword";
    String ESTIMATE_RIDE = BASE_URL + "estimates";
    String CARD_TYPES = BASE_URL + "credit-cards/type";
    String BANK_DETAIL = BASE_URL + "bank-accounts";
    String SERVICE_AVAILABLES = BASE_URL + "service-availables";
    String PAYMENT_BY_CARD = BASE_URL + "e-payments";
    String ACCEPT_BY_CASH = BASE_URL + "e-payments";
    String CHECK_AVAILABLE_CARD = BASE_URL + "booking/is-saved-card?userId=";
    String CHECK_BACK_ACCOUNT = BASE_URL + "users/bank-account-detail";
    String DRIVER_GEO = BASE_URL + "bookings/";
    String UPDATE_DRIVER_GEO = BASE_URL + "users/update-location";
    String REFERRAL = BASE_URL + "referrals";
    String PAYMENT_STATUS = BASE_URL + "transaction";
    String EARNING = BASE_URL + "earning";
    String PAID = BASE_URL + "earning/paid";
    String DUE = BASE_URL + "earning/due";
    String REDEEM_REFERRAL_AMOUNT = BASE_URL + "referrals";
    String CANCEL_BOOKING = BASE_URL + "bookings/cancel/";
    String BOOKING_CHARGES = BASE_URL + "bookings/cancel-charge/";
    String RATING = BASE_URL + "ratings";
    String GUARANTEE_DRIVER = BASE_URL + "bookings/guarantee-driver/";
    String GUARANTEE_REQUEST = BASE_URL + "bookings/guarantee-request/";
    String EMERGENCY_CONTACT = BASE_URL + "emergency-contacts";
    String SEND_OTP = BASE_URL + "users/verify-number";
    String COUNTRY_LIST = BASE_URL + "countries";
    String WALLET = BASE_URL + "wallets";
    String TRANSACTIONS = BASE_URL + "transaction/get-transactions";
    String WALLET_HISTORY = TRANSACTIONS;
    String SEND_MESSAGE_DRIVER = BASE_URL + "messages";
    String RECALL = BASE_URL + "recall";
    String WAIT_TIME_NOTIFICATION = BASE_URL + "booking-statuses/wait-time";
    String FLEET = BASE_URL + "fleet/get-driver-fleet?cat_id=";
    String AVAILABLE_FLEET = BASE_URL + "vehicle/fleets";
    String NOTIFICATION = BASE_URL + "notification";
    String SHARE_CODE = BASE_URL + "users/sharecode";

    //String DISTANCE_BY_GOOGLE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&travelMode=DRIVING&origins=";
    String DISTANCE_BY_GOOGLE = "https://maps.googleapis.com/maps/api/directions/json?sensor=false&mode=DRIVING&key=AIzaSyDxBqk-VhEklzJyUW3cQ1PE9tsbFQQPYWo&origin=";
    String GetAddressFrom_LatLong_url = "https://maps.googleapis.com/maps/api/place/details/json?key=AIzaSyDxBqk-VhEklzJyUW3cQ1PE9tsbFQQPYWo&placeid=";
    //String SEND_OTP =  "http://52.65.245.81/v2/v3/app/send-otp-driver";
    String guarantee_job_find_driver_url = BASE_URL + "v3/api/v4/app/guarantee/find-driver";
    String delete_ride_url = BASE_URL + "app/delete-ride";



    //FCM
    int NOTIFICATION_ID = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;

    String TOPIC_GLOBAL = "global";
    String REGISTRATION_COMPLETE = "registrationComplete";
    String PUSH_NOTIFICATION = "pushNotification";

    String GET_WALLET = CREATE_USER + "/wallet-amount";
    String RECHARGE_WALLET = PAYMENT_BY_CARD + "/add-to-wallet";

    double PI = 3.14159265;

    String PLACE_API = "AIzaSyDxBqk-VhEklzJyUW3cQ1PE9tsbFQQPYWo";

    String CONFIRM_BOOKING = BASE_URL + "booking/pre-booking-confirmed";
    String DECLINED_BY_DRIVER = BASE_URL + "bookings/cancel-by-driver/";
    String GET_TWILLIO_NUMBER = BASE_URL + "twilio?ride_id=";
}

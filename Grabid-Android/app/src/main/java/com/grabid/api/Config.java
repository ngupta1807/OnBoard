package com.grabid.api;

/**
 * Created by vinod on 10/14/2016.
 */
public interface Config {
    //Chauffer
    String JOB = "job";
    String TAG = "GRABID";
    String BASE_URL = "https://www.grabid.com.au";
    //String BASE_URL = "http://alt.grabid.com.au";

    String PREF_NAME = "grabit_prefs";
    String MESSAGE = "message";
    String DATA = "data";
    String CREDITCARD = "Creditcard";
    String FIELD = "field";
    String PAYMENT = "payment";
    String BANKDETAIL = "Bankdetail";
    String IS_LOGIN = "isLoggedIn";
    String STATUS = "status";
    String VHICLE = "vehicle";
    String USER = "user";
    String DRIVER_PROFILE = "driverProfile";
    String COMPANY = "company";

    //Session
    String CONSUMER_ID = "consumer_id";
    String EMAIL = "email";
    String TOKEN = "auth_key";
    String TEMP_TOKEN = "temp_auth_key";
    String TEMP_USER_DATA = "temp_user_data";
    String FIRSTNAME = "firstName";
    String LASTNAME = "lastName";
    String SEX = "sex";
    String DATE_OF_BIRTH = "dob";
    String USER_NAME = "username";
    String PHONE = "phone";
    String FULLNAME = "fullName";
    String USER_ID = "id";
    String PROFILE = "profile";
    String PROFILE_PIC = "image";
    String HAS_PREFERENCES = "has_categories";

    String IP = "";

    //String SERVER_URL = "/Grabit/wwwroot/api/v1/";
    String SERVER_URL = "/api/web/v1/";
    String GET_BARCODES = "delivery-doc/barcode?code=";
    String PREFERENCES = "preferences";
    String CREATE_USER = "users";
    String LOGIN = "users/login";
    String GOOGLE_LOGIN = "user/login-google";
    String FACEBOOK_LOGIN = "user/login-fb";
    String SOCIAL_LOGIN = "users/social-login";
    String FORGOT_PASSWORD = "users/forgot";
    String CHANGE_PASSWORD = "users/changepassword";
    String LOGOUT = "users/logout";
    String FAQ = "faq?expand=faqlist";
    String FAQCHAUFFER = "faq/term?app=2";
    String LICENCE_TYPE = "licencetype";
    String VEHICLE_TYPE = "vehicletype";
    String userprofile = "user/profile-view?id=";
    String COUNTRY = "country";
    String STATE = "states";
    String DELIVERY_TYPE = "deliverytype";
    String DELIVERIES = "deliveries";
    String DURATION = "delivery-doc/duration";
    String JOBS = "jobs";
    String DELIVERY_DOCS = "delivery-docs";
    String DELIVERY_POD = "delivery-doc/pod";
    String DELIVERY_STATUS_IN_TRANSIT = "/delivery-status-in-transit";
    String DELIVERY_STATUS_IN_COMPLETE = "/delivery-status-complete";
    String CHOOSE_BID = "delivery/choose-bid?id=";
    String CHOOSE_JOB_BID = "job/choose-bid/?id=";
    String DELIVERY = "delivery";
    String FAVUSER = "favuser";
    String FAVUSERS = "favusers";
    String DELIVERYRELIST = "delivery/relist";
    String SEARCH = "/search";
    String BOOKMARK = "bookmark";
    String BID = "bid";
    String BID_ON_JOB = "bid/bid-on-job";
    String BIDS = "bids";
    String DELIVERY_FUTURE = "delivery/future";
    String DELIVERY_PAST = "delivery/past";
    String DELIVERY_INTRANSIT = "delivery/in-transit";
    String DELIVERY_CANCELLED = "delivery/cancelled";
    String BANK_DETAIL = "bankdetails";
    String COLLECT_DETAIL = "payment/collect";
    String CREDIT_CARD = "creditcards";
    String VELIDATE_VEHICLE_DATA = "bid/validate-vehicle";
    String AVAILABILITY = "users/availability";
    String DRIVERTRANSACTIONS = "payment/driver-transactions";
    String AVAILABILITY_CHANGE = "users/availabilitychange";
    String EQUIPMENT_TYPE = "equipment-type";
    String CURRENCY = "currency";
    String DELIVERY_TRACK = "delivery/track";
    String SHIPPER_FUTURE = "delivery/shipper-future";
    String SHIPPER_PAST = "delivery/shipper-past";
    String SHIPPER_INTRANSIT = "delivery/shipper-intransit";
    String SHIPPER_PENDING = "delivery/shipper-pending";
    String SHIPPER_CANCELLED = "delivery/shipper-cancelled";
    String CANCEL_DELIVERY = "/delivery-cancel?delivery_id=";
    String CANCEL_DELIVERYUPCOMING = "/delivery-cancel";
    String JOB_CANCEL = "/job-cancel";
    String PAYMENT_PAY = "payment/pay";
    String UPDATE_LOCATION = "user/update-location";
    String SHIPPER = "home/shipper";
    String DELIVERY_HOME = "deliveryhome/home";
    String UPDATE_DEVICE = "user/update-device";
    String NOTIFICATIONS = "notifications";
    String READ_NOTIFICATION = "read-notification";
    String NOTIFICATION = "notification";
    String NOTIFICATION_DELETE = "notification/delete";
    String FEEDBACKS = "feedbacks";
    String FEEDBACK = "feedback";
    String GET_FAVOURITIES = "favourite-user";
    String GET_FAVOURITE_USERS_GROUP = "favourite-user/user-group-list/?user_id=";
    String GET_FAVOURITEGROUPS = "favourite-user/all-user-groups/?expand=favusers";
    String GET_FAVOURITE_GROUP_LIST = "favourite-user/group-list";
    String CREATE_FAV_USERGROUP = "favourite-user/create-user-group";
    String RENAME_FAV_GROUP = "favourite-user/update-user-group";
    String ADD_FAVOURITE = "favourite-user/add-user";
    String ADD_FAVOURIE_PARICULAR_GROUP = "favourite-user/add-user-to-group";
    String FAVOURITE_USER_GROUP_DETAILS = "favourite-user/find-user-group/?expand=favusers&id=";
    String VALIDATE_VEHICLE = "vehicle/isowner";
    String VALIDATE_VEHICLE_API = "vehicle/validate-vehicle";
    String COMPANY_DATA = "user/update-step-two";
    String PROFILE_DATA = "user/update-step-one";
    String DRIVER_DATA = "user/update-step-three";
    String REMOVE_FAVOURITE = "favourite-user/remove-user";
    String REMOVEFAVOURIE_GET = "favourite-user/remove-users-from-group/?id=";
    String REMOVE_USER_FROM_GROUP = "favourite-user/remove-users-from-group";
    String REMOVE_USER_GROUP = "favourite-user/delete-user-group/?id=";
    String SEARCH_FAVOURITIES = "favourite-user/search-user?username=";
    String FEEDBACK_TO_SHIPPER = "feedback/feedback-to-shipper";
    String FEEDBACK_SHIPPER = "feedback/shipper";
    String VEHICLE_MANUFACTURE = "vehicle-manufacturers";
    String transaction_details = "/payment/driver-transactions";
    String VEHICLE = "vehicles";
    String PAYMENT_SHIPPER = "payment/shipper";
    String BOOKMARKS = "bookmarks";
    String VALIDATE_NEW_USER = "users/validate-first-page";
    String SHAREREFERRAL = "users/sharecode";
    String OTP = "user/mobile-varify";
    String FAQTERM = "faq/term";
    String VALIDATE_UPDATE_USER = "users/validate-update-first-page";
    String USER_INFO = "userInfo";
    String DRIVER_INFO = "driverInfo";
    String COMPANY_INFO = "companyInfo";
    String VEHICLE_INFO = "vehicleInfo";

    String ImgURL = "http://52.63.139.32/frontend/web/images/uploads/driver_profile/";
    //   String DeliveryImgURL = "http://dev.grabid.com.au/frontend/web/images/uploads/delivery/";
    String DeliveryImgURL = Config.BASE_URL + "/frontend/web/images/uploads/delivery/";

    String BANK_UPCOMING = "payment/driver-upcoming";
    String BANK_HISTORY = "payment/driver-completed";
    String CARD_UPCOMING = "payment/shipper-upcoming";
    String CARD_HISTORY = "payment/shipper-completed";
    String CARD_PENALTY = "payment/penality";

    String PROFILE_VIEW = "/profile-view?id=";

    String SUITABLE_VEHICLE = "suitable-vehicle";
    String LIFTING_EQUIPMENT = "lifting-equipment";

    String REFERRAL_ALL = "referral/earning";
    String REFERRAL_PAID = "referral/paid-referral";
    String REFERRAL_UNPAID = "referral/unpaid-current-month";
    String CASH_OUT = "referral/cash-out-earning";
    String TAKE_NOW = "take-now";
    String SHIPPER_RATING = "shipper_rating";
    String DRIVER_RATING = "driver_rating";
    String COUNT = "count";
    String BANK_DETAILIN = "bank_detail";
    String CREDIT_CARDIN = "credit_card";

    //Added by VK
    String ACCEPT_ALLOCATION = SERVER_URL + "delivery-doc/allocation-accept";
    String REJECT_ALLOCATION = SERVER_URL + "delivery-doc/allocation-reject";
    String GET_AVAILABLE_DRIVERS = SERVER_URL + "delivery-doc/driver-list?id=";
    String ALLOCATE_DRIVER = SERVER_URL + "delivery-doc/driver-allocation";
    String IS_ABLE_TO_ALLOCATE_DRIVER = "is_able_to_allocate_driver";
    String ALLOCATE_DRIVER_ID = "allocated_driver_id";
    String ALLOCATION_STATUS = "allocation_status";
    //VK end

    // global topic to receive app wide push notifications
    String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    String REGISTRATION_COMPLETE = "registrationComplete";
    String PUSH_NOTIFICATION = "pushNotification";
    // id to handle the notification in the notification tray
    int NOTIFICATION_ID = 100;
    int NOTIFICATION_ID_BIG_IMAGE = 101;

    String SHARED_PREF = "ah_firebase";
    String DELIVREY_ROUTE = "delivery/delivery-route";
    String PICK_UP_DETAIL = "pick_up_detail";
    String DROP_OFF_DETAIL = "drop_off_detail";
    String PREVIOUSDETAIL = "previous_detail";
    String CURRENT_DETAIL = "current_detail";
    String ROUTE_LOCATION = "route_location";
    String LATITUDE = "latitude";
    String LONGITUDE = "longitude";
    String ITEM_TITLE = "item_delivery_title";
    String DRIVER_AMOUNT = "delivery/driver-amount";
    String ITEM_PICK_UP = "from_pick_up_at";
    String ITEM_DROP_OFF = "completed_at";
    String READ_STATUS = "/read-status";
    String ASAP = "pick_up_time_type";
    String ROUNDTRIP = "delivery_type_id";
    String ITEMTYPE = "item_type";
    String GETPANALITY = SERVER_URL + "delivery-doc/penalty-amount?id=";
    String SEATCHECK = "suitable-vehicle/seat-check";
}

package com.grabid.api;

public interface Keys {

    String KEY_SUB_PREFERENCES = "sub_cat";
    boolean IsCreditCard = false, IsBankInfo = false;
    //For My Store List

    //Chauffer Keys
    String CERTIFICATE_IMAGE = "driver_certificate_image";
    String CERTIFICATE_VALIDATE = "driver_certificate_validity";
    String CERTIFICATE_NO = "driver_certificate_number";
    String JOB_TYPE = "job_type";
    String ASAP = "asap";
    String ROUNDTRIP = "round_trip";
    String TOTALPASSENGER = "total_passenger";
    String PAYMENT_MODE = "pay_mode_id";
    String BID_DATE_TIME1 = "bid_datetime1";
    String BID_DATE_TIME2 = "bid_datetime2";
    String COMPANY_LOGO = "company_logo";
    String ID = "id";
    String FAVOURITE_USER_ID = "fav_user_id";
    String PICK_UPBARCODE = "pickupbarcode";
    String DROP_OFFBARCODE = "dropoffbarcode";
    String DURATION = "duration";
    String CREDITCARDDETAIL = "creditCardDetail";
    String PAYMENT_TYPE = "payment_type";
    //Chauffer
    String ITEM_TYPE = "item_type";
    String USER_GROUP = "user_group";
    String PICK_UP_TIME_TYPE = "pick_up_time_type";
    String DELIVERY_TYPE_ID_ROUND_TRIP = "delivery_type_id";
    String job_PICK_UP_DATE_TIME = "job_datetime";
    String PICK_UP_MOBILE_VIRTUAL = "pick_up_mobile_virtual";
    //
    String KEY_STORE_ID = "storeID";

    String KEY_IMAGE = "profile_image";
    String DRIVER_LIMAGE = "driverlimage";
    String KEY_EMAIL = "email";
    String BANK_DETAIL = "Bankdetail";
    String CREDIT_CARD = "creditCardDetail";
    String IS_PROFILE_COMPLETED = "is_profile_completed";
    String IS_LAST_STEP = "last_step";

    //VERSION 2
    String IS_OWNER = "is_owner";
    String KEY_ID = "id";
    String KEY_JOB_ID = "job_id";
    String ISFAVOURITE_USER = "is_favourite_user";
    String LIFT_EQUIPMENT = "pick_up_lifting_equipment";
    String DROP_EQUIPMENT = "drop_off_lifting_equipment";
    String KEY_NAME = "name";
    String KEY_ICON = "icon";
    String KEY_USER_ID = "user_id";
    String KEY_IS_SELECTED = "is_select";
    String DELIVERY_ITEMS = "delivery_items";
    String SUITABLE_VEHICLE_TEXT = "suitable_vehicle_text";
    String APP_ID = "app_id";
    //WALLET IDs
    String CARD_NUMBER = "card_number";
    String CARD_TYPE = "card_type";
    String NAME_ON_CARD = "name_on_card";
    String EXPIRY = "card_expiry_date";
    String CVV = "cvv";
    String CARD_TOKEN = "card_token";

    String BANK_NAME = "bank_name";
    String HOLDER_NAME = "account_name";
    String ACCOUNT_NUMBER = "account_number";
    String BRANCH_CODE = "routing_number";
    String SWIFT_CODE = "swift_code";
    String HASH_ACCOUNT_NUMBER = "hash_account_number";

    //IP
    String KEY_IP = "ip";
    String POSITION = "position";
    String KEY_ORDER_ID = "order_id";

    String COUNTRY_ID = "country_id";
    String MOBILE = "mobile";
    String APP_TOKEN = "app_token";

    // GCM
    String GCM_KEY = "gcm_key";

    String STATUS = "status";


    String DELIVERY_STATUS = "delivery_status";
    String DELIVERY_ID = "delivery_id";
    String DELIVERY_TYPE_NAME = "delivery_type_name";
    String DRIVER_ID = "driver_id";
    String BID_ID = "bid_id";
    String RELIST_NOTIFICATION = "relist_notification_sent";
    String COMPLETED_AT = "completed_at";
    String DROPOFF_DATE = "drop_off_day";
    String PICKUP_DATE = "pick_up_day";
    String ITEM_PHOTO = "item_photo";
    String USER_TIME = "usertime";
    String RECEIVER_NAME = "receiver_name";
    String RECEIVER_SIGN = "receiver_sign";
    String FROM_PICKUP_NAME = "from_pick_up_name";
    String FROM_PICKUP_SIGN = "from_pick_up_sign";

    String AUCTION_BID = "auction_bid";
    String DOCTYPE = "doc_type";
    String DOCNAME = "doc_name";
    String AUCTION_START_TIME = "auction_start_time";
    String AUCTION_END_TIME = "auction_end_time";
    String FIXED_OFFER = "fixed_offer";
    String PICKUP_DAY = "pick_up_day";
    String PICKUP_DAY_TYPE = "pick_up_time_type";
    String PICKUP_END_DAY = "pick_up_end_time";
    String PICKUP_COUNTRY = "pick_up_country";
    String PICKUP_STATE = "pick_up_state";
    String PICKUP_CITY = "pick_up_city";
    String PICKUP_DATETIME = "puDateTimeChoose";

    String PICKUP_ADDRESS = "pick_up_address";
    String PICKUP_CONTACT_PERSON = "pick_up_contact_person";
    String PICKUP_MOBILE = "pick_up_mobile";
    String PICKUP_PHONE = "pick_up_phone";
    String PICKUP_BUILD_TYPE = "pick_up_building_type";
    String PICKUP_LIFT_EQUIPMENT = "pick_up_lifting_equipment";
    String PICKUP_BUILD_COMPANYNAME = "building_name";
    String DROPOFF_BUILD_COMPANYNAME = "building_name_drop";
    String DROPOFF_DAY = "drop_off_day";
    String DROPOFF_DAY_TYPE = "drop_off_time_type";
    String DROPOFF_END_DAY = "drop_off_end_time";
    String DROPOFF_COUNTRY = "drop_off_country";
    String DROPOFF_DATETIME = "doDateTimeChoose";
    String DROPOFF_STATE = "drop_off_state";
    String DROPOFF_CITY = "drop_off_city";
    String DROPOFF_ADDRESS = "drop_off_address";
    String DROPOFF_CONTACT = "drop_off_contact_person";
    String DROPOFF_MOBILE = "drop_off_mobile";
    String DROPOFF_PHONE = "drop_off_phone";
    String DROPOFF_BUILD_TYPE = "drop_off_building_type";
    String DROPOFF_INDUCTION_REQUIRE = "drop_off_induction_required";
    String DROPOFF_SPECIAL_RESTRICTION = "drop_off_any_special_restrictions";
    String PICKUP_INDUCTION_REQUIRE = "pick_up_induction_required";
    String PICKUP_SPECIAL_RESTRICTION = "pick_up_any_special_restrictions";
    String FAVOURITE_USER_GROUP_IDS = "fav_user_groups_id";

    String PICKUP_LIFT_EQUIP_AVAILABLE = "pick_up_lifting_equipment_available";
    String PICKUP_LIFT_EQUIP_NEEDED = "pick_up_lifting_equipment_needed";
    String DROPOFF_LIFT_EQUIP_AVAILABLE = "drop_off_lifting_equipment_available";
    String DROPOFF_LIFT_EQUIP_NEEDED = "drop_off_lifting_equipment_needed";
    String SUITABLE_VEHICLE = "suitable_vehicle";

    String DROPOFF_LIFT_EQUIPMENT = "drop_off_lifting_equipment";
    String DROPOFF_CALL = "drop_off_call_before_delivery";
    String DROPOFF_APPOINTMENT = "drop_off_appointment_required";
    String ITEM_DELIVERY_TITLE = "item_delivery_title";

    String PICKUP_LATITUDE = "pick_up_latitude";
    String PICKUP_LONGITUDE = "pick_up_longitude";
    String DROPOFF_LATITUDE = "drop_off_latitude";
    String DROPOFF_LONGITUDE = "drop_off_longitude";
    String DELIVERY_TYPE_ID = "delivery_type_id";

    String DELIVERY_ITEM = "delivery_item";
    String GEO_ZONE = "geo_zone";
    String RADIUS = "radius";
    String BID_STATUS = "bid_status";
    String DISTANCE = "distance";
    String DRIVER_DISTANCE = "driver_distance";
    String DRIVER_DETAIL = "driver_detail";
    String TITLE = "title";
    String ITEM_DELIVERY_TYPE_SUB_ID = "item_delivery_type_sub_id";
    String ITEM_DELIVERY_TYPE_SUB_NAME = "item_delivery_type_sub_name";
    String ITEM_LENGTH = "item_length";
    String ITEM_WIDTH = "item_width";
    String ITEM_HEIGHT = "item_height";
    String ITEM_STACKABLE = "item_stackable";
    String ITEM_HAZARDOUS = "item_hazardous";
    String ALLOCATED_DEIVER_DETAIL = "allocated_driver_detail";

    String ITEM_EQUIPMENT_TYPE_ID = "item_equipment_type_id";
    String ITEM_EQUIPMENT_TYPE_NAME = "item_equipment_type_name";
    String ITEM_QTY = "item_qty";
    String ITEM_QUANTITY = "item_quantity";
    String ITEM_MORE = "item_more_details";
    String ITEM_ANIMAL_NAME = "item_animal_name";
    String ITEM_ANIMAL_BREED = "item_animal_breed";
    String ITEM_WEIGHT = "item_weight";
    String ITEM_CURRENT_VACCINATIONS = "item_current_vaccinations";
    String ITEM_ANIMAL_CARRIER = "item_animal_carrier";
    String SHIPPER_DETAIL = "shipper_detail";
    String ITEM_DELIVERY_TYPE_SUB_SUB_ID = "item_delivery_type_sub_sub_id";
    String ITEM_DELIVERY_TYPE_SUB_SUB_NAME = "item_delivery_type_sub_sub_name";
    String ITEM_DELIVAR_OTHER = "item_delivar_others";
    String MAX_AUCTION_BID = "maximum_opening_bid";
    String BIDS = "bids";
    String BID_CHOOSED = "bid_choosed";
    String ANIMAL_TYPE = "";

    String GIVEN_BY = "given_by";
    String SENDER_ID = "sender_id";
    String RATING = "rating";
    String FEEDBACK = "feedback";
    String FEEDBACK_TO_SHIPPER = "feedback_to_shipper";
    String FEEDBACK_TO_DRIVER = "feedback_to_driver";
    String TYPE = "type";
    String DELIVERY_TITLE = "delivery_title";

    String USER_TYPE = "user_type";
    String PROFILE_IMAGE = "profile_image";
    String REFER_CODE = "refer_code";
    String DRIVER_RATING = "driver_rating";
    String DEVICE_OS = "device_os";
    String FIRST_NAME = "first_name";
    String USER_PAYMENT_MODES = "user_payment_modes";
    String IS_LOGIN = "is_login";
    String HAS_UNREAD_NOTIFICATIONS = "has_unread_notifications";
    String DROP_DIFFERENCE = "dropdifference";
    String PICK_UP_DIFFERENCE = "pickupdifference";
    String SHIPPER_RATING = "shipper_rating";
    String SHIPPER_NAME = "shipper_name";
    String USERNAME = "username";
    String BIDDER_ID = "bidder_id";
    String EMAIL = "email";
    String VERIFIED_STATUS = "verified_status";
    String LAST_NAME = "last_name";
    String FULL_NAME = "fullname";
    String GENDER = "gender";
    String TIME_ZONE_ID = "time_zone_id";
    String DATE_OF_BIRTH = "dob";
    String ADMIN_APPROVAL_STATUS = "admin_approval_status";
    String USED_REFER_CODE = "used_refer_code";
    String REGISTER_GST = "register_for_gst";
    String VEHICLE_TYPE = "vehicle_type";

    String OFFICE_NUMBER = "office_number";
    String ABN_NUMBER = "abn_number";
    String CONTACT_PERSON = "contact_person";
    String VEHICLE_IN_FLEET = "vehicle_in_fleet";
    String POSTALCODE = "postal_code";
    String VEHICLE = "vehicle";
    String STATE_NAME = "state_name";
    String COUNTRY_NAME = "country_name";
    String VEHICLE_INSURANCE_POLICY = "vehicle_insurance_policy";
    String FREIGHT_INSURANCE_POLICY = "freight_insurance_policy";
    String STATE_ID = "state_id";
    String VEHICLE_QTY = "vehicle_qty_type";
    String ADDRESS = "address";
    String SUBURB = "suburb";
    String COMPANY_MOBILE = "company_mobile";
    String NAME = "name";
    String SPECIAL_PERMIT_DETAIL = "special_permit_detail";

    String SPECIAL_PERMIT = "special_permit_required";
    String CREATED_AT = "created_at";
    String CREATED_BY = "created_by";
    String COMPANY_ID = "company_id";
    String UPDATED_BY = "updated_by";
    String DATE = "date";
    String COMMISION_AMOUNT = "commission_amount";
    String REGISTRATION_NUMBER = "registration_number";

    String AVAILABLE_STATUS = "availability_status";
    String INSURANCE_POLICY = "insurance_policy";
    String FREIGHT = "freight_insurance";
    String FREIGHT_INSURANCE_COVER = "freight_insurance_cover";
    String LICENCE_TYPE_ID = "licence_type_id";
    String LICENCE_NUMBER = "licence_number";
    String MEDICARE_NUMBER = "medicare_card_number";
    String MEDICARE_IMAGE = "medicare_card_image";
    String NATIONAL_IMAGE = "police_check_image";
    String LICENCE_VALID_TILL = "licence_valid_till";
    String LICENCE_IMAGE = "licence_image";
    String CAN_DRIVE_TAXI = "can_drive_taxi";
    String TAXI_DRIVER_LICENCE = "taxi_driver_licence";
    String TAXI_LICENCE_IMAGE = "taxi_licence_image";
    String CURRENT_LATITUDE = "current_latitude";
    String CURRENT_LONGITUDE = "current_longitude";
    String CITY = "city";

    String PAYMENT_AMOUNT = "payment_amount";
    String PAYMENT_METHOD = "payment_method";
    String PAYMENT_METHOD_NAME = "name";

    String AMOUNT = "amount";
    String AMOUNT_COLLECTED = "amount_collected";
    String TRANSACTION_ID = "transaction_id";
    String PAY_TO_DRIVER_AMOUNT = "pay_to_driver_amount";
    String PAY_TO_DRIVER_STATUS = "pay_to_driver_status";
    String PAY_TO_DRIVER_AT = "pay_to_driver_at";
    String PAID_ON = "paid_on";

    String CM_TOTAL_EARNING = "total_earning_current_month";
    String REFERRAL_ELIGIBLE_AMOUNT = "referral_fees_eligible_amount";


    String PAID_LOYALITY_AMOUNT = "total_paid_loyalty_amount";
    String AVL_LOYALITY_AMOUNT = "available_loyalty_amount";
    String PAID_REFFERAL_AMOUNT = "total_paid_referral_amount";
    String AVL_REFFERAL_AMOUNT = "available_referral_amount";
    String CASHOUT_BUTTON = "cashout_button";
    String DELIVERY_STATUS_TYPE = "delivery_status_type";
    String SUITABLE_VEHICAL_IDS = "suitable_vehicle_ids";


    String PICKUP_LIFT_EQUIP_AVAILABLE_IDS = "pick_up_lifting_equipment_available_ids";
    String DROPOFF_LIFT_EQUIP_AVAILABLE_IDS = "drop_off_lifting_equipment_available_ids";

    String PICKUP_LIFT_EQUIP_NEEDED_IDS = "pick_up_lifting_equipment_needed_ids";
    String DROPOFF_LIFT_EQUIP_NEEDED_IDS = "drop_off_lifting_equipment_needed_ids";

    String CURRENT_SCREEN = "currr_screen";
    String READ_STATUS = "read_status";
    String NOTIFICATION_ID = "_id";
    String READSTATUS = "readstatus";
    String PICKUP_LIFT_EQUIP_AVAILABLE_TEXT = "pick_up_lifting_equipment_available_text";
    String DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT = "drop_off_lifting_equipment_available_text";
    String PICKUP_LIFT_EQUIP_NEEDED_TEXT = "pick_up_lifting_equipment_needed_text";
    String DROPOFF_LIFT_EQUIP_NEEDED_TEXT = "drop_off_lifting_equipment_needed_text";
    //permission Keys
    String FINE_LOCATION = "fine_location";
    String CAMERA = "camera";
    String CATEGORYTYPE = "categorytype";
    String CATEGORYTITLE = "categorytitle";
    String CATEGORYDESCRIPTION = "categorydescription";


}
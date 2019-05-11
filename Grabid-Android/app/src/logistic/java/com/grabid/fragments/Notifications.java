package com.grabid.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.NotificationAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Delivery;
import com.grabid.models.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.grabid.common.AlertManager.messageDialog;

/**
 * Created by vinod on 10/14/2016.
 */
public class Notifications extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener {
    ListView list;
    NotificationAdapter adapter;
    SessionManager session;
    ArrayList<Notification> notificationsData = new ArrayList<Notification>();
    TextView noData;
    String notificationType = "";
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;
    String type = "notification";
    int position;
    Delivery deliveryData;
    int pos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.notification));
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.markread.setVisibility(View.VISIBLE);
        View view = inflater.inflate(R.layout.notification, null);
        init(view);
        //if (page == 1)
        page = 1;
        HomeActivity.markread.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                MarkAllRead();

            }
        });
        getNotifications();
        return view;
    }

    Fragment fragment;

    private void init(View view) {
        session = new SessionManager(getActivity());
        list = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        adapter = new NotificationAdapter(getActivity(), notificationsData);
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("NOTOIFICATION", "title:." + notificationsData.get(i).getTitle().toString()); // ||  (notificationsData.get(i).getType().toString().equals("62"))
                /*if ((notificationsData.get(i).getTitle().toString().contains("relist")) || (notificationsData.get(i).getTitle().toString().contains("has withdrawn from the delivery"))) {
                    //bundle.putString("data", notificationsData.get(i).getDeliveryID());
                    position = i;
                    getDelivery(notificationsData.get(i).getDeliveryID());
                    //fragment = new Shipment();
                } else {*/
                String type = notificationsData.get(i).getType();
                if (type != null && (type.contentEquals("33") || type.contentEquals("34") || type.contentEquals("63"))) {
                    pos = i;
                    changeNotificationStatus(notificationsData.get(i).getId());
                } else if (type != null && type.equals("66")) {
                    Intent intent;
                    intent = new Intent(getActivity(), HomeActivity.class);
                    intent.putExtra("message", "message");
                    intent.putExtra("type", "66");
                    intent.putExtra("creditcarddecline", "creditcarddecline");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();

                } else {
                    String backStateName = this.getClass().getName();
                    session.saveNotificationID(notificationsData.get(i).getId());
                    session.saveReadStatus(notificationsData.get(i).getReadStatus());
                    Bundle bundle = new Bundle();
                    bundle.putString("delivery_id", notificationsData.get(i).getDeliveryID());
                    bundle.putSerializable("incoming_type", "notification");
                    bundle.putString("notificationType", notificationsData.get(i).getDeliveryID());
                    fragment = new DeliveryInfo();
                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
                //  }

            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                firstVisibleItem = firstVisibleItemm;
                visibleItemCount = visibleItemCountt;
                totalItemCount = totalItemCountt;


            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (firstVisibleItem > 0 && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                    if (!loadingMore) {
                        if (totalCount >= page) {
                            loadingMore = true;
                            getNotifications();
                        }
                    }

                    //  new AsyncTask().execute();

                    //get next 10-20 items(your choice)items

                }
            }
        });
    }

    private void MarkAllRead() {
        type = "markallread";
        String url = Config.SERVER_URL + Config.NOTIFICATIONS + "/" + Config.READ_NOTIFICATION;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void getDelivery(String deliveryID) {
        type = "get";
        String url = Config.SERVER_URL + Config.DELIVERIES + "/" + deliveryID;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void changeNotificationStatus(String id) {
        type = "change_notification";
        Log.v("id:", "id:" + session.getNotificationID());
        String url = Config.SERVER_URL + Config.NOTIFICATION + Config.READ_STATUS + "?id=" + id;
        Log.d("url", url);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI;
            mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (type != null && type.equals("change_notification")) {
                    notificationsData.get(pos).setReadStatus("1");
                    adapter.notifyDataSetChanged();
                } else if (type != null && type.equals("markallread")) {
                    try {
                        new SessionManager(getActivity()).saveCount("");
                        HomeActivity.badge.hide();
                        String message = outJson.optString("message");
                        if (message != null && !message.contentEquals("")) {
                            if (message.contains("successfully")) {
                                //   messageDialog(getActivity(), "Success!", message);
                                page = 1;
                                getNotifications();

                            }// else
                            //  messageDialog(getActivity(), "Alert!", message);
                        }
                    } catch (Exception e) {
                        e.toString();
                    }

                } else {
                    JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                    if (type != null && type.equals("get")) {
                        try {
                            JSONObject deliveryObj = dataObj.getJSONObject(Config.DELIVERY);
                            //
                            Delivery delivery = new Delivery();
                            delivery.setPaymentAmount(deliveryObj.getString(Keys.PAYMENT_AMOUNT));
                            delivery.setBookmarked((deliveryObj.get(Config.BOOKMARK).toString().equals("null")) ? false : true);
                            delivery.setId(deliveryObj.getString(Keys.KEY_ID));
                            delivery.setBidStatus(deliveryObj.get(Keys.BID_STATUS).toString());
                            delivery.setTitle(deliveryObj.getString(Keys.ITEM_DELIVERY_TITLE));
                            delivery.setBidID(deliveryObj.getString(Keys.BID_ID));
                            delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                            delivery.setDropoffAdress(deliveryObj.getString(Keys.DROPOFF_ADDRESS));
                            delivery.setDriverID(deliveryObj.getString(Keys.DRIVER_ID));
                            delivery.setItemPhoto(deliveryObj.getString(Keys.ITEM_PHOTO));

                            //Added By VK
                            delivery.setIsAbleToAllocate(deliveryObj.getBoolean(Config.IS_ABLE_TO_ALLOCATE_DRIVER));
                            delivery.setAllocateDriverID(deliveryObj.get(Config.ALLOCATE_DRIVER_ID).toString());
                            delivery.setAllocationStatus(deliveryObj.get(Config.ALLOCATION_STATUS).toString());
                            //VK end

                            delivery.setObjData(deliveryObj.toString());
                            delivery.setCompletedAt(deliveryObj.getString(Keys.COMPLETED_AT));
                            delivery.setPickUpAddress(deliveryObj.getString(Keys.PICKUP_ADDRESS));
                            delivery.setUserID(deliveryObj.getString(Keys.KEY_USER_ID));
                            delivery.setPickUpDate(deliveryObj.getString(Keys.PICKUP_DATE));
                            delivery.setDropOffDate(deliveryObj.getString(Keys.DROPOFF_DATE));
                            delivery.setWidth(deliveryObj.getString(Keys.ITEM_WIDTH));
                            delivery.setStatus(deliveryObj.getString(Keys.STATUS));
                            delivery.setStackable(deliveryObj.getString(Keys.ITEM_STACKABLE));
                            delivery.setReceiver(deliveryObj.getString(Keys.RECEIVER_NAME));
                            delivery.setReceiverSign(deliveryObj.getString(Keys.RECEIVER_SIGN));
                            delivery.setSender(deliveryObj.getString(Keys.FROM_PICKUP_NAME));
                            delivery.setSenderSign(deliveryObj.getString(Keys.FROM_PICKUP_SIGN));
                            delivery.setQty(deliveryObj.getString(Keys.ITEM_QTY));
                            delivery.setWeight(deliveryObj.getString(Keys.ITEM_WEIGHT));
                            delivery.setPuMobile(deliveryObj.getString(Keys.PICKUP_MOBILE));
                            delivery.setPuContactPerson(deliveryObj.getString(Keys.PICKUP_CONTACT_PERSON));
                            delivery.setDoMobile(deliveryObj.getString(Keys.DROPOFF_MOBILE));
                            delivery.setPuLng(deliveryObj.getString(Keys.PICKUP_LONGITUDE));
                            delivery.setPuLat(deliveryObj.getString(Keys.PICKUP_LATITUDE));
                            delivery.setDoContactPerson(deliveryObj.getString(Keys.DROPOFF_CONTACT));
                            delivery.setDoLat(deliveryObj.getString(Keys.DROPOFF_LATITUDE));
                            delivery.setDoLng(deliveryObj.getString(Keys.DROPOFF_LONGITUDE));
                            delivery.setPuLiftEquipment(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIPMENT));
                            delivery.setAnimalName(deliveryObj.getString(Keys.ITEM_ANIMAL_NAME));
                            delivery.setBreed(deliveryObj.getString(Keys.ITEM_ANIMAL_BREED));
                            delivery.setPuBuildType(deliveryObj.getString(Keys.PICKUP_BUILD_TYPE));
                            delivery.setDoBuildType(deliveryObj.getString(Keys.DROPOFF_BUILD_TYPE));
                            delivery.setDoLiftEquipment(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIPMENT));
                            delivery.setFixedOffer(deliveryObj.getString(Keys.FIXED_OFFER));
                            delivery.setAuctionStart(deliveryObj.getString(Keys.AUCTION_START_TIME));
                            delivery.setAuctionEnd(deliveryObj.getString(Keys.AUCTION_END_TIME));
                            delivery.setAuctionBid(deliveryObj.getString(Keys.AUCTION_BID));
                            delivery.setGeo(deliveryObj.getString(Keys.GEO_ZONE));
                            delivery.setRadius(deliveryObj.getString(Keys.RADIUS));
                            delivery.setMaxOpeningBid(deliveryObj.getString(Keys.MAX_AUCTION_BID));
                            delivery.setDoAppoint(deliveryObj.getString(Keys.DROPOFF_APPOINTMENT));
                            delivery.setDoCall(deliveryObj.getString(Keys.DROPOFF_CALL));
                            delivery.setMore(deliveryObj.getString(Keys.ITEM_MORE));
                            delivery.setUser_Group(deliveryObj.getString(Keys.USER_GROUP));
                            try {
                                delivery.setRelistNotification(deliveryObj.getString(Keys.RELIST_NOTIFICATION));
                                delivery.setItem_delivery_other(deliveryObj.getString(Keys.ITEM_DELIVAR_OTHER));
                                delivery.setPickUpComName(deliveryObj.getString(Keys.PICKUP_BUILD_COMPANYNAME));
                                delivery.setDropOffComName(deliveryObj.getString(Keys.DROPOFF_BUILD_COMPANYNAME));
                            } catch (Exception e) {
                                e.toString();
                            }
                            if (!deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID).equals("null")) {
                                delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID));
                                delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_NAME));
                            } else {
                                delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_ID));
                                delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_NAME));
                            }
                            if (deliveryObj.get(Config.BOOKMARK).toString().equals("null")) {
                            } else {
                                delivery.setBookmarkID(deliveryObj.getJSONObject(Config.BOOKMARK).getString(Keys.KEY_ID));
                            }
                            try {
                                delivery.setFav_user_id(deliveryObj.getString(Keys.FAVOURITE_USER_GROUP_IDS));
                            } catch (Exception e) {
                                e.toString();
                            }
                            delivery.setDeliveryName(deliveryObj.getString(Keys.DELIVERY_TYPE_NAME));
                            delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                            delivery.setDeliveryTypeID(deliveryObj.getString(Keys.DELIVERY_TYPE_ID));
                            delivery.setDeliveryTypeSubSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID));
                            delivery.setDeliveryTypeSubSubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_NAME));
                            delivery.setCurrentVaccination(deliveryObj.getString(Keys.ITEM_CURRENT_VACCINATIONS));
                            delivery.setAnimalCarrier(deliveryObj.getString(Keys.ITEM_ANIMAL_CARRIER));
                            delivery.setShipperName(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                            delivery.setShipperImage(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                            delivery.setShipperRating(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                            delivery.setShipperID(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                            try {
                                if (deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).has(Keys.MOBILE))
                                    delivery.setSender_Mobile(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.MOBILE));
                            } catch (Exception e) {
                                e.toString();
                            }
                            delivery.setBidArray(deliveryObj.get(Keys.BIDS).toString());
                            delivery.setChoosedBidsArray(deliveryObj.get(Keys.BID_CHOOSED).toString());
                            delivery.setHasDriverFeedback(deliveryObj.get(Keys.FEEDBACK_TO_DRIVER).toString().equals("null") ? false : true);
                            delivery.setHasShipperFeedback(deliveryObj.get(Keys.FEEDBACK_TO_SHIPPER).toString().equals("null") ? false : true);
                            delivery.setItemsData(deliveryObj.get(Keys.DELIVERY_ITEMS).toString());
                            delivery.setSuitableVehicles(deliveryObj.get(Keys.SUITABLE_VEHICLE_TEXT).toString());
                            delivery.setDropoffDateType(deliveryObj.getString(Keys.DROPOFF_DAY_TYPE));
                            delivery.setPickupDateType(deliveryObj.getString(Keys.PICKUP_DAY_TYPE));
                            delivery.setDropOffEndDate(deliveryObj.getString(Keys.DROPOFF_END_DAY));
                            delivery.setPickupEndDate(deliveryObj.getString(Keys.PICKUP_END_DAY));
                            delivery.setPickupCountry(deliveryObj.getString(Keys.PICKUP_COUNTRY));
                            delivery.setPickupState(deliveryObj.getString(Keys.PICKUP_STATE));
                            delivery.setPickupCity(deliveryObj.getString(Keys.PICKUP_CITY));
                            delivery.setDropoffCountry(deliveryObj.getString(Keys.DROPOFF_COUNTRY));
                            delivery.setDropoffState(deliveryObj.getString(Keys.DROPOFF_STATE));
                            delivery.setDropoffCity(deliveryObj.getString(Keys.DROPOFF_CITY));
                            delivery.setPickupinductionRequire(deliveryObj.getString(Keys.PICKUP_INDUCTION_REQUIRE));
                            delivery.setPickupSpecialRestriction(deliveryObj.getString(Keys.PICKUP_SPECIAL_RESTRICTION));
                            delivery.setDropoffinductionRequire(deliveryObj.getString(Keys.DROPOFF_INDUCTION_REQUIRE));
                            delivery.setDropoffSpecialRestriction(deliveryObj.getString(Keys.DROPOFF_SPECIAL_RESTRICTION));
                            delivery.setSpecialPermit(deliveryObj.getString(Keys.SPECIAL_PERMIT));
                            delivery.setSpecialPermitDetail(deliveryObj.getString(Keys.SPECIAL_PERMIT_DETAIL));
                            delivery.setSuitabelVehicle(deliveryObj.getString(Keys.SUITABLE_VEHICAL_IDS));
                            try {
                                delivery.setPickUpLiftEquiAvailableIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_IDS));
                                delivery.setDropOffLiftEquiAvailableIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_IDS));
                                delivery.setPickUpLiftEquiNeededIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_IDS));
                                delivery.setDropOffLiftEquiNeededIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_IDS));
                                delivery.setPuLiftEquipmentText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT));
                                delivery.setPuLiftEquipmentNeededText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT));
                                delivery.setDoLiftEquipmentText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT));
                                delivery.setDoLiftEquipmentNeededText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT));
                                delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                                delivery.setCompleted_at(deliveryObj.getString("completed_at"));
                            } catch (Exception e) {
                                e.toString();

                            }
                            try {
                                delivery.setPickUpBarcode(deliveryObj.getString(Keys.PICK_UPBARCODE));
                                delivery.setDropOffBarCode(deliveryObj.getString(Keys.DROP_OFFBARCODE));
                            } catch (Exception e) {
                                e.toString();
                            }
                        /*delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                        delivery.setCompleted_at(deliveryObj.getString("completed_at"));*/
                            if (deliveryObj.getString(Keys.KEY_USER_ID).equals(new SessionManager(getActivity()).getUserDetails().getId())) {
                                delivery.setIsDriver(false);
                            } else {
                                delivery.setIsDriver(true);
                            }
                            try {
                                if (!delivery.getDeliveryStatus().equals("0")) {
                                    delivery.setDriver_name(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                                    delivery.setDriver_image(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                                    delivery.setDriver_rating(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                                    delivery.setDriver_id(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                                    delivery.setIsFavouriteUser(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.ISFAVOURITE_USER));
                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                            this.deliveryData = delivery;
                            //
                            String notificationType = deliveryObj.getString(Keys.RELIST_NOTIFICATION);
                            String backStateName = this.getClass().getName();
                            session.saveNotificationID(notificationsData.get(position).getId());
                            session.saveReadStatus(notificationsData.get(position).getReadStatus());
                            Bundle bundle = new Bundle();
                            bundle.putString("delivery_id", notificationsData.get(position).getDeliveryID());
                            bundle.putString("notificationType", notificationsData.get(position).getDeliveryID());
                            Log.v("NOTOIFICATION", "title:." + notificationsData.get(position).getTitle().toString()); // ||  (notificationsData.get(i).getType().toString().equals("62"))

                            if (notificationType != null && notificationType.equals("66")) {
                                Intent intent;
                                intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra("message", "message");
                                intent.putExtra("type", "66");
                                intent.putExtra("creditcarddecline", "creditcarddecline");
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                getActivity().finish();
                            }
                          /*  if ((notificationType != null && notificationType.equals("1") && !deliveryData.getDeliveryStatus().equals("4"))) {
                                //bundle.putString("data", notificationsData.get(i).getDeliveryID());
                                HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                                data.put("data", deliveryData);
                                bundle.putSerializable("data", data);
                                bundle.putString("incoming_type", "notificationshipment");
                                fragment = new Shipment();

                            } */
                            else {
                                bundle.putString("incoming_type", "notification");
                                fragment = new DeliveryInfo();
                            }
                            fragment.setArguments(bundle);
                            getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                                    .addToBackStack(null)
                                    .commitAllowingStateLoss();
                        } catch (Exception e) {
                            e.toString();
                        }
                    } else {
                        JSONArray feedbackArray = dataObj.getJSONArray(Config.NOTIFICATION);
                        if (page == 1)
                            notificationsData.clear();
                        ++page;
                        loadingMore = false;
                        for (int i = 0; i < feedbackArray.length(); i++) {
                            JSONObject feedbackObj = feedbackArray.getJSONObject(i);
                            Notification notification = new Notification();
                            notification.setId(feedbackObj.getString(Keys.KEY_ID));
                            notification.setUserID(feedbackObj.getString(Keys.KEY_ID));
                            notification.setDeliveryTitle(feedbackObj.getString(Keys.DELIVERY_TITLE));
                            notification.setTitle(feedbackObj.getString(Keys.TITLE));
                            notification.setMessage(feedbackObj.getString(Config.MESSAGE));
                            notification.setSenderID(feedbackObj.getString(Keys.SENDER_ID));
                            notification.setDeliveryID(feedbackObj.getString(Keys.DELIVERY_ID));
                            notification.setType(feedbackObj.getString(Keys.TYPE));
                            notification.setReadStatus(feedbackObj.getString(Keys.READ_STATUS));
                            notificationsData.add(notification);
                        }
                        adapter.notifyDataSetChanged();
                        if (notificationsData.size() == 0) {
                            list.setVisibility(View.GONE);
                            noData.setVisibility(View.VISIBLE);
                        } else {
                            list.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                        }
                        try {
                            if (dataObj.has("_meta")) {
                                JSONObject metaCount = dataObj.getJSONObject("_meta");
                                totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                                Log.v("totalcount", String.valueOf(totalCount));

                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    }
                }
            } else {
                messageDialog(getActivity(), "Alert!", "Error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            list.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

    }

    private void getNotifications() {
        if (page == 1)
            notificationsData.clear();
        type = "notification";
        String url = Config.SERVER_URL + Config.NOTIFICATIONS + "?page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }
}
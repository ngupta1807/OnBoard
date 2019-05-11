package com.grabid.fragments;

import android.app.Fragment;
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
import com.grabid.adapters.ListAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Delivery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vinod on 10/14/2016.
 */
public class SearchResult extends Fragment implements AsyncTaskCompleteListener {
    ListView list;
    ListAdapter adapter;
    String url;
    int totalCount = 1;
    int page = 2;
    SessionManager session;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    boolean loadingMore = false;
    TextView noData;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        View view = inflater.inflate(R.layout.search_result, null);
        if (getArguments() != null && getArguments().containsKey("data")) {
            deliveryData = (ArrayList<Delivery>) getArguments().getSerializable("data");
            url = getArguments().getString("url");
            totalCount = getArguments().getInt("totalcount");
            Log.v("url", url);
        }
        init(view);
        return view;
    }

    ArrayList<Delivery> deliveryData = new ArrayList<>();

    private void init(View view) {
        list = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        session = new SessionManager(getActivity());
        adapter = new ListAdapter(getActivity(), deliveryData, 0);
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
        if (deliveryData.size() == 0) {
            list.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String backStateName = this.getClass().getName();
                Bundle bundle = new Bundle();
                HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                data.put("data", deliveryData.get(i));
                bundle.putSerializable("data", data);
                bundle.putSerializable("incoming_type", "search");
                bundle.putSerializable("incoming_delivery_type", "");
                Fragment fragment = new DeliveryInfo();
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
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
                            getDeliveries();
                        }
                    }

                    //  new AsyncTask().execute();

                    //get next 10-20 items(your choice)items

                }
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void getDeliveries() {
        String urlc = url + "?page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(urlc, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void handleResponse(String result) {
        try {
            JSONObject outterJson = new JSONObject(result);
            if (Integer.parseInt(outterJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                try {
                    JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
                    JSONArray deliveryArray = dataObj.getJSONArray(Config.JOB);
                    ++page;
                    loadingMore = false;
                    for (int i = 0; i < deliveryArray.length(); i++) {
                        JSONObject deliveryObj = deliveryArray.getJSONObject(i);
                        Delivery delivery = new Delivery();
                        Log.v("data", deliveryObj.toString());
                        delivery.setPaymentAmount(deliveryObj.getString(Keys.PAYMENT_AMOUNT));
                        delivery.setBookmarked((deliveryObj.get(Config.BOOKMARK).toString().equals("null")) ? false : true);
                        delivery.setId(deliveryObj.getString(Keys.KEY_ID));
                        //  delivery.setLiftequipement(deliveryObj.getString(Keys.LIFT_EQUIPMENT));
                        //delivery.setAnimalName(deliveryObj.getString(Keys.ITEM_ANIMAL_NAME));
                        //delivery.setDliftequipement(deliveryObj.getString(Keys.DROP_EQUIPMENT));
                        delivery.setAsap((deliveryObj.get(Config.ASAP).toString().equals("1")) ? true : false);
                        delivery.setRoundTrip((deliveryObj.get(Config.ROUNDTRIP).toString().equals("1")) ? true : false);
                        delivery.setItemtype(deliveryObj.getString(Config.ITEMTYPE));
                        //delivery.setBreed(deliveryObj.getString(Keys.ITEM_ANIMAL_BREED));
                        delivery.setTitle(deliveryObj.getString(Keys.ITEM_DELIVERY_TITLE));
                        delivery.setBidID(deliveryObj.getString(Keys.BID_ID));
                        delivery.setBidStatus(deliveryObj.get(Keys.BID_STATUS).toString());
                        delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                        delivery.setDropoffAdress(deliveryObj.getString(Keys.DROPOFF_ADDRESS));
                        delivery.setDriverID(deliveryObj.getString(Keys.DRIVER_ID));
                        //delivery.setItemPhoto(deliveryObj.getString(Keys.ITEM_PHOTO));

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
                        //delivery.setDropOffDate(deliveryObj.getString(Keys.DROPOFF_DATE));
                        delivery.setPickupCountry(deliveryObj.getString(Keys.PICKUP_COUNTRY));
                        delivery.setPickupState(deliveryObj.getString(Keys.PICKUP_STATE));
                        delivery.setPickupCity(deliveryObj.getString(Keys.PICKUP_CITY));
                        delivery.setDropoffCountry(deliveryObj.getString(Keys.DROPOFF_COUNTRY));
                        delivery.setDropoffState(deliveryObj.getString(Keys.DROPOFF_STATE));
                        delivery.setDropoffCity(deliveryObj.getString(Keys.DROPOFF_CITY));
                        //delivery.setWidth(deliveryObj.getString(Keys.ITEM_WIDTH));
                        delivery.setStatus(deliveryObj.getString(Keys.STATUS));
                        // delivery.setStackable(deliveryObj.getString(Keys.ITEM_STACKABLE));
                        delivery.setReceiver(deliveryObj.getString(Keys.RECEIVER_NAME));
                        delivery.setReceiverSign(deliveryObj.getString(Keys.RECEIVER_SIGN));
                        delivery.setSender(deliveryObj.getString(Keys.FROM_PICKUP_NAME));
                        delivery.setSenderSign(deliveryObj.getString(Keys.FROM_PICKUP_SIGN));
                        delivery.setQty(deliveryObj.getString(Keys.ITEM_QTY));
                        //delivery.setWeight(deliveryObj.getString(Keys.ITEM_WEIGHT));
                        delivery.setPuMobile(deliveryObj.getString(Keys.PICKUP_MOBILE));
                        delivery.setPuContactPerson(deliveryObj.getString(Keys.PICKUP_CONTACT_PERSON));
                        //delivery.setDoMobile(deliveryObj.getString(Keys.DROPOFF_MOBILE));
                        delivery.setPuLat(deliveryObj.getString(Keys.PICKUP_LATITUDE));  //plz chk
                        delivery.setPuLng(deliveryObj.getString(Keys.PICKUP_LONGITUDE));
                        delivery.setDoContactPerson(deliveryObj.getString(Keys.DROPOFF_CONTACT));
                        delivery.setDoLat(deliveryObj.getString(Keys.DROPOFF_LATITUDE)); //plz chk
                        delivery.setDoLng(deliveryObj.getString(Keys.DROPOFF_LONGITUDE));
                        //delivery.setPuLiftEquipment(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIPMENT));
                        //delivery.setPuBuildType(deliveryObj.getString(Keys.PICKUP_BUILD_TYPE));
                        //delivery.setDoBuildType(deliveryObj.getString(Keys.DROPOFF_BUILD_TYPE));
                        //delivery.setDoLiftEquipment(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIPMENT));
                        delivery.setFixedOffer(deliveryObj.getString(Keys.FIXED_OFFER));
                        delivery.setAuctionStart(deliveryObj.getString(Keys.AUCTION_START_TIME));
                        delivery.setAuctionEnd(deliveryObj.getString(Keys.AUCTION_END_TIME));
                        delivery.setMaxOpeningBid(deliveryObj.getString(Keys.MAX_AUCTION_BID));
                        delivery.setAuctionBid(deliveryObj.getString(Keys.AUCTION_BID));
                        delivery.setCompanyLogo(deliveryObj.getString(Keys.COMPANY_LOGO));
                        delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                        delivery.setCompleted_at(deliveryObj.getString("completed_at"));
                        //delivery.setDoAppoint(deliveryObj.getString(Keys.DROPOFF_APPOINTMENT));
                        //delivery.setMore(deliveryObj.getString(Keys.ITEM_MORE));
                        delivery.setDoCall(deliveryObj.getString(Keys.DROPOFF_CALL));
                        delivery.setUser_Group(deliveryObj.getString(Keys.USER_GROUP));
                        delivery.setPayment_mode(deliveryObj.getString(Keys.PAYMENT_MODE));
                        try {
                            delivery.setRelistNotification(deliveryObj.getString(Keys.RELIST_NOTIFICATION));
                        } catch (Exception e) {
                            e.toString();
                        }
                    /*if (!deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID).equals("null")) {
                        delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID));
                        delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_NAME));
                    } else {
                        delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_ID));
                        delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_NAME));
                    }
                    */
                        if (deliveryObj.get(Config.BOOKMARK).toString().equals("null")) {
                        } else {
                            delivery.setBookmarkID(deliveryObj.getJSONObject(Config.BOOKMARK).getString(Keys.KEY_ID));
                        }
                        try {
                            delivery.setFav_user_id(deliveryObj.getString(Keys.FAVOURITE_USER_GROUP_IDS));
                        } catch (Exception e) {
                            e.toString();
                        }
                        delivery.setGeo(deliveryObj.getString(Keys.GEO_ZONE));
                        delivery.setRadius(deliveryObj.getString(Keys.RADIUS));

                        delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS)); //added
                        delivery.setDeliveryName(deliveryObj.getString(Keys.DELIVERY_TYPE_NAME));
                        // delivery.setDeliveryTypeID(deliveryObj.getString(Keys.DELIVERY_TYPE_ID));
                        //delivery.setDeliveryTypeSubSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID));
                        // delivery.setDeliveryTypeSubSubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_NAME));
                        //delivery.setCurrentVaccination(deliveryObj.getString(Keys.ITEM_CURRENT_VACCINATIONS));
                        //delivery.setAnimalCarrier(deliveryObj.getString(Keys.ITEM_ANIMAL_CARRIER));
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
                        // delivery.setItemsData(deliveryObj.get(Keys.DELIVERY_ITEMS).toString());
                        delivery.setSuitableVehicles(deliveryObj.get(Keys.SUITABLE_VEHICLE_TEXT).toString());
                        // delivery.setDropoffDateType(deliveryObj.getString(Keys.DROPOFF_DAY_TYPE));
                        //delivery.setPickupDateType(deliveryObj.getString(Keys.PICKUP_DAY_TYPE));
                        //delivery.setDropOffEndDate(deliveryObj.getString(Keys.DROPOFF_END_DAY));
                        //delivery.setPickupEndDate(deliveryObj.getString(Keys.PICKUP_END_DAY));
                        //delivery.setPickupinductionRequire(deliveryObj.getString(Keys.PICKUP_INDUCTION_REQUIRE));
                        delivery.setPickupSpecialRestriction(deliveryObj.getString(Keys.PICKUP_SPECIAL_RESTRICTION));
                        // delivery.setDropoffinductionRequire(deliveryObj.getString(Keys.DROPOFF_INDUCTION_REQUIRE));
                        // delivery.setDropoffSpecialRestriction(deliveryObj.getString(Keys.DROPOFF_SPECIAL_RESTRICTION));
                        //delivery.setSpecialPermit(deliveryObj.getString(Keys.SPECIAL_PERMIT));
                        delivery.setSpecialPermitDetail(deliveryObj.getString(Keys.SPECIAL_PERMIT_DETAIL));
                        delivery.setSuitabelVehicle(deliveryObj.getString(Keys.SUITABLE_VEHICAL_IDS));
                        // delivery.setPickUpLiftEquiAvailableIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_IDS));
                        //delivery.setDropOffLiftEquiAvailableIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_IDS));
                        //delivery.setPickUpLiftEquiNeededIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_IDS));
                        //delivery.setDropOffLiftEquiNeededIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_IDS));
                   /* try {
                        delivery.setPuLiftEquipmentText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT));
                        delivery.setPuLiftEquipmentNeededText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT));
                        delivery.setDoLiftEquipmentText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT));
                        delivery.setDoLiftEquipmentNeededText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT));

                    } catch (Exception e) {
                        e.toString();

                    }*/
                        delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                        delivery.setCompleted_at(deliveryObj.getString("completed_at"));
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
                        deliveryData.add(delivery);
                    }
                    adapter.notifyDataSetChanged();
                    if (deliveryData.size() == 0) {
                        list.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    } else {
                        noData.setVisibility(View.GONE);
                        list.setVisibility(View.VISIBLE);
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

                } catch (Exception e) {
                    e.toString();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
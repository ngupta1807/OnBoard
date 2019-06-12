package com.bookmyride.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.DriverHome;
import com.bookmyride.activities.Filter;
import com.bookmyride.activities.PassengerHome;
import com.bookmyride.activities.RideDetail;
import com.bookmyride.adapters.RideAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Ride;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RideSummary extends Fragment implements AsyncTaskCompleteListener,
        View.OnClickListener {
    LinearLayout allRides, completed;
    TextView allView, completedView;
    ListView rideList;
    SessionHandler session;
    RideAdapter adapter;
    ArrayList<Ride> allRideData = new ArrayList<>();
    ArrayList<Ride> upcomingRideData = new ArrayList<>();
    ArrayList<Ride> completedRideData = new ArrayList<>();
    int selectedType = 1;
    TextView noData;

    EditText search;
    int pageCount, currentPage = 0;
    public static boolean hasMore = false;

    @Override
    public void onDestroy() {
        if (getActivity() instanceof DriverHome)
            DriverHome.filter.setVisibility(View.GONE);
        else if (getActivity() instanceof PassengerHome)
            PassengerHome.filter.setVisibility(View.GONE);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        hasMore = false;
        updateUI(selectedType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() instanceof DriverHome) {
            DriverHome.filter.setVisibility(View.VISIBLE);
            DriverHome.filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getActivity(), Filter.class), 0);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        } else if (getActivity() instanceof PassengerHome) {
            PassengerHome.filter.setVisibility(View.VISIBLE);
            PassengerHome.filter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivityForResult(new Intent(getActivity(), Filter.class), 0);
                    getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            });
        }
        View view = inflater.inflate(R.layout.ride_summary, null);
        init(view);
        //updateUI(1);
        return view;
    }

    private void init(View view) {
        session = new SessionHandler(getActivity());
        allRides = (LinearLayout) view.findViewById(R.id.rides_all);
        allRides.setOnClickListener(this);
        completed = (LinearLayout) view.findViewById(R.id.rides_complete);
        completed.setOnClickListener(this);
        allView = (TextView) view.findViewById(R.id.h_view);
        completedView = (TextView) view.findViewById(R.id.d_view);
        rideList = (ListView) view.findViewById(R.id.rides_list);
        noData = (TextView) view.findViewById(R.id.no_data);
        search = (EditText) view.findViewById(R.id.search);
    }

    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (hasMore)
                getRides("");
        }
    };

    private void setListAdapter(final ArrayList<Ride> rideData) {
        adapter = new RideAdapter(getActivity(), rideData, loadMore);
        rideList.setAdapter(adapter);
        rideList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Ride selected;
                /* if(selectedType == 1)
                    selected = rideData.get(position);
                else if(selectedType == 2)
                    selected = upcomingRideData.get(position);
                else if(selectedType == 3) */
                selected = rideData.get(position);
                startActivity(new Intent(getActivity(), RideDetail.class)
                        .putExtra("rideDetail", selected));
            }
        });
        if (rideData.size() > 0) {
            noData.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
        } else {
            rideList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rides_all:
                updateUI(1);
                break;
            case R.id.rides_complete:
                updateUI(3);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                currentPage = 0;
                RideSummary.hasMore = false;
                setListAdapter(allRideData);
                getFilteredRides(data.getStringExtra("endPoint"));
            }
        }
    }

    private void getFilteredRides(String endPoint) {
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                JSONArray items = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                //if (items.length() > 0 && !hasMore) {
                if (!hasMore) {
                    allRideData.clear();
                    upcomingRideData.clear();
                    completedRideData.clear();
                }
                for (int i = 0; i < items.length(); i++) {
                    JSONObject obj = items.getJSONObject(i);
                    String pick = obj.getString(Key.PICKUP);
                    Ride ride = new Ride();
                    if (!pick.equals("null") && !pick.equals("")) {
                        JSONObject pickup = new JSONObject(pick);
                        ride.setPuInfo(pickup.toString());
                        ride.setPickup(pickup.getString(Key.ADDRESS));
                        ride.setPuLat(pickup.getString("lat"));
                        ride.setPuLng(pickup.getString("lng"));
                    }
                    String drop = obj.getString(Key.DROPOFF);
                    if (drop != null && !drop.equals("null")) {
                        JSONObject dropOff = new JSONObject(drop);
                        ride.setDoInfo(dropOff.toString());
                        ride.setDropoff(dropOff.getString(Key.ADDRESS));
                        ride.setDoLat(dropOff.getString("lat"));
                        ride.setDoLng(dropOff.getString("lng"));
                    }
                    if (obj.has("isFavorite"))
                        ride.setDriverFavourited(obj.get("isFavorite").toString().equals("1") ? true : false);
                    else ride.setDriverFavourited(false);

                    if (obj.has("refundAmount"))
                        ride.setRefunAmount(obj.get("refundAmount").toString());
                    else ride.setRefunAmount("");

                    if (obj.has("refundReason"))
                        ride.setRefundReason(obj.getString("refundReason"));
                    else ride.setRefundReason("");

                    ride.setCardStatus(obj.get("cardStatus").toString());
                    ride.setCardDetail(obj.get("cardDetail").toString());
                    ride.setDriverCategory(obj.getString("driverCategory_id"));
                    ride.setPuDate(obj.getString("pickUpDate"));
                    ride.setId(obj.getString(Key.ID));
                    ride.setStatus(obj.getString("statusText"));
                    ride.setPaymentStatus(obj.get("paymentStatus").toString());
                    ride.setBookingType(obj.getString("type"));
                    ride.setStatusId(obj.get("status").toString());
                    ride.setDoDate(obj.getString("dropOffDate"));
                    ride.setDuration(obj.getString("duration"));
                    ride.setStatusInfo(obj.get(Key.ASAP).toString());
                    //ride.setDiscount(obj.get("is_discount").toString().equals("1")?true:false);
                    ride.setWaitTime(obj.get("waitTime").toString());
                    String fareDetail = obj.getString("fareDetail");
                    if (!fareDetail.equals("") && !fareDetail.equals("null")) {
                        JSONObject fareObj = new JSONObject(fareDetail);
                        ride.setFareInfo(fareObj.toString());
                        String currency;
                        if (fareObj.has("currency"))
                            currency = fareObj.get("currency").toString();
                        else
                            currency = "$";
                        if (!obj.get("status").toString().equals("7")) {
                            ride.setDistance(obj.get("distance").toString());
                            if (fareObj.has("discount"))
                                ride.setDiscountAmt(fareObj.get("discount").toString());
                            if (fareObj.has("discountType"))
                                ride.setDiscountType(fareObj.get("discountType").toString());
                            if (fareObj.has("waitTimeCharges"))
                                ride.setWaitingCharge(fareObj.getString("waitTimeCharges"));
                            if (fareObj.has("tip")) {
                                String total = fareObj.getString("total");
                                String tp = fareObj.getString("tip");
                                double totalPaid = Double.parseDouble(total) + Double.parseDouble(tp);
                                ride.setTotalBill(fareObj.getString("total"));
                                ride.setTotalPaid("" + totalPaid);
                                ride.setTipAmount(currency + fareObj.getString("tip"));
                            } else {
                                ride.setTotalBill(fareObj.getString("total"));
                                ride.setTotalPaid(fareObj.getString("total"));
                            }
                        } else {
                            //ride.setTotalBill(fareObj.getString("total"));
                            ride.setTotalPaid(fareObj.getString("total"));
                        }
                    }
                    String paymentDetail = obj.getString("payment_amount_detail");
                    if (!paymentDetail.equals("") && !paymentDetail.equals("null")) {
                        JSONObject paymentObj = new JSONObject(paymentDetail);
                        String status = "";
                        if (paymentObj.has("status"))
                            status = paymentObj.getString("status");
                        if (!status.equals("422")) {
                            if (paymentObj.has("Wallet_amt_used"))
                                ride.setUsedWalletAmount(paymentObj.get("Wallet_amt_used").toString());
                            else
                                ride.setUsedWalletAmount("0");
                            ride.setTotalPaid(paymentObj.get("amount").toString());
                            ride.setPaidVia(paymentObj.get("payment_gateway").toString());
                        }
                    }
                    String paymentInfo = obj.get("paymentDetail").toString();
                    if (!paymentInfo.equals("") && !paymentInfo.equals("null")) {
                        JSONObject payObj = new JSONObject(paymentInfo);
                        if (payObj.has("card"))
                            ride.setCardNumber(payObj.getString("card"));
                    }
                    String passengerInfo = obj.getString("passanger");
                    if (!passengerInfo.equals("") && !passengerInfo.equals("null")) {
                        JSONObject passengerObj = new JSONObject(passengerInfo);
                        ride.setpName(passengerObj.getString("fullName"));
                        if (passengerObj.getString("dial_code").equals("null"))
                            ride.setpPhone(passengerObj.getString("phone"));
                        else
                            ride.setpPhone(passengerObj.getString("dial_code") + passengerObj.getString("phone"));
                    }

                    String rating = obj.get("rating").toString();
                    if (!rating.equals("") && !rating.equals("null")) {
                        JSONObject ratingObj = new JSONObject(rating);
                        ride.setDriverRating(ratingObj.getDouble("rateToDriver"));
                        ride.setPassengerRating(ratingObj.getDouble("rateToCustomer"));
                    }
                    allRideData.add(ride);
                    if (ride.getStatusId().equals("10"))
                        completedRideData.add(ride);
                    else if (ride.getStatusId().equals("15"))
                        upcomingRideData.add(ride);
                }
                //updateUI(1);
                adapter.notifyDataSetChanged();
                if (adapter.getCount() > 0)
                    noData.setVisibility(View.GONE);
                else noData.setVisibility(View.VISIBLE);

                pageCount = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("pageCount");
                currentPage = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("currentPage");

                if (currentPage < pageCount)
                    hasMore = true;
                else hasMore = false;

            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRides(String status) {
        String endPoint;
        if (status.equals(""))
            endPoint = Config.BOOKING_LIST + "?expand=drivergeo,passanger&page=" + (++currentPage);
        else
            endPoint = Config.BOOKING_LIST + "?status=" + status + "&expand=drivergeo,passanger&page=" + (++currentPage);
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void updateUI(int type) {
        selectedType = type;
        if (type == 1) {
            currentPage = 0;
            setListAdapter(allRideData);
            getRides("");
            search.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
            allView.setBackgroundResource(R.color.driver_color);
            completedView.setBackgroundResource(R.color.white);
        } else if (type == 2) {
            currentPage = 0;
            setListAdapter(upcomingRideData);
            getRides("15");
            search.setVisibility(View.VISIBLE);
            rideList.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            currentPage = 0;
            setListAdapter(completedRideData);
            getRides("10");
            search.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
            allView.setBackgroundResource(R.color.white);
            completedView.setBackgroundResource(R.color.driver_color);
        }
    }
}
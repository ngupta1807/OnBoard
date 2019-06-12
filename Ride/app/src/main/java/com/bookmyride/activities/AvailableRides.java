package com.bookmyride.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.bookmyride.R;
import com.bookmyride.adapters.RideAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.api.ServiceHandlerInBack;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.fragments.RideSummary;
import com.bookmyride.models.CalendarCollection;
import com.bookmyride.models.Ride;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class AvailableRides extends AppCompatActivity implements AsyncTaskCompleteListener,
        View.OnClickListener {
    ListView rideList;
    SessionHandler session;
    RideAdapter adapter;
    ArrayList<Ride> ridesData = new ArrayList<>();
    int selectedType = 3;
    TextView noData;
    int pageCount, currentPage;
    RelativeLayout layList;
    public static boolean hasRefresh = true;
    Timer updateTimer = new Timer();
    int progressState = 0;

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onDestroy() {
        DriverHome.filter.setVisibility(View.GONE);
        hasRefresh = true;
        if (updateTimer != null)
            updateTimer.cancel();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        RideSummary.hasMore = false;
        if (hasRefresh) {
            refreshList();
            //updateUI(selectedType);
        }
    }

    private void refreshList() {
        updateTimer.schedule(new TimerTask() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(selectedType);
                    }
                });
            }
        }, 0, 60000);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_ride);
        init();
        //selectedType = getIntent().getIntExtra("preValue", 3);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    }

    public void simpleClicked() {
        final Calendar calendar = Calendar.getInstance();
        final Date defaultDate = calendar.getTime();

        singleBuilder = new SingleDateAndTimePickerDialog.Builder(AvailableRides.this)
                //.bottomSheet()
                .curved()
                .backgroundColor(Color.BLACK)
                .mainColor(Color.parseColor("#FFFFFF"))
                .minutesStep(1)
                //.mustBeOnFuture()
                .defaultDate(defaultDate)
                //.minDateRange(minDate)
                //.maxDateRange(maxDate)
                .title("Simple")
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        //Alert("Date", simpleDateFormat.format(date));
                        String bookingDataTime = simpleDateFormat.format(date);
                        if (dateType == 0)
                            startDate.setText("" + bookingDataTime);
                        else if (dateType == 1)
                            endDate.setText("" + bookingDataTime);
                    }
                });
        singleBuilder.display();
    }

    SingleDateAndTimePickerDialog.Builder singleBuilder;
    SimpleDateFormat simpleDateFormat;

    DatePickerDialog.OnDateSetListener onDateListen = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String m;
            String d;

            if (monthOfYear < 10)
                m = "0" + monthOfYear;
            else
                m = String.valueOf(monthOfYear);

            if (dayOfMonth < 10)
                d = "0" + dayOfMonth;
            else
                d = String.valueOf(dayOfMonth);

            if (dateType == 0)
                startDate.setText(String.valueOf(year) + "/" + m + "/" + d);
            else if (dateType == 1)
                endDate.setText(String.valueOf(year) + "/" + m + "/" + d);
        }
    };

    private void init() {
        session = new SessionHandler(this);
        rideList = (ListView) findViewById(R.id.rides_list);
        noData = (TextView) findViewById(R.id.no_data);
        layList = (RelativeLayout) findViewById(R.id.lay_list);
    }

    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (RideSummary.hasMore)
                getRides("" + selectedType);
        }
    };

    private void setListAdapter(final ArrayList<Ride> data) {
        adapter = new RideAdapter(this, data, loadMore);
        rideList.setAdapter(adapter);
        rideList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Ride selected = data.get(position);
                startActivity(new Intent(getApplicationContext(), RideDetail.class)
                        .putExtra("rideDetail", selected));
            }
        });
        if (data.size() > 0) {
            noData.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
        } else {
            rideList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    int dateType;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rides_upcoming:
                progressState = 0;
                updateUI(1);
                break;
            case R.id.rides_complete:
                progressState = 0;
                updateUI(2);
                break;
            case R.id.search:
                currentPage = 0;
                getRides("");
                layList.setVisibility(View.VISIBLE);
                break;
            case R.id.list:
                updateUI(1);
                break;
            case R.id.filter:
                //showFilters();
                startActivityForResult(new Intent(getApplicationContext(), Filter.class)
                        .putExtra("type", "" + selectedType), 0);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.start_date:
                dateType = 0;
                simpleClicked();
                break;
            case R.id.end_date:
                dateType = 1;
                simpleClicked();
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
                setListAdapter(ridesData);
                getFilteredRides(data.getStringExtra("endPoint"));
            }
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                CalendarCollection.date_collection_arr = new ArrayList<CalendarCollection>();
                JSONArray items = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                if (!RideSummary.hasMore) {
                    ridesData.clear();
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
                        ride.setDriverFavourited(obj.get("isFavorite").equals("1") ? true : false);
                    else ride.setDriverFavourited(false);
                    ride.setDriverCategory(obj.getString("driverCategory_id"));
                    ride.setPuDate(obj.getString("pickUpDate"));
                    ride.setId(obj.getString(Key.ID));
                    ride.setStatus(obj.getString("statusText"));
                    ride.setPaymentStatus(obj.get("paymentStatus").toString());
                    ride.setBookingType(obj.getString("type"));
                    ride.setStatusId(obj.get("status").toString());
                    ride.setDoDate(obj.getString("dropOffDate"));
                    ride.setDiscount(obj.get("is_discount").toString().equals("1") ? true : false);
                    ride.setDuration(obj.getString("duration"));
                    ride.setStatusInfo(obj.get(Key.ASAP).toString());
                    ride.setWaitTime(obj.get("waitTime").toString());
                    String fareDetail = obj.getString("fareDetail");
                    if (!fareDetail.equals("") && !fareDetail.equals("null")) {
                        JSONObject fareObj = new JSONObject(fareDetail);
                        ride.setFareInfo(fareObj.toString());
                        String currency = fareObj.get("currency").toString();
                        ride.setDistance(obj.get("distance").toString());
                        if (fareObj.has("discount"))
                            ride.setDiscountAmt(fareObj.get("discount").toString());
                        if (fareObj.has("discountType"))
                            ride.setDiscountType(fareObj.get("discountType").toString());
                        if (fareObj.has("tip")) {
                            String total = fareObj.getString("total");
                            String tp = fareObj.getString("tip");
                            double totalPaid = Double.parseDouble(total) + Double.parseDouble(tp);
                            ride.setTotalBill(fareObj.getString("total"));
                            ride.setTotalPaid(currency + totalPaid);
                            ride.setTipAmount(currency + fareObj.getString("tip"));
                        } else {
                            ride.setTotalBill(fareObj.getString("total"));
                            ride.setTotalPaid(currency + fareObj.getString("total"));
                        }
                    }
                    String paymentDetail = obj.getString("payment_amount_detail");
                    if (!paymentDetail.equals("") && !paymentDetail.equals("null")) {
                        JSONObject paymentObj = new JSONObject(paymentDetail);
                        ride.setUsedWalletAmount(paymentObj.get("Wallet_amt_used").toString());
                        ride.setTotalPaid("$" + paymentObj.get("amount").toString());
                        ride.setPaidVia(paymentObj.get("payment_gateway").toString());
                    }
                    String paymentInfo = obj.getString("paymentDetail");
                    if (!paymentInfo.equals("") && !paymentInfo.equals("null")) {
                        JSONObject payObj = new JSONObject(paymentInfo);
                        if (payObj.has("card"))
                            ride.setCardNumber(payObj.getString("card"));
                    }
                    String passengerInfo = obj.getString("passanger");
                    if (!passengerInfo.equals("") && !passengerInfo.equals("null")) {
                        JSONObject passengerObj = new JSONObject(passengerInfo);
                        ride.setpName(passengerObj.getString("fullName"));
                        if (passengerObj.getString("dial_code").equals("null") ||
                                passengerObj.getString("dial_code").equals(""))
                            ride.setpPhone(passengerObj.getString("phone"));
                        else
                            ride.setpPhone(passengerObj.getString("dial_code") + passengerObj.getString("phone"));
                    }
                    ride.setMessage("Test");
                    // Insert event to db
                    ridesData.add(ride);
                }
                //updateUI(1);
                adapter.notifyDataSetChanged();
                if (adapter.getCount() > 0) {
                    noData.setVisibility(View.GONE);
                    rideList.setVisibility(View.VISIBLE);
                } else {
                    rideList.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                }
                progressState = 1;
                pageCount = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("pageCount");
                currentPage = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("currentPage");

                if (currentPage < pageCount)
                    RideSummary.hasMore = true;
                else RideSummary.hasMore = false;

            } else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getRides(String type) {
        String endPoint = Config.BOOKING_LIST + "?type=" + type + "&expand=drivergeo,passanger&page=" + (++currentPage);
        if (Internet.hasInternet(this)) {
            if (progressState == 0) {
                APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
                apiHandler.execute(endPoint, session.getToken());
            } else {
                ServiceHandlerInBack apiHandler = new ServiceHandlerInBack(this, HTTPMethods.GET, this, null);
                apiHandler.execute(endPoint, session.getToken());
            }
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void getFilteredRides(String endPoint) {
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(this, true);
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

    TextView startDate, endDate;

    private void updateUI(int type) {
        selectedType = type;
        currentPage = 0;
        RideSummary.hasMore = false;
        setListAdapter(ridesData);
        getRides("" + selectedType);
        rideList.setVisibility(View.VISIBLE);
    }
}

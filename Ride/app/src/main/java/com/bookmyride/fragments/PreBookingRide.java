package com.bookmyride.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.DriverHome;
import com.bookmyride.activities.Filter;
import com.bookmyride.activities.RideDetail;
import com.bookmyride.activities.RidesOnCalendar;
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
import com.bookmyride.db.DatabaseHandler;
import com.bookmyride.models.CalendarCollection;
import com.bookmyride.models.Ride;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class PreBookingRide extends Fragment implements AsyncTaskCompleteListener,
        View.OnClickListener {
    ListView rideList;
    SessionHandler session;
    RideAdapter adapter;
    ArrayList<Ride> upcomingData = new ArrayList<>();
    ArrayList<Ride> completedData = new ArrayList<>();
    int selectedType = 1;
    TextView noData, calendar;
    int pageCount, currentPage;
    RelativeLayout layList;
    LinearLayout upcoming, completed;
    TextView allView, completedView;
    DatabaseHandler db;
    public static boolean hasRefresh = true;
    Timer updateTimer = new Timer();
    int progressState = 0;

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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(selectedType);
                    }
                });
            }
        }, 0, 60000);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DriverHome.filter.setVisibility(View.VISIBLE);
        DriverHome.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), Filter.class)
                        .putExtra("type", "" + selectedType), 0);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        View view = inflater.inflate(R.layout.prebooking_ride, null);
        init(view);
        selectedType = getArguments().getInt("preValue");
        return view;
    }

    private void init(View view) {
        db = new DatabaseHandler(getActivity());
        session = new SessionHandler(getActivity());
        rideList = (ListView) view.findViewById(R.id.rides_list);
        noData = (TextView) view.findViewById(R.id.no_data);
        calendar = (TextView) view.findViewById(R.id.calendar);
        calendar.setOnClickListener(this);

        upcoming = (LinearLayout) view.findViewById(R.id.rides_upcoming);
        upcoming.setOnClickListener(this);
        completed = (LinearLayout) view.findViewById(R.id.rides_complete);
        completed.setOnClickListener(this);
        allView = (TextView) view.findViewById(R.id.h_view);
        completedView = (TextView) view.findViewById(R.id.d_view);
        layList = (RelativeLayout) view.findViewById(R.id.lay_list);
    }

    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (RideSummary.hasMore)
                getRides("" + selectedType);
        }
    };

    private void setListAdapter(final ArrayList<Ride> data) {
        adapter = new RideAdapter(getActivity(), data, loadMore);
        rideList.setAdapter(adapter);
        rideList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Ride selected = data.get(position);
                startActivity(new Intent(getActivity(), RideDetail.class)
                        .putExtra("rideDetail", selected));
            }
        });
        if (data.size() > 0) {
            calendar.setVisibility(View.VISIBLE);
            noData.setVisibility(View.GONE);
            rideList.setVisibility(View.VISIBLE);
        } else {
            calendar.setVisibility(View.GONE);
            rideList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

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
                startActivityForResult(new Intent(getActivity(), Filter.class)
                        .putExtra("type", "" + selectedType), 0);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.calendar:
                startActivity(new Intent(getActivity(), RidesOnCalendar.class));
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                /*if (calendar.getText().toString().contains("Calendar")) {
                    calendar.setText("View On List");
                    layList.setVisibility(View.GONE);
                    layCalendar.setVisibility(View.VISIBLE);
                } else if (calendar.getText().toString().contains("List")) {
                    calendar.setText("View On Calendar");
                    layCalendar.setVisibility(View.GONE);
                    layList.setVisibility(View.VISIBLE);
                }*/
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
                setListAdapter(upcomingData);
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
                    upcomingData.clear();
                    completedData.clear();
                }
                db.deleteAllRides();
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
                    ride.setDiscount(obj.get("is_discount").toString().equals("1") ? true : false);
                    ride.setDuration(obj.getString("duration"));
                    ride.setStatusInfo(obj.get(Key.ASAP).toString());
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
                        }
                    }
                    String paymentDetail = obj.getString("payment_amount_detail");
                    if (!paymentDetail.equals("") && !paymentDetail.equals("null")) {
                        JSONObject paymentObj = new JSONObject(paymentDetail);
                        ride.setUsedWalletAmount(paymentObj.get("Wallet_amt_used").toString());
                        ride.setTotalPaid(paymentObj.get("amount").toString());
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
                    String rating = obj.get("rating").toString();
                    if (!rating.equals("") && !rating.equals("null")) {
                        JSONObject ratingObj = new JSONObject(rating);
                        ride.setDriverRating(ratingObj.getDouble("rateToDriver"));
                        ride.setPassengerRating(ratingObj.getDouble("rateToCustomer"));
                    }
                    ride.setMessage("Test");
                    // Insert event to db
                    db.addEvent(ride);
                    upcomingData.add(ride);
                    if (ride.getStatusId().equals("10"))
                        completedData.add(ride);

                    //CalenderAccess.readCalendarEvent(getActivity());
                    //CalenderAccess.addReminderInCalendar("2017-06-02 01:05","Nisha","Ride :",getActivity());
                }
                //saveEventsOnCalendar();
                //updateUI(1);
                adapter.notifyDataSetChanged();
                if (adapter.getCount() > 0) {
                    calendar.setVisibility(View.VISIBLE);
                    noData.setVisibility(View.GONE);
                    rideList.setVisibility(View.VISIBLE);
                } else {
                    calendar.setVisibility(View.GONE);
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

    private void saveEventsOnCalendar() {
        for (int i = 0; i < upcomingData.size(); i++) {
            Ride ride = upcomingData.get(i);
            readCalendarEvent(getActivity(), ride.getPuDate());
            addReminderInCalendar(ride.getPuDate(), "Pre-Booking", ride.getPuInfo());
        }
    }

    /* Adds Events and Reminders in Calendar. */
    private void addReminderInCalendar(String puDate, String title, String puInfo) {
        String puAddress = "";
        try {
            JSONObject puObj = new JSONObject(puInfo);
            puAddress = puObj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String datetime[] = puDate.split(" ");
        String date[] = datetime[0].split("-");
        String time[] = datetime[1].split(":");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(date[0]));
        cal.set(Calendar.DATE, Integer.parseInt(date[2]));
        cal.set(Calendar.HOUR, Integer.parseInt(time[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        //cal.set(Calendar.SECOND, Integer.parseInt(time[2]));

        ContentResolver cr = getActivity().getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();
        Uri EVENTS_URI = Uri.parse(this.getCalendarUriBase(true) + "events");
        /* Inserting an event in calendar. */
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, puAddress);
        values.put(CalendarContract.Events.ALL_DAY, 0);
        // event starts at 11 minutes from now

        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis() + 1 * 60 * 1000);
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis() + 2 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        Uri event = cr.insert(EVENTS_URI, values);

        /* Adding reminder for event added. */
        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 15);
        cr.insert(REMINDERS_URI, values);
    }

    private void getRides(String type) {
        String endPoint = Config.BOOKING_LIST + "?type=" + type + "&expand=drivergeo,passanger&page=" + (++currentPage);
        if (Internet.hasInternet(getActivity())) {
            if (progressState == 0) {
                APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
                apiHandler.execute(endPoint, session.getToken());
            } else {
                ServiceHandlerInBack apiHandler = new ServiceHandlerInBack(getActivity(), HTTPMethods.GET, this, null);
                apiHandler.execute(endPoint, session.getToken());
            }
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void getFilteredRides(String endPoint) {
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(getActivity(), true);
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
            RideSummary.hasMore = false;
            setListAdapter(upcomingData);
            getRides("1");
            rideList.setVisibility(View.VISIBLE);
            allView.setBackgroundResource(R.color.driver_color);
            completedView.setBackgroundResource(R.color.white);
        } else if (type == 2) {
            currentPage = 0;
            RideSummary.hasMore = false;
            setListAdapter(upcomingData);
            getRides("2");
            rideList.setVisibility(View.VISIBLE);
            allView.setBackgroundResource(R.color.white);
            completedView.setBackgroundResource(R.color.driver_color);
        }
    }

    /**
     * Returns Calendar Base URI, supports both new and old OS.
     */
    public static String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }

    public void deleteEvent(long eventID) {
        Uri eventUri = ContentUris.withAppendedId(Uri.parse(getCalendarUriBase(true)), eventID);
        getActivity().getContentResolver().delete(eventUri, null, null);
    }

    public void readCalendarEvent(Context context, String date) {
        Cursor cursor = context.getContentResolver()
                .query(Uri.parse("content://com.android.calendar/events"),
                        new String[]{"_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();
        String CNames[] = new String[cursor.getCount()];
        for (int i = 0; i < CNames.length; i++) {
            // if(date.equals(getDate(Long.parseLong(cursor.getString(3))))){
            deleteEvent(Long.parseLong(cursor.getString(0)));
            //}
            cursor.moveToNext();
        }
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        Date date = calendar.getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -1);
        return formatter.format(c.getTime());
    }
}

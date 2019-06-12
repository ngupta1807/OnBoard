package com.bookmyride.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Vinod on 2017-01-07.
 */
public class EstimateRide extends AppCompatActivity implements AsyncTaskCompleteListener,
        View.OnClickListener {
    private TextView pickup, drop, distance;
    SessionHandler session;
    TextView taxiBaseFare, taxiProcessingFee, taxiGST, taxiTotal,
            economyBaseFare, economyProcessingFee, economyGST, economyTotal,
            premiumBaseFare, premiumProcessingFee, premiumGST, premiumTotal,
            bikeBaseFare, bikeProcessingFee, bikeGST, bikeTotal;
    TextView taxiRide, economyRide, premiumRide, bikeRide, noData;
    ImageView iconTaxi;

    String puLocation, doLocation, puLat, puLng, doLat, doLng, selectedCategory;
    RelativeLayout layTaxi, layEconomy, layPremium, layBike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_estimate);
        init();
        getData();
        getEstimate();
    }

    public void getData() {
        Intent intent = getIntent();
        puLat = intent.getStringExtra("puLat");
        puLng = intent.getStringExtra("puLng");
        doLat = intent.getStringExtra("doLat");
        doLng = intent.getStringExtra("doLng");
        puLocation = intent.getStringExtra("pickup");
        doLocation = intent.getStringExtra("dropoff");
    }

    private void init() {
        session = new SessionHandler(this);
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        pickup = (TextView) findViewById(R.id.pu_address);
        drop = (TextView) findViewById(R.id.do_address);
        distance = (TextView) findViewById(R.id.distance);
        taxiBaseFare = (TextView) findViewById(R.id.t_fare);
        taxiProcessingFee = (TextView) findViewById(R.id.t_process_fee);
        taxiGST = (TextView) findViewById(R.id.t_gst);
        taxiTotal = (TextView) findViewById(R.id.t_total);
        economyBaseFare = (TextView) findViewById(R.id.e_fare);
        economyProcessingFee = (TextView) findViewById(R.id.e_process_fee);
        economyGST = (TextView) findViewById(R.id.e_gst);
        economyTotal = (TextView) findViewById(R.id.e_total);
        premiumBaseFare = (TextView) findViewById(R.id.p_fare);
        premiumProcessingFee = (TextView) findViewById(R.id.p_process_fee);
        premiumGST = (TextView) findViewById(R.id.p_gst);
        premiumTotal = (TextView) findViewById(R.id.p_total);
        bikeBaseFare = (TextView) findViewById(R.id.m_fare);
        bikeProcessingFee = (TextView) findViewById(R.id.m_process_fee);
        bikeGST = (TextView) findViewById(R.id.m_gst);
        bikeTotal = (TextView) findViewById(R.id.m_total);

        taxiRide = (TextView) findViewById(R.id.t_status);
        economyRide = (TextView) findViewById(R.id.e_status);
        premiumRide = (TextView) findViewById(R.id.p_status);
        bikeRide = (TextView) findViewById(R.id.m_status);

        taxiRide.setOnClickListener(this);
        economyRide.setOnClickListener(this);
        premiumRide.setOnClickListener(this);
        bikeRide.setOnClickListener(this);

        iconTaxi = (ImageView) findViewById(R.id.icon_taxi);

        layTaxi = (RelativeLayout) findViewById(R.id.lay_taxi);
        layEconomy = (RelativeLayout) findViewById(R.id.lay_economy);
        layPremium = (RelativeLayout) findViewById(R.id.lay_premium);
        layBike = (RelativeLayout) findViewById(R.id.lay_bike);

        noData = (TextView) findViewById(R.id.no_data);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = sdf.format(new Date());
        return currentDateandTime;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.t_status:
                selectedCategory = "1";
                requestDialog();
                break;
            case R.id.e_status:
                selectedCategory = "2";
                requestDialog();
                break;
            case R.id.p_status:
                selectedCategory = "3";
                requestDialog();
                break;
            case R.id.m_status:
                selectedCategory = "4";
                requestDialog();
                break;
        }
    }

    public void getEstimate() {
        Intent intent = getIntent();
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", intent.getStringExtra("puLat"));
        params.put("lng", intent.getStringExtra("puLng"));
        params.put("distance", intent.getStringExtra("distance"));
        params.put("duration", intent.getStringExtra("duration"));
        params.put("pickUpDate", getCurrentDateTime());
        params.put("driverCategory", intent.getStringExtra("category"));
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
            apiCall.execute(Config.ESTIMATE_RIDE, session.getToken());
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
                //JSONArray dataArray = outJson.getJSONArray(Key.DATA);
                JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                JSONObject taxiObj = dataObj.getJSONObject("Taxi");
                JSONObject economyObj = dataObj.getJSONObject("Economy");
                JSONObject premiumObj = dataObj.getJSONObject("Premium");
                JSONObject bikeObj = dataObj.getJSONObject("Motor Bike");

                pickup.setText(getIntent().getStringExtra("pickup"));
                drop.setText(getIntent().getStringExtra("dropoff"));

                iconTaxi.bringToFront();
                iconTaxi.getParent().requestLayout();
                iconTaxi.invalidate();

                String taxiCount = taxiObj.getString("vehiclesCount");
                String economyCount = economyObj.getString("vehiclesCount");
                String premiumCount = premiumObj.getString("vehiclesCount");
                String bikeCount = bikeObj.getString("vehiclesCount");

                //Fare summary for taxi
                //taxiBaseFare.setText(taxiObj.getString("currency") + taxiObj.get("fare").toString());
                taxiBaseFare.setText(taxiObj.getString("vehiclesCount") + " Vehicles");
                //taxiProcessingFee.setText(taxiObj.getString("currency") + taxiObj.get("processFee").toString());
                taxiProcessingFee.setText(taxiObj.getString("ApproxPickUpIn"));
                //taxiGST.setText(taxiObj.get("serviceTax").toString() + "%");
                taxiGST.setText(taxiObj.getString("currency") + getFarePerKM(taxiObj.get("farePerKm").toString()));
                taxiTotal.setText(taxiObj.getString("currency") + taxiObj.get("total").toString());

                //Fare summary for Economy
                //economyBaseFare.setText(economyObj.getString("currency") + economyObj.get("fare").toString());
                economyBaseFare.setText(economyObj.getString("vehiclesCount") + " Vehicles");
                //economyProcessingFee.setText(economyObj.getString("currency") + economyObj.get("processFee").toString());
                economyProcessingFee.setText(economyObj.getString("ApproxPickUpIn"));
                //economyGST.setText(economyObj.get("serviceTax").toString() + "%");
                economyGST.setText(economyObj.getString("currency") + getFarePerKM(economyObj.get("farePerKm").toString()));
                economyTotal.setText(economyObj.getString("currency") + economyObj.get("total").toString());

                //Fare summary for Premium
                //premiumBaseFare.setText(premiumObj.getString("currency") + premiumObj.get("fare").toString());
                premiumBaseFare.setText(premiumObj.getString("vehiclesCount") + " Vehicles");
                //premiumProcessingFee.setText(premiumObj.getString("currency") + premiumObj.get("processFee").toString());
                premiumProcessingFee.setText(premiumObj.getString("ApproxPickUpIn"));
                //premiumGST.setText(premiumObj.get("serviceTax").toString() + "%");
                premiumGST.setText(premiumObj.getString("currency") + getFarePerKM(premiumObj.get("farePerKm").toString()));
                premiumTotal.setText(premiumObj.getString("currency") + premiumObj.get("total").toString());

                //Fare summary for Motor Bike
                //bikeBaseFare.setText(bikeObj.getString("currency") + premiumObj.get("fare").toString());
                bikeBaseFare.setText(bikeObj.getString("vehiclesCount") + " Vehicles");
                //bikeProcessingFee.setText(bikeObj.getString("currency") + bikeObj.get("processFee").toString());
                bikeProcessingFee.setText(bikeObj.getString("ApproxPickUpIn"));
                //bikeGST.setText(bikeObj.get("serviceTax").toString() + "%");
                bikeGST.setText(bikeObj.getString("currency") + getFarePerKM(bikeObj.get("farePerKm").toString()));
                bikeTotal.setText(bikeObj.getString("currency") + bikeObj.get("total").toString());

                distance.setText(taxiObj.get("distance").toString() + taxiObj.getString("distanceUnit"));

                if (taxiCount.equals("0") || taxiCount.equals("") || taxiCount.equals("null"))
                    layTaxi.setVisibility(View.GONE);
                if (economyCount.equals("0") || economyCount.equals("") || economyCount.equals("null"))
                    layEconomy.setVisibility(View.GONE);
                if (premiumCount.equals("0") || premiumCount.equals("") || premiumCount.equals("null"))
                    layPremium.setVisibility(View.GONE);
                if (bikeCount.equals("0") || bikeCount.equals("") || bikeCount.equals("null"))
                    layBike.setVisibility(View.GONE);

                if (layTaxi.getVisibility() == View.GONE &&
                        layEconomy.getVisibility() == View.GONE &&
                        layPremium.getVisibility() == View.GONE &&
                        layBike.getVisibility() == View.GONE) {
                    noData.setVisibility(View.VISIBLE);
                } else noData.setVisibility(View.GONE);

            } else if (outJson.getInt(Key.STATUS) == APIStatus.CREATED) {
                JSONArray dataArray = outJson.getJSONArray(Key.DATA);
                JSONObject obj = dataArray.getJSONObject(0);
                String userId = obj.get(Key.USER_ID).toString();
                String rideId = obj.get(Key.ID).toString();
                showBookingTimer(userId, rideId);
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                JSONArray jsonArray = outJson.getJSONArray(Key.DATA);
                JSONObject innerObj = jsonArray.getJSONObject(0);
                String field = innerObj.getString("field");
                String message = innerObj.getString("message");
                //Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
                if (field.equalsIgnoreCase("payment")) {
                    String rideId = innerObj.getString("rideId");
                    paymentPendingPrompt(rideId, message);
                } else
                    Alert("Alert!", message);
            }else {
                Alert("Alert!", outJson.getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void paymentPendingPrompt(final String rideID, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(EstimateRide.this, true);
        mDialog.setDialogTitle("Alert!");
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        startActivity(new Intent(EstimateRide.this, RideDetail.class)
                                .putExtra("booking_id", rideID));
                    }
                });
        mDialog.show();
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(EstimateRide.this, true);
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

    private void bookRide(String type) {
        HashMap<String, String> requestParam = new HashMap<>();
        JSONObject pickup = new JSONObject();
        try {
            pickup.put("address", puLocation);
            pickup.put("lat", puLat);
            pickup.put("lng", puLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (bookingType == 0)
            requestParam.put("pickUpDate", getCurrentDateTime());
        else
            requestParam.put("pickUpDate", bookingDataTime);
        requestParam.put("pickUp", pickup.toString());
        JSONObject dropoff = new JSONObject();
        try {
            dropoff.put("address", doLocation);
            dropoff.put("lat", doLat);
            dropoff.put("lng", doLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParam.put("dropOff", dropoff.toString());
        requestParam.put("distance", getIntent().getStringExtra("distance"));
        requestParam.put("duration", getIntent().getStringExtra("duration"));
        requestParam.put("type", type);
        requestParam.put("driverCategory_id", selectedCategory);
        requestParam.put("is_guarantee", "0");
        requestParam.put("note", strNote);

        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.BOOKING_REQUEST, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showBookingTimer(String userID, String rideID) {
        JSONObject pickup = new JSONObject();
        try {
            pickup.put("address", puLocation);
            pickup.put("lat", puLat);
            pickup.put("lng", puLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject dropOff = new JSONObject();
        try {
            dropOff.put("address", doLocation);
            dropOff.put("lat", doLat);
            dropOff.put("lng", doLng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(EstimateRide.this, BookingTimer.class);
        intent.putExtra("pickUp", pickup.toString());
        intent.putExtra("dropOff", dropOff.toString());
        intent.putExtra("CategoryID", selectedCategory);
        intent.putExtra("type", "0");
        intent.putExtra("puDate", getCurrentDateTime());
        intent.putExtra("UserID", userID);
        intent.putExtra("RideID", rideID);
        intent.putExtra("Response_time", "30");
        intent.putExtra("Next_driver_availability", "30");
        intent.putExtra("Message", "This booking request has not yet been accepted by available drivers. We will continue looking for an available driver and will notify you as soon as we have found one. Please cancel the booking if you no longer need this ride*. *Please note the BookMyRide cancellation policy.");
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        EstimateRide.this.finish();
    }

    private String getFarePerKM(String fare) {
        double actFare = Double.parseDouble(fare);
        String duration = String.format("%.2f", new BigDecimal(actFare));
        return duration + "/KM";
    }

    Dialog mDialog;
    String strNote = "";
    int bookingType = 2;
    String bookingDataTime = "";

    public void requestDialog() {
        mDialog = new Dialog(this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_note);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        TextView message = (TextView) mDialog.findViewById(R.id.msg);
        final TextView note = (TextView) mDialog.findViewById(R.id.note);
        TextView now = (TextView) mDialog.findViewById(R.id.now);
        TextView later = (TextView) mDialog.findViewById(R.id.later);
        ImageView cross = (ImageView) mDialog.findViewById(R.id.cross);
        title.setText("BookMyRide");
        message.setText("Do you want to request now or later?");
        now.setText("Now");
        later.setText("Later");

        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mDialog.dismiss();
                strNote = note.getText().toString();
                bookingType = 0;
                if (session.isCardExist()) {
                    mDialog.dismiss();
                    bookRide("0");
                } else
                    isCardSaved();
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                strNote = note.getText().toString();
                bookingType = 1;
                //showDateTimePicker();
                simpleClicked();
            }
        });

        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        mDialog.show();
    }

    public void simpleClicked() {

        final Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.HOUR, 1);
        calendar.add(Calendar.MINUTE, 11);
        final Date defaultDate = calendar.getTime();

        singleBuilder = new SingleDateAndTimePickerDialog.Builder(this)
                .bottomSheet()
                .curved()
                .backgroundColor(Color.BLACK)
                .mainColor(Color.parseColor("#e0e0e0"))
                .minutesStep(1)
                .mustBeOnFuture()
                .defaultDate(defaultDate)
                .listener(new SingleDateAndTimePickerDialog.Listener() {
                    @Override
                    public void onDateSelected(Date date) {
                        //Alert("Date", simpleDateFormat.format(date));
                        bookingDataTime = simpleDateFormat.format(date);
                        if (findTimeDistance(bookingDataTime) >= 1) {
                            if (session.isCardExist()) {
                                if (mDialog != null && mDialog.isShowing())
                                    mDialog.dismiss();
                                bookRide("1");
                            } else
                                isCardSaved();
                            //bookRide("1");
                        } else
                            Alert("BookMyRide", "The pre-booked ride time must be at least one hour from current time.");
                        //singleText.setText(simpleDateFormat.format(date));
                    }
                });
        singleBuilder.display();
    }

    SingleDateAndTimePickerDialog.Builder singleBuilder;
    SimpleDateFormat simpleDateFormat;

    private int findTimeDistance(String bookingDataTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Calendar cal = Calendar.getInstance();
            String currentDateTime = format.format(cal.getTime());
            Date Date1 = format.parse(currentDateTime);
            Date Date2 = format.parse(bookingDataTime);
            long mills = Date2.getTime() - Date1.getTime();
            int Hours = (int) (mills / (1000 * 60 * 60));
            int Mins = (int) (mills / (1000 * 60)) % 60;

            String diff = Hours + ":" + Mins; // updated value every1 second
            return Hours;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void isCardSaved() {
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) {
                    try {
                        JSONObject outerJson = new JSONObject(result);
                        if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                            if (mDialog != null && mDialog.isShowing())
                                mDialog.dismiss();
                            session.saveCardExist(true);
                            if (bookingType == 0) {
                                bookRide("0");
                            } else {
                                bookRide("1");
                            }
                        } else if (outerJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                            session.saveCardExist(false);
                            showRideDialog("Alert!", outerJson.getString(Key.MESSAGE));
                        } else {
                            session.saveCardExist(false);
                            Alert("Alert!", outerJson.getString(Key.MESSAGE));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, null);
            apiHandler.execute(Config.CHECK_AVAILABLE_CARD + session.getUserID(), session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showRideDialog(String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(this, false);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setCancelOnTouchOutside(false);
        mDialog.setPositiveButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setNegativeButton("Save Card", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                startActivityForResult(new Intent(getApplicationContext(), MyCard.class)
                        .putExtra("isBack", ""), 0);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mDialog.show();
    }
}
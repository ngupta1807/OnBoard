package com.bookmyride.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.bookmyride.R;
import com.bookmyride.adapters.PlaceAutoCompleteAdapter;
import com.bookmyride.api.Config;
import com.bookmyride.fragments.PreBookingRide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by vinod on 5/22/2017.
 */
public class Filter extends AppCompatActivity implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    TextView startDate, endDate, time, search, reset;
    ImageView cross;
    AutoCompleteTextView puAddress, doAddress;
    private PlaceAutoCompleteAdapter puAdapter;
    private PlaceAutoCompleteAdapter doAdapter;
    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_filter);
        init();
    }

    public void showPicker() {
        final Calendar calendar = Calendar.getInstance();
        final Date defaultDate = calendar.getTime();

        singleBuilder = new SingleDateAndTimePickerDialog.Builder(Filter.this)
                .curved()
                .minutesStep(1)
                .defaultDate(defaultDate)
                .title("Choose Date & Time")
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

    private void init() {
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        reset = (TextView) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        startDate = (TextView) findViewById(R.id.start_date);
        startDate.setOnClickListener(this);
        endDate = (TextView) findViewById(R.id.end_date);
        endDate.setOnClickListener(this);
        time = (TextView) findViewById(R.id.time);
        puAddress = (AutoCompleteTextView) findViewById(R.id.pu_address);
        doAddress = (AutoCompleteTextView) findViewById(R.id.do_address);
        puAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);
        puAddress.setAdapter(puAdapter);

        doAdapter = new PlaceAutoCompleteAdapter(this, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);
        doAddress.setAdapter(doAdapter);
        cross = (ImageView) findViewById(R.id.cross);
        cross.setOnClickListener(this);
        search = (TextView) findViewById(R.id.search);
        search.setOnClickListener(this);

        puAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = puAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(Config.TAG, "Autocomplete item selected: " + item.description);

                /* Issue a request to the Places Geo Data API to retrieve a Place object with additional
                details about the place.
                */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(Config.TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);
                    }
                });
            }
        });
        puAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                /*if (start != null) {
                    start = null;
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        doAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = doAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i(Config.TAG, "Autocomplete item selected: " + item.description);

                /* Issue a request to the Places Geo Data API to retrieve a Place object with additional
                details about the place.
                */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(Config.TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);
                    }
                });
            }
        });
        doAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                /*if (start != null) {
                    start = null;
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    int dateType;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                startDate.setText("");
                endDate.setText("");
                puAddress.setText("");
                doAddress.setText("");
                break;
            case R.id.cross:
                Filter.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            case R.id.start_date:
                dateType = 0;
                showPicker();
                break;
            case R.id.end_date:
                dateType = 1;
                showPicker();
                break;
            case R.id.search:
                String type = "?type=";
                String from = "&from=";
                String to = "&to=";
                String puAdd = "&pickup_address=";
                String doAdd = "&dropoff_address=";
                String page = "&page=0";
                String baseEndPoint = Config.BOOKING_LIST;
                if (getIntent().hasExtra("type"))
                    baseEndPoint = baseEndPoint + type + getIntent().getStringExtra("type")
                            + "&expand=drivergeo,passanger";
                else baseEndPoint = baseEndPoint + "?expand=drivergeo,passanger";
                String endPoint = baseEndPoint
                        + from + startDate.getText().toString()
                        + to + endDate.getText().toString()
                        + puAdd + puAddress.getText().toString()
                        + doAdd + doAddress.getText().toString()
                        + page;
                Intent intent = new Intent();
                intent.putExtra("endPoint", endPoint);
                setResult(RESULT_OK, intent);
                Filter.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                PreBookingRide.hasRefresh = false;
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(Config.TAG, connectionResult.toString());
    }
}

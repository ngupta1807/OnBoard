package com.bookmyride.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Vinod on 2017-01-07.
 */
public class RatingToDriver extends AppCompatActivity implements AsyncTaskCompleteListener {
    TextView submit, skip;
    RatingBar punctuality, driving, cleanness, courtesy;
    float punRaring = 0.0f, drivingRating = 0.0f, cleanRating = 0.0f, courtesyRating = 0.0f;
    SessionHandler session;
    EditText comment;
    CheckBox favoriteDriver;
    boolean isDriverFavourited = false;
    RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.rate_to_driver);
        init();
        if (getIntent().hasExtra("is_back")) {
            skip.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
        } else back.setVisibility(View.GONE);
    }

    private void closeKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void init() {
        session = new SessionHandler(this);
        skip = (TextView) findViewById(R.id.skip);
        back = (RelativeLayout) findViewById(R.id.back);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().hasExtra("is_back")) {
                    Intent intent = new Intent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    Intent intent;
                    if (session.getUserType().equals("4"))
                        intent = new Intent(getApplicationContext(), DriverHome.class);
                    else
                        intent = new Intent(getApplicationContext(), PassengerHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    RatingToDriver.this.finish();
                }
            }
        });
        submit = (TextView) findViewById(R.id.submit);
        comment = (EditText) findViewById(R.id.comment);
        comment.clearFocus();
        closeKeyboard(comment);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    if (!isDriverFavourited)
                        favouriteDriver();
                    else
                        submitRating("");
                }
            }
        });
        punctuality = (RatingBar) findViewById(R.id.rating_punctuality);
        driving = (RatingBar) findViewById(R.id.rating_driving);
        cleanness = (RatingBar) findViewById(R.id.rating_cleanliness);
        courtesy = (RatingBar) findViewById(R.id.rating_courtesy);
        punctuality.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                punRaring = rating;
            }
        });
        driving.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                drivingRating = rating;
            }
        });
        cleanness.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                cleanRating = rating;
            }
        });
        courtesy.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                courtesyRating = rating;
            }
        });
        favoriteDriver = (CheckBox) findViewById(R.id.favourite);
        isDriverFavourited = getIntent().getBooleanExtra("isFavourite", false);
        /*if (isDriverFavourited)
            favoriteDriver.setVisibility(View.GONE);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private boolean isValid() {
        /*if(comment.getText().toString().length() == 0){
            Alert("Oops !!!", "Please enter your comments.");
            return false;
        } else*/
        if (punRaring == 0.0f) {
            Alert("Oops !!!", "Please add rating about punctuality.");
            return false;
        } else if (drivingRating == 0.0f) {
            Alert("Oops !!!", "Please add rating about driving.");
            return false;
        } else if (cleanRating == 0.0f) {
            Alert("Oops !!!", "Please add rating about cleanliness.");
            return false;
        } else if (courtesyRating == 0.0f) {
            Alert("Oops !!!", "Please add rating about courtesy.");
            return false;
        }
        return true;
    }

    public void submitRating(String favourite) {
        HashMap<String, String> params = new HashMap<>();
        params.put("booking_id", getIntent().getStringExtra("bookingID"));
        params.put("comment", comment.getText().toString());
        params.put("punctuality", "" + punRaring);
        params.put("courtesy", "" + courtesyRating);
        params.put("driving", "" + drivingRating);
        params.put("cleanliness", "" + cleanRating);
        //params.put("favrouite", favoriteDriver.isChecked() ? "1" : "0");
        if (!favourite.equals(""))
            params.put("favrouite", favourite);
        if (Internet.hasInternet(this)) {
            APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
            apiCall.execute(Config.RATING, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));

    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outerJson = new JSONObject(result);
            if (outerJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                Alert("Success!", outerJson.getString(Key.MESSAGE));
            } else {
                Alert("Alert!", outerJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(RatingToDriver.this, true);
        mDialog.setDialogTitle(title);
        if (message.equals(""))
            message = "Thanks for giving feedback.";
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.contains("Success")) {
                    if (getIntent().hasExtra("is_back")) {
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    } else {
                        Intent intent2;
                        if (session.getUserType().equals("4"))
                            intent2 = new Intent(getApplicationContext(), DriverHome.class);
                        else
                            intent2 = new Intent(getApplicationContext(), PassengerHome.class);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        RatingToDriver.this.finish();
                    }
                }
            }
        });
        mDialog.show();
    }

    private void favouriteDriver() {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(RatingToDriver.this, true);
        mDialog.setDialogTitle("BookMyRide");
        mDialog.setDialogMessage("Do you want to add driver as favourite?");
        mDialog.setNegativeButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                submitRating("1");
            }
        });
        mDialog.setPositiveButton(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                submitRating("0");
            }
        });
        mDialog.show();
    }
}
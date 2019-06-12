package com.bookmyride.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
public class RatingToPassenger extends AppCompatActivity implements AsyncTaskCompleteListener {
    TextView submit, skip;
    SessionHandler session;
    EditText comment;
    RelativeLayout back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.rate_to_passenger);
        init();
        if (getIntent().hasExtra("show")) {
            try {
                JSONObject obj = new JSONObject(getIntent().getStringExtra("rideData"));
                final View layout = getLayoutInflater().inflate(R.layout.customtoast, null);
                TextView message = (TextView) layout.findViewById(R.id.message);
                message.setText(obj.getString("message"));
                TextView title = (TextView) layout.findViewById(R.id.title);
                title.setText(getResources().getString(R.string.app_name));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
                        toast.setView(layout);
                        toast.show();
                    }
                }, 300);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (getIntent().hasExtra("is_back")) {
            skip.setVisibility(View.GONE);
            back.setVisibility(View.VISIBLE);
        } else back.setVisibility(View.GONE);
    }

    float punRaring = 0.0f, cleanRating = 0.0f, courtesyRating = 0.0f;
    RatingBar punctuality, cleanness, courtesy;

    private void init() {
        session = new SessionHandler(this);
        submit = (TextView) findViewById(R.id.submit);
        comment = (EditText) findViewById(R.id.comment);
        comment.clearFocus();
        closeKeyboard(comment);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitRating();
            }
        });
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
                    Intent intent2;
                    if (session.getUserType().equals("3"))
                        intent2 = new Intent(getApplicationContext(), PassengerHome.class);
                    else
                        intent2 = new Intent(getApplicationContext(), DriverHome.class);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            }
        });
        punctuality = (RatingBar) findViewById(R.id.rating_punctuality);
        cleanness = (RatingBar) findViewById(R.id.rating_cleanliness);
        courtesy = (RatingBar) findViewById(R.id.rating_courtesy);
        punctuality.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                punRaring = rating;
            }
        });

        courtesy.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                courtesyRating = rating;
            }
        });
        cleanness.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                cleanRating = rating;
            }
        });
    }

    private void closeKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean isValid() {
        /*if(comment.getText().toString().length() == 0){
            Alert("Oops !!!", "Please enter your comments.");
            return false;
        } else */
        if (punRaring == 0.0f) {
            Alert("Oops !!!", "Please add rating about punctuality.");
            return false;
        } else if (courtesyRating == 0.0f) {
            Alert("Oops !!!", "Please add rating about courtesy.");
            return false;
        } else if (cleanRating == 0.0f) {
            Alert("Oops !!!", "Please add rating about cleanliness.");
            return false;
        }
        return true;
    }

    public void submitRating() {
        if (isValid()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("booking_id", getIntent().getStringExtra("bookingId"));
            params.put("comment", comment.getText().toString());
            params.put("punctuality", "" + punRaring);
            params.put("courtesy", "" + courtesyRating);
            params.put("cleanliness", "" + cleanRating);
            if (Internet.hasInternet(this)) {
                APIHandler apiCall = new APIHandler(this, HTTPMethods.POST, this, params);
                apiCall.execute(Config.RATING, session.getToken());
            } else
                Alert("Alert!", getResources().getString(R.string.no_internet));
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
                Alert("Success!", outJson.getString(Key.MESSAGE));
            } else if (outJson.getInt(Key.STATUS) == APIStatus.SERVER_ERROR) {
                Alert("Success!", outJson.getString(Key.MESSAGE));
            } else {
                Alert("Error!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(RatingToPassenger.this, true);
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
                        Intent intent;
                        if (session.getUserType().equals("3"))
                            intent = new Intent(getApplicationContext(), PassengerHome.class);
                        else
                            intent = new Intent(getApplicationContext(), DriverHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        RatingToPassenger.this.finish();
                    }
                }
            }
        });
        mDialog.show();
    }
}
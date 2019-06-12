package com.bookmyride.activities.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.activities.Login;
import com.bookmyride.activities.PassengerSignup;

/**
 * Created by vinod on 2017-01-08.
 */
public class SelectUser extends AppCompatActivity implements View.OnClickListener {
    TextView asDriver, asPassenger, asBoth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);
        init();
    }

    private void init() {
        asDriver = (TextView) findViewById(R.id.as_driver);
        asPassenger = (TextView) findViewById(R.id.as_passenger);
        asBoth = (TextView) findViewById(R.id.as_both);
        asDriver.setOnClickListener(this);
        asPassenger.setOnClickListener(this);
        asBoth.setOnClickListener(this);
    }

    public void onBack(View view) {
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        SelectUser.this.finish();
    }

    private void Alert(final String title, String message) {
        final com.bookmyride.views.AlertDialog mDialog = new com.bookmyride.views.AlertDialog(SelectUser.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.equalsIgnoreCase("Success!")) {
                    Intent intent = new Intent(SelectUser.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
        mDialog.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        if (view.getId() == R.id.as_driver) {
            intent = new Intent(getApplicationContext(), Location.class);
            intent.putExtra("is_customer", "0");
        } else if (view.getId() == R.id.as_both) {
            //Alert("Ride24:7","In Progress.");
            intent = new Intent(getApplicationContext(), Location.class);
            intent.putExtra("is_customer", "1");
        } else {
            intent = new Intent(getApplicationContext(), PassengerSignup.class);
        }
        if (getIntent().hasExtra("social_data")) {
            intent.putExtra("social_data", getIntent().getStringExtra("social_data"));
            intent.putExtra("type", getIntent().getStringExtra("type"));
        }
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}

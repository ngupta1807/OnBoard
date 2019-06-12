package com.bookmyride.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.api.Key;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by vinod on 2017-01-21.
 */
public class NotificationDialogs extends AppCompatActivity {
    Dialog mDialog;
    TextView title;
    RelativeLayout Rl_ok;
    TextView message;
    private String msg, status = "", asap = "";
    String bookingId, pickUP, dropOff, driverCategory, bookingType, name, phone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showPopup();
    }

    private void showPopup() {
        mDialog = new Dialog(this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.dialog_payment);
        title = (TextView) mDialog.findViewById(R.id.txt_msg);
        message = (TextView) mDialog.findViewById(R.id.msg);
        Rl_ok = (RelativeLayout) mDialog.findViewById(R.id.ok);

        Intent intent = getIntent();
        try {
            JSONObject obj = new JSONObject(intent.getStringExtra("rideData"));
            msg = obj.getString("message");
            if (obj.has("status"))
                status = obj.getString("status");
            if (obj.has(Key.ASAP))
                asap = obj.getString(Key.ASAP);
            if (obj.has("bookingId"))
                bookingId = obj.getString("bookingId");
            if (obj.has("pickUp"))
                pickUP = obj.getString("pickUp");
            if (obj.has("dropOff"))
                dropOff = obj.getString("dropOff");
            if (obj.has("driverCategory_id"))
                driverCategory = obj.getString("driverCategory_id");
            if (obj.has("type"))
                bookingType = obj.getString("type");
            if (obj.has("name"))
                name = obj.getString("name");
            if (obj.has("phone"))
                phone = obj.getString("phone");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getIntent().hasExtra("add_extra"))
            msg = msg + " For more detail, Please visit your PRE-BOOKING RIDES section.";
        message.setText(msg);
        title.setText(getResources().getString(R.string.app_name));
        Rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                /* Intent intent = new Intent(getApplicationContext(), DriverHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out); */
                if (asap.equals("1")) {
                    //startActivity(new Intent(NotificationDialogs.this, StartRiding.class)
                    startActivity(new Intent(NotificationDialogs.this, DriverArrived.class)
                            .putExtra("bookingId", bookingId)
                            .putExtra("pickUp", pickUP)
                            .putExtra("type", bookingType)
                            .putExtra("payment_status", "0")
                            .putExtra("driverCategory", driverCategory)
                            .putExtra("passenger_name", name)
                            .putExtra("passenger_phone", phone)
                            .putExtra("dropOff", dropOff));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    NotificationDialogs.this.finish();
                } else NotificationDialogs.this.finish();
            }
        });
        mDialog.show();
        /*if(getIntent().hasExtra("end"))
            dismissDialog();*/
    }

    private void dismissDialog() {
        final Timer timer2 = new Timer();
        timer2.schedule(new TimerTask() {
            public void run() {
                mDialog.dismiss();
                timer2.cancel(); //this will cancel the timer of the system
                /*Intent intent = new Intent(getApplicationContext(), DriverHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                NotificationDialogs.this.finish();
            }
        }, 5000);
    }
}

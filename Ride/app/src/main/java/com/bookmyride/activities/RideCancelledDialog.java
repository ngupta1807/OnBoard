package com.bookmyride.activities;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;

/**
 * Created by vinod on 2017-01-21.
 */
public class RideCancelledDialog extends AppCompatActivity {
    Dialog mDialog;
    TextView title;
    RelativeLayout Rl_ok;
    TextView message;
    String msg = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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

        msg = getIntent().getStringExtra("message");

        message.setText(msg);
        if (getIntent().hasExtra("title"))
            title.setText(getIntent().getStringExtra("title"));
        else
            title.setText("Alert!");
        Rl_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                RideCancelledDialog.this.finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        mDialog.show();
        /*if(getIntent().hasExtra("end"))
            dismissDialog();*/
    }
}
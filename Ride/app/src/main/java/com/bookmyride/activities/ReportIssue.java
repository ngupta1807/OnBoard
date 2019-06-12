package com.bookmyride.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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

public class ReportIssue extends AppCompatActivity implements AsyncTaskCompleteListener {
    SessionHandler session;
    TextView submit;
    EditText queryType, subject, message;

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.submit_report);
        init();
    }

    private void init() {
        session = new SessionHandler(this);
        queryType = (EditText) findViewById(R.id.type);
        queryType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRideDialog();
            }
        });
        subject = (EditText) findViewById(R.id.subject);
        message = (EditText) findViewById(R.id.comment);
        submit = (TextView) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitIssue();
            }
        });
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
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void Alert(final String title, String message) {
        final AlertDialog mDialog = new AlertDialog(this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                if (title.contains("Success!")) {
                    ReportIssue.this.finish();
                }
            }
        });
        mDialog.show();
    }

    private void submitIssue() {
        if (isValid()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("type", queryType.getText().toString());
            params.put("subject", subject.getText().toString());
            params.put("message", message.getText().toString());
            params.put("booking_id", getIntent().getStringExtra("bookingID"));
            if (Internet.hasInternet(this)) {
                APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, params);
                apiHandler.execute(Config.REPORT_ISSUE, session.getToken());
            } else
                Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    private boolean isValid() {
        if (queryType.getText().toString().equals("")) {
            Alert("Alert!", "Please choose query type.");
            return false;
        } else if (subject.getText().toString().equals("")) {
            Alert("Alert!", "Please enter your subject.");
            return false;
        } else if (message.getText().toString().equals("")) {
            Alert("Alert!", "Please enter your message.");
            return false;
        }
        return true;
    }

    Dialog mDialog;

    public void showRideDialog() {
        mDialog = new Dialog(ReportIssue.this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Select Query Type");

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

        ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this,
                R.layout.simple_list_item, R.id.textItem, getGatewayList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                queryType.setText(parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getGatewayList() {
        String[] gateway = getResources().getStringArray(R.array.query_types);
        return gateway;
    }
}
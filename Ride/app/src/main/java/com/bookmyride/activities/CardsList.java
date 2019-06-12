package com.bookmyride.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.bookmyride.R;
import com.bookmyride.adapters.CardAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Cards;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CardsList extends AppCompatActivity implements AsyncTaskCompleteListener {
    SessionHandler session;
    ListView cardList;
    CardAdapter adapter;
    TextView addCard;
    ArrayList<Cards> cardData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_list);
        init();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Internet.hasInternet(this)) {
            getSavedCard();
        } else {
            Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    int type;

    private void init() {
        session = new SessionHandler(this);
        addCard = (TextView) findViewById(R.id.wallet_add);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CardsList.this, MyCard.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
        cardList = (ListView) findViewById(R.id.card_list);
        adapter = new CardAdapter(this, cardData);
        cardList.setAdapter(adapter);

        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                payByCard(i);
            }
        });
    }

    private void payByCard(int pos) {
        type = 2;
        HashMap<String, String> requestParam = new HashMap<>();
        requestParam.put("bookingId", session.getCurrentBookingId());
        if (getIntent().getStringExtra("tip").equals(""))
            requestParam.put("tip", "0");
        else
            requestParam.put("tip", getIntent().getStringExtra("tip"));
        requestParam.put("gateway", cardData.get(pos).getGateway());
        requestParam.put("isSavedCard", "1");
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.POST, this, requestParam);
            apiHandler.execute(Config.PAYMENT_BY_CARD, session.getToken());
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
                if (type == 1) {
                    cardData.clear();
                    JSONArray dataArray = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                    for (int i = 0; i < dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(0);
                        Cards card = new Cards();
                        card.setIcon("");
                        card.setName(dataObj.getString("first_name") + " " + dataObj.getString("last_name"));
                        card.setType(dataObj.getString(Key.TYPE));
                        card.setGateway(dataObj.getString(Key.GATEWAY));
                        card.setExpiry(dataObj.getString("expire_month") + "/" + dataObj.getString("expire_year"));
                        card.setNumber(dataObj.getString(Key.NUMBER));
                        cardData.add(card);
                    }
                    adapter.notifyDataSetChanged();
                } else {

                }
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getSavedCard() {
        type = 1;
        if (Internet.hasInternet(this)) {
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this, null);
            apiHandler.execute(Config.CREDIT_CARD, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(CardsList.this, true);
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
}
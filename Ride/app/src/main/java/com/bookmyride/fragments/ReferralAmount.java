package com.bookmyride.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.ReferralAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReferralAmount extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener {
    SessionHandler session;
    ArrayList<com.bookmyride.models.ReferralAmount> redeemData = new ArrayList<>();
    ReferralAdapter adapter;

    private ListView listview;
    private TextView noData, redeemLoyalty, redeemReferral;
    TextView totalLoyalty, totalReferral, amount;
    LinearLayout layAll, all, history;
    TextView allView, historyView;
    int pageCount, currentPage = 0;
    public static boolean hasMore = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.referral_amount, null);
        init(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.all:
                updateUI(1);
                break;
            case R.id.history:
                updateUI(3);
                break;
        }
    }

    /* @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("RIDE247 WALLET");
    } */

    private void init(View view) {
        session = new SessionHandler(getActivity());
        listview = (ListView) view.findViewById(R.id.list);
        noData = (TextView) view.findViewById(R.id.no_data);
        redeemReferral = (TextView) view.findViewById(R.id.redeem_referral);
        redeemLoyalty = (TextView) view.findViewById(R.id.redeem_loyalty);
        amount = (TextView) view.findViewById(R.id.total_amount);
        totalLoyalty = (TextView) view.findViewById(R.id.total_loyalty);
        totalReferral = (TextView) view.findViewById(R.id.total_referral);
        all = (LinearLayout) view.findViewById(R.id.all);
        all.setOnClickListener(this);
        history = (LinearLayout) view.findViewById(R.id.history);
        history.setOnClickListener(this);
        layAll = (LinearLayout) view.findViewById(R.id.lay_all);
        adapter = new ReferralAdapter(getActivity(), redeemData, null);
        listview.setAdapter(adapter);
        allView = (TextView) view.findViewById(R.id.h_view);
        historyView = (TextView) view.findViewById(R.id.d_view);
        redeemLoyalty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLoyaltyCashout && amountValue > 0) {
                    redeemLoyalty.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_color));
                    redeemReferralAmount("2");
                } else {
                    redeemLoyalty.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark));
                    Alert("Alert!", "You are not able to redeem your loyalty amount now.");
                }
            }
        });
        redeemReferral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReferralCashout && amountValue > 0) {
                    redeemReferral.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_color));
                    redeemReferralAmount("1");
                } else {
                    redeemReferral.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark));
                    Alert("Alert!", "You are not able to redeem your referral amount now.");
                }
            }
        });
    }

    private void redeemReferralAmount(String redeemType) {
        type = 1;
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("type", redeemType);
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, jsonParams);
            apiHandler.execute(Config.WALLET, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    boolean isReferralCashout = false;
    boolean isLoyaltyCashout = false;
    double amountValue = 0.0;

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 1) {
                    Alert("Success!", outJson.getString(Key.MESSAGE));
                } else {
                    if (selectedType == 1) {
                        JSONObject dataObj = outJson.getJSONObject(Key.DATA);
                        if (!dataObj.get("amount").toString().equals("null"))
                            amountValue = Double.parseDouble(dataObj.get("amount").toString());
                        amount.setText("$" + dataObj.get("amount").toString());
                        totalReferral.setText("$" + dataObj.get("referral_amount").toString());
                        totalLoyalty.setText("$" + dataObj.get("loyalty_amount").toString());
                        isReferralCashout = (dataObj.get("referralCashout").toString().equals("1")) ? true : false;
                        isLoyaltyCashout = (dataObj.get("loyaltyCashout").toString().equals("1")) ? true : false;
                        if (isReferralCashout && amountValue > 0) {
                            redeemReferral.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_color));
                        } else {
                            redeemReferral.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark));
                        }
                        if (isLoyaltyCashout && amountValue > 0) {
                            redeemLoyalty.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.driver_color));
                        } else {
                            redeemLoyalty.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.dark));
                        }
                    } else if (selectedType == 2) {
                        paidData.clear();
                        JSONArray ride_array = outJson.getJSONObject("data").getJSONArray("earnings");
                        if (ride_array.length() > 0) {
                            redeemData.clear();
                            for (int i = 0; i < ride_array.length(); i++) {
                                JSONObject ride_object = ride_array.getJSONObject(i);
                                com.bookmyride.models.ReferralAmount pojo = new com.bookmyride.models.ReferralAmount();
                                pojo.setStatus(ride_object.getString("paid_at"));
                                pojo.setReferralAmount("$" + ride_object.getString("amount"));
                                pojo.setType(ride_object.getString("earning_type_string"));
                                paidData.add(pojo);
                            }
                            setListAdapter(paidData);
                        }
                    } else if (selectedType == 3) {
                        dueData.clear();
                        JSONArray ride_array = outJson.getJSONObject("data").getJSONArray("items");
                        if (ride_array.length() > 0) {
                            redeemData.clear();
                            for (int i = 0; i < ride_array.length(); i++) {
                                JSONObject ride_object = ride_array.getJSONObject(i);
                                com.bookmyride.models.ReferralAmount pojo = new com.bookmyride.models.ReferralAmount();
                                pojo.setStatus(ride_object.getString("created_at"));
                                pojo.setReferralAmount("$" + ride_object.getString("amount"));
                                pojo.setType(ride_object.getString("earning_type_string"));
                                pojo.setBookingid(ride_object.getString("booking_id"));
                                //pojo.setVia(ride_object.getString("earning_type_string"));
                                /*if(ride_object.getString("earning_type_string").equals("Referral")) {
                                    try {
                                        pojo.setVia("Refferal via " + ride_object.getJSONObject("viaReferBy").getString("username"));
                                    } catch (Exception ex){
                                        pojo.setVia("");
                                    }
                                } else {
                                    pojo.setVia("Loyalty from Booking "+ride_object.getString("booking_id"));
                                }*/
                                if (!ride_object.getString(Key.AMOUNT).equals("null")
                                        && !ride_object.getString(Key.AMOUNT).equals("")) {
                                    dueData.add(pojo);
                                }
                                //dueData.add(pojo);
                            }
                        }
                        pageCount = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("pageCount");
                        currentPage = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("currentPage");

                        if (currentPage < pageCount)
                            hasMore = true;
                        else hasMore = false;
                        setListAdapter(dueData);
                    }
                }
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int type = 0;

    private void getReferralAmount(String endPoint) {
        type = 0;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(endPoint, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(getActivity(), true);
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

    int selectedType;

    private void updateUI(int type) {
        selectedType = type;
        if (type == 1) {
            allView.setBackgroundResource(R.color.driver_color);
            historyView.setBackgroundResource(R.color.white);
            listview.setVisibility(View.GONE);
            noData.setVisibility(View.GONE);
            layAll.setVisibility(View.VISIBLE);
            redeemReferral.setVisibility(View.VISIBLE);
            redeemLoyalty.setVisibility(View.VISIBLE);
            getReferralAmount(Config.WALLET + "/0");
        } else if (type == 3) {
            currentPage = 0;
            allView.setBackgroundResource(R.color.white);
            historyView.setBackgroundResource(R.color.driver_color);
            listview.setVisibility(View.VISIBLE);
            layAll.setVisibility(View.GONE);
            setListAdapter(dueData);
            redeemReferral.setVisibility(View.GONE);
            redeemLoyalty.setVisibility(View.GONE);
            getReferralAmount(Config.WALLET + "?expand=viaReferBy");
        }
    }

    ArrayList<com.bookmyride.models.ReferralAmount> paidData = new ArrayList<>();
    ArrayList<com.bookmyride.models.ReferralAmount> dueData = new ArrayList<>();

    private void setListAdapter(final ArrayList<com.bookmyride.models.ReferralAmount> paymentData) {
        adapter = new ReferralAdapter(getActivity(), paymentData, loadMore);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                paymentData.get(position);

            }
        });
        if (paymentData.size() > 0) {
            noData.setVisibility(View.GONE);
        } else {
            noData.setVisibility(View.VISIBLE);
            listview.setEmptyView(noData);
            layAll.setVisibility(View.GONE);
        }
    }

    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (hasMore) {
                getReferralAmount(Config.WALLET + "?expand=viaReferBy");
            }
        }
    };
}
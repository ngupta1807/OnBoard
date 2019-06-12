package com.bookmyride.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.PaymentAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.Payment;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PayMe extends Fragment implements AsyncTaskCompleteListener,
        View.OnClickListener {
    SessionHandler session;
    EditText accountNumber, holderName, bankName, bsb;
    TextView save;
    //RelativeLayout overLay;
    RelativeLayout layList;
    LinearLayout layInfo;
    LinearLayout upComing, history, detail;
    TextView upcomingView, historyView, detailView;
    ListView list;
    TextView noData;
    PaymentAdapter adapter;
    ArrayList<Payment> allData = new ArrayList<Payment>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pay_me, null);
        init(view);
        updateUI(1);
        return view;
    }

    public static boolean hasMore = false;
    int pageCount, currentPage = 0;
    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (hasMore) {
                String mType = "";
                if (type == 1)
                    mType = "/rideearning?type=upcoming";
                else if (type == 2)
                    mType = "/rideearning?type=history";
                getList(mType);
            }
        }
    };

    private void init(View view) {
        session = new SessionHandler(getActivity());
        accountNumber = (EditText) view.findViewById(R.id.ac_number);
        holderName = (EditText) view.findViewById(R.id.ac_name);
        holderName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        bankName = (EditText) view.findViewById(R.id.bank_name);
        bankName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        bsb = (EditText) view.findViewById(R.id.bsb);
        save = (TextView) view.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save.getText().toString().equals("Edit")) {
                    //overLay.setVisibility(View.GONE);
                    updateInputs();
                    save.setText("Update");
                } else {
                    if (isValidate())
                        saveBankDetail();
                }
            }
        });
        /* overLay = (RelativeLayout) view.findViewById(R.id.lay_overlay);
        overLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        }); */
        layInfo = (LinearLayout) view.findViewById(R.id.lay_info);
        layList = (RelativeLayout) view.findViewById(R.id.lay_list);

        noData = (TextView) view.findViewById(R.id.no_data);
        upComing = (LinearLayout) view.findViewById(R.id.upcoming);
        upComing.setOnClickListener(this);
        detail = (LinearLayout) view.findViewById(R.id.detail);
        detail.setOnClickListener(this);
        history = (LinearLayout) view.findViewById(R.id.history);
        history.setOnClickListener(this);

        upcomingView = (TextView) view.findViewById(R.id.u_view);
        historyView = (TextView) view.findViewById(R.id.h_view);
        detailView = (TextView) view.findViewById(R.id.d_view);

        list = (ListView) view.findViewById(R.id.list);
        list = (ListView) view.findViewById(R.id.list);
        adapter = new PaymentAdapter(getActivity(), allData, loadMore);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
        if (allData.size() == 0)
            noData.setVisibility(View.VISIBLE);
        else noData.setVisibility(View.GONE);

        holderName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    holderName.requestFocus();
                    holderName.setFocusable(true);
                    holderName.setCursorVisible(true);
                    holderName.setFocusableInTouchMode(true);
                    holderName.setSelection(holderName.getText().length());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(holderName, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        accountNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    accountNumber.requestFocus();
                    accountNumber.setFocusable(true);
                    accountNumber.setCursorVisible(true);
                    accountNumber.setFocusableInTouchMode(true);
                    accountNumber.setSelection(accountNumber.getText().length());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(accountNumber, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        bankName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    bankName.requestFocus();
                    bankName.setFocusable(true);
                    bankName.setCursorVisible(true);
                    bankName.setFocusableInTouchMode(true);
                    bankName.setSelection(bankName.getText().length());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(bankName, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        bsb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    bsb.requestFocus();
                    bsb.setFocusable(true);
                    bsb.setCursorVisible(true);
                    bsb.setFocusableInTouchMode(true);
                    bsb.setSelection(bsb.getText().length());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(bsb, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upcoming:
                updateUI(1);
                break;
            case R.id.history:
                updateUI(2);
                break;
            case R.id.detail:
                updateUI(3);
                break;
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
                if (type == 3) {
                    JSONObject innerObj = outJson.getJSONObject(Key.DATA);
                    accountNumber.setText(innerObj.getString(Key.NUMBER));
                    holderName.setText(innerObj.getString(Key.ACCOUNT_OWNER_NAME));
                    bankName.setText(innerObj.getString(Key.NAME));
                    bsb.setText(innerObj.getString(Key.ROUTING));
                    save.setText("Edit");
                    disableInputs();
                    //overLay.setVisibility(View.VISIBLE);
                } else if (type == 4) {
                    save.setText("Edit");
                    disableInputs();
                    //overLay.setVisibility(View.VISIBLE);
                    Alert("Success!", outJson.getString(Key.MESSAGE));
                } else if (type == 1 || type == 2) {
                    JSONArray items = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                    if (!hasMore)
                        allData.clear();

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i);
                        Payment payment = new Payment();
                        payment.setPaidStatus(obj.getString("paid_status"));
                        payment.setAmount("$" + obj.getString(Key.AMOUNT));
                        payment.setBookingID(obj.getString("booking_id"));
                        //payment.setCurrency(obj.getString(Key));
                        payment.setDate(obj.getString(Key.CREATED_AT));
                        payment.setType(obj.getString("earning_type_string"));
                        if (!obj.getString(Key.AMOUNT).equals("null")
                                && !obj.getString(Key.AMOUNT).equals("")) {
                            allData.add(payment);
                        }
                        //allData.add(payment);
                    }
                    adapter.notifyDataSetChanged();
                    if (allData.size() > 0) {
                        list.setVisibility(View.VISIBLE);
                        noData.setVisibility(View.GONE);
                    } else {
                        list.setVisibility(View.GONE);
                        noData.setVisibility(View.VISIBLE);
                    }
                    pageCount = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("pageCount");
                    currentPage = outJson.getJSONObject(Key.DATA).getJSONObject("_meta").getInt("currentPage");

                    if (currentPage < pageCount)
                        hasMore = true;
                    else hasMore = false;
                }
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int type = 0;

    private void getBankDetail() {
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.BANK_DETAIL + "/0", session.getToken());
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

    private boolean isValidate() {
        if (holderName.getText().toString().trim().isEmpty()) {
            Alert("Oops !!!", "Please enter account holder name.");
            return false;
        } else if (accountNumber.getText().toString().length() == 0 || accountNumber.getText().toString().length() < 6) {
            Alert("Oops !!!", "Please enter valid bank account number.");
            return false;
        } else if (bankName.getText().toString().length() == 0) {
            Alert("Oops !!!", "Please enter bank name.");
            return false;
        } else if (bsb.getText().toString().length() == 0 || bsb.getText().toString().length() < 6) {
            Alert("Oops !!!", "Please enter valid 6 digit BSB number.");
            return false;
        }
        return true;
    }

    private void saveBankDetail() {
        type = 4;
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put(Key.NAME, bankName.getText().toString());
        jsonParams.put(Key.ACCOUNT_OWNER_NAME, holderName.getText().toString());
        jsonParams.put(Key.NUMBER, accountNumber.getText().toString());
        jsonParams.put(Key.ROUTING, bsb.getText().toString());
        APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, jsonParams);
        apiHandler.execute(Config.BANK_DETAIL, session.getToken());
    }

    private void updateUI(int type) {
        this.type = type;
        if (type == 1) {
            hasMore = false;
            currentPage = 0;
            list.smoothScrollToPosition(0);
            upcomingView.setBackgroundResource(R.color.driver_color);
            historyView.setBackgroundResource(R.color.white);
            detailView.setBackgroundResource(R.color.white);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
        } else if (type == 2) {
            hasMore = false;
            currentPage = 0;
            list.smoothScrollToPosition(0);
            upcomingView.setBackgroundResource(R.color.white);
            historyView.setBackgroundResource(R.color.driver_color);
            detailView.setBackgroundResource(R.color.white);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
        } else if (type == 3) {
            upcomingView.setBackgroundResource(R.color.white);
            historyView.setBackgroundResource(R.color.white);
            detailView.setBackgroundResource(R.color.driver_color);
            layList.setVisibility(View.GONE);
            layInfo.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
        }
        if (type == 3) {
            getBankDetail();
        } else {
            String mType = "";
            if (type == 1)
                mType = "/rideearning?type=upcoming";
            else if (type == 2)
                mType = "/rideearning?type=history";
            getList(mType);
        }
    }

    private void getList(String type) {
        //historyData.clear();
        if (Internet.hasInternet(getActivity())) {
            APIHandler mobileAPI = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(Config.WALLET + type + "&page=" + (++currentPage), session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void updateInputs() {
        holderName.setEnabled(true);
        accountNumber.setEnabled(true);
        bankName.setEnabled(true);
        bsb.setEnabled(true);
    }

    private void disableInputs() {
        accountNumber.setEnabled(false);
        accountNumber.setCursorVisible(false);
        holderName.setEnabled(false);
        holderName.setCursorVisible(false);
        bankName.setEnabled(false);
        bankName.setCursorVisible(false);
        bsb.setEnabled(false);
        bsb.setCursorVisible(false);
    }
}
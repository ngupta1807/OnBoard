package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.HistoryAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.BankDetail;
import com.grabid.models.PaymentHistory;
import com.grabid.models.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vinod on 10/14/2016.
 */
public class PayMe extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    EditText bank, holderName, accNumber, branchCode, swiftCode;
    TextView submit;
    String id;
    private static int GET_CARD = 60;
    private static int ADD_CARD = 61;
    private static int UPDATE_CARD = 62;
    private static int DELETE_CARD = 63;
    private static int GET_UPCOMING = 64;
    private static int GET_HISTORY = 65;
    int type = 0;
    RelativeLayout overlay, layInfo, layList;
    TextView upComing, history, detail;
    ListView list;
    SessionManager session;
    TextView noData;
    HistoryAdapter adapter;
    TabLayout mTabLayout;
    ArrayList<PaymentHistory> historyData = new ArrayList<PaymentHistory>();
    UserInfo userInfo;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initTopBar();
        View view = inflater.inflate(R.layout.bank_detail, null);
        init(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                String typr = bundle.getString("UITYPE");
                if (typr.contentEquals("3")) {
//                    updateUI(3);
                    mTabLayout.getTabAt(2).select();
                    mTabLayout.setVisibility(View.GONE);
                } else {
//                    updateUI(1);
                    mTabLayout.getTabAt(0).select();
                }
            } catch (Exception e) {
                e.toString();
            }
        } else {
//            updateUI(1);
            if (userInfo.getBankDetail() != null && !userInfo.getBankDetail().contentEquals("null"))
                mTabLayout.getTabAt(0).select();
            else {
                mTabLayout.getTabAt(2).select();
                mTabLayout.setVisibility(View.GONE);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(getBankInfoRec, new IntentFilter("getbankinfo"));

    }

    @Override
    public void onStart() {
        super.onStart();
        HomeActivity.IsBankDetail = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.IsBankDetail = false;
    }

    private BroadcastReceiver getBankInfoRec = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String str = intent.getAction();
            String del_id = "";
            try {
                if (str != null && str.contentEquals("getbankinfo")) {

                    getBankInfo();
                    //page = 1;
                    //getFeedbacks(type);

                }
            } catch (Exception e) {
                e.toString();
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(getBankInfoRec);

    }

    public String makeFirstLetterCapitel(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }


    public void onTabTapped(int position) {
        updateUI(position);
    }

    public void UpdateDesign(int menuType) {
        HomeActivity.title.setText(getResources().getString(R.string.pay_me));
        if (menuType == 1)
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
    }

    private void initTopBar() {
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.pay_me));
        //  HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.edit.setVisibility(View.GONE);
                overlay.setVisibility(View.GONE);
                submit.setText("UPDATE");
                accNumber.setText("");
                branchCode.setText("");
            }
        });
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        userInfo = session.getUserDetails();
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        bank = (EditText) view.findViewById(R.id.bank);
        holderName = (EditText) view.findViewById(R.id.holder_name);
        accNumber = (EditText) view.findViewById(R.id.acc_number);
        branchCode = (EditText) view.findViewById(R.id.branch_code);
        swiftCode = (EditText) view.findViewById(R.id.swift_code);
        overlay = (RelativeLayout) view.findViewById(R.id.layOver);
        overlay.setOnClickListener(this);
        layInfo = (RelativeLayout) view.findViewById(R.id.lay_info);
        layList = (RelativeLayout) view.findViewById(R.id.lay_list);
        noData = (TextView) view.findViewById(R.id.no_data);
        upComing = (TextView) view.findViewById(R.id.upcoming);
        upComing.setOnClickListener(this);
        detail = (TextView) view.findViewById(R.id.detail);
        detail.setOnClickListener(this);
        history = (TextView) view.findViewById(R.id.history);
        history.setOnClickListener(this);
        list = (ListView) view.findViewById(R.id.list);
        adapter = new HistoryAdapter(getActivity(), historyData, "bank");
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String backStateName = this.getClass().getName();
                Bundle bundle = new Bundle();
                HashMap<String, PaymentHistory> data = new HashMap<String, PaymentHistory>();
                data.put("data", historyData.get(i));
                bundle.putString("delivery_id", historyData.get(i).getDeliveryId());
                bundle.putSerializable("data", data);
                bundle.putString("incoming_type", "bank");
                bundle.putSerializable("incoming_delivery_type", "");
                Fragment fragment = new WalletTransactionDetails();
                fragment.setArguments(bundle);
                getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                if (type == GET_HISTORY || type == GET_UPCOMING) {
                    firstVisibleItem = firstVisibleItemm;
                    visibleItemCount = visibleItemCountt;
                    totalItemCount = totalItemCountt;
                }


            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (type == GET_HISTORY || type == GET_UPCOMING) {
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if (firstVisibleItem > 0 && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                        if (!loadingMore) {
                            if (totalCount >= page) {
                                loadingMore = true;
                                if (type == GET_UPCOMING)
                                    getList(1);
                                else getList(2);
                            }
                        }
                    }

                    //  new AsyncTask().execute();

                    //get next 10-20 items(your choice)items

                }
            }
        });
        bank.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (bank.getText().toString().length() > 0)
                    bank.setText(makeFirstLetterCapitel(bank.getText().toString()));
            }
        });
        holderName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (holderName.getText().toString().length() > 0)
                    holderName.setText(makeFirstLetterCapitel(holderName.getText().toString()));
            }
        });
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.v("", String.valueOf(position));
                onTabTapped(tab.getPosition() + 1);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Log.v("", String.valueOf(position));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabTapped(tab.getPosition() + 1);
            }
        });
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d("data", result);
        handleResponse(result);
    }

    private void clearData() {
        bank.setText("");
        holderName.setText("");
        accNumber.setText("");
        branchCode.setText("");
        swiftCode.setText("");
        submit.setText("SAVE DETAILS");
        overlay.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.edit.setVisibility(View.GONE);
        session.saveBankDetail("null");

    }


    private void handleResponse(String result) {
        if (type == DELETE_CARD) {
            if (Integer.parseInt(result) == APIStatus.SUCCESS) {
                clearData();
                messageDialogMap(getActivity(), "Success!", getResources().getString(R.string.wallet_delete));
                // showMessage("Success!", getResources().getString(R.string.wallet_delete));
            } else
                showMessage("Error!", getResources().getString(R.string.fail));
        } else {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    if (type == ADD_CARD) {
                        try {
                            JSONObject cardObj = outJson.getJSONObject(Config.DATA);
                            BankDetail card = new BankDetail();
                            card.setId(cardObj.getString(Keys.KEY_ID));
                            card.setUserID(cardObj.getString(Keys.KEY_USER_ID));
                            card.setHolderName(cardObj.getString(Keys.HOLDER_NAME));
                            card.setBankName(cardObj.getString(Keys.BANK_NAME));
                            card.setBranchCode(cardObj.getString(Keys.BRANCH_CODE));
                            //card.setHashNumber(cardObj.getString(Keys.HASH_ACCOUNT_NUMBER));
                            //card.setSwiftCode(cardObj.getString(Keys.SWIFT_CODE));
                            card.setAccountNumber(cardObj.getString(Keys.ACCOUNT_NUMBER));
                            appendData(card);
                            session.saveBankDetail(outJson.optJSONObject(Config.DATA).toString());
                            HomeActivity.edit.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.toString();
                        }
                        //showMessage("Success!", getResources().getString(R.string.wallet_success));
                        messageDialog(getActivity(), "Success!", getResources().getString(R.string.wallet_success));
                    } else if (type == UPDATE_CARD) {
                        try {
                            JSONObject cardObj = outJson.getJSONObject(Config.DATA);
                            BankDetail card = new BankDetail();
                            card.setId(cardObj.getString(Keys.KEY_ID));
                            card.setUserID(cardObj.getString(Keys.KEY_USER_ID));
                            card.setHolderName(cardObj.getString(Keys.HOLDER_NAME));
                            card.setBankName(cardObj.getString(Keys.BANK_NAME));
                            card.setBranchCode(cardObj.getString(Keys.BRANCH_CODE));
                            //card.setHashNumber(cardObj.getString(Keys.HASH_ACCOUNT_NUMBER));
                            //card.setSwiftCode(cardObj.getString(Keys.SWIFT_CODE));
                            card.setAccountNumber(cardObj.getString(Keys.ACCOUNT_NUMBER));
                            appendData(card);
                            session.saveBankDetail(outJson.optJSONObject(Config.DATA).toString());
                            HomeActivity.edit.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            e.toString();
                        }
                        messageDialog(getActivity(), "Success!", getResources().getString(R.string.wallet_update));

                        //    showMessage("Success!", getResources().getString(R.string.wallet_update));
                    } else if (type == GET_CARD) {
                        JSONObject innerObject = outJson.getJSONObject(Config.DATA);
                        if (innerObject.get(Config.BANKDETAIL) instanceof JSONObject) {
                            JSONObject cardObj = innerObject.getJSONObject(Config.BANKDETAIL);
                            BankDetail card = new BankDetail();
                            try {
                                card.setId(cardObj.getString(Keys.KEY_ID));
                            }catch (Exception ex){
                                card.setId("");
                            }
                            //card.setId(cardObj.getString(Keys.KEY_JOB_ID));
                            card.setUserID(cardObj.getString(Keys.KEY_USER_ID));
                            card.setHolderName(cardObj.getString(Keys.HOLDER_NAME));
                            card.setBankName(cardObj.getString(Keys.BANK_NAME));
                            card.setBranchCode(cardObj.getString(Keys.BRANCH_CODE));
                            //card.setHashNumber(cardObj.getString(Keys.HASH_ACCOUNT_NUMBER));
                            //card.setSwiftCode(cardObj.getString(Keys.SWIFT_CODE));
                            card.setAccountNumber(cardObj.getString(Keys.ACCOUNT_NUMBER));
                            appendData(card);
                            try {
                                session.saveBankDetail(outJson.optJSONObject(Config.DATA).toString());
                            } catch (Exception e) {
                                e.toString();
                            }
                        }
                    } else if (type == GET_UPCOMING) {
                        if (page == 1)
                            historyData.clear();
                        ++page;
                        loadingMore = false;
                        try {
                            JSONArray innerObject = outJson.getJSONArray(Config.DATA);
                            // if (innerObject.has(Config.PAYMENT)) {
                            //    JSONArray payment = innerObject.getJSONArray(Config.PAYMENT);
                            for (int i = 0; i < innerObject.length(); i++) {
                                JSONObject cardObj = innerObject.getJSONObject(i);
                                PaymentHistory card = new PaymentHistory();
                                card.setPayable(0);
                                //  card.setId(cardObj.getString(Keys.KEY_ID));
                                card.setDeliveryTitle(cardObj.getString(Keys.DELIVERY_TITLE));
                                //   card.setAmount(cardObj.getString(Keys.AMOUNT));
                                try {
                                    card.setDeliveryId(cardObj.getString(Keys.KEY_JOB_ID));
                                }catch (Exception ex){
                                    card.setDeliveryId("");
                                }
                                card.setPayToDriverAmount(cardObj.getString(Keys.PAY_TO_DRIVER_AMOUNT));
                                card.setDate((cardObj.optString(Keys.CREATED_AT)));
                                card.setPaymentMethodName(cardObj.optString(Keys.TYPE));
                                card.setDelStatus(cardObj.optString(Keys.DELIVERY_STATUS));

                                //  card.setCommision_amount(cardObj.optString(Keys.COMMISION_AMOUNT));
                                //  JSONArray childArray = cardObj.optJSONArray("payment_detail");
                              /*  if (childArray != null && childArray.length() > 0) {
                                    JSONObject childJson = childArray.optJSONObject(0);
                                    if (childJson.has(Keys.PAYMENT_METHOD)) {
                                        JSONObject innerJson = childJson.optJSONObject(Keys.PAYMENT_METHOD);
                                        card.setPaymentMethodName(innerJson.optString(Keys.PAYMENT_METHOD_NAME));
                                    }

                                }*/
                                historyData.add(card);
                            }
                            list.setVisibility(View.VISIBLE);
                            noData.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            if (historyData.size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                                list.setVisibility(View.GONE);
                            } else {
                                noData.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                            }
                           /* try {
                                if (innerObject.has("_meta")) {
                                    JSONObject metaCount = innerObject.getJSONObject("_meta");
                                    totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                                    Log.v("totalcount", String.valueOf(totalCount));

                                }
                            } catch (Exception e) {
                                e.toString();
                            }*/
                        } catch (Exception ex) {
                            noData.setVisibility(View.VISIBLE);
                            list.setVisibility(View.GONE);
                        }
                        //appendData(card);
                    } else if (type == GET_HISTORY) {
                        if (page == 1)
                            historyData.clear();
                        ++page;
                        loadingMore = false;
                        try {
                            JSONArray innerObject = outJson.getJSONArray(Config.DATA);
                            // if (innerObject.has(Config.PAYMENT)) {
                            //   JSONArray payment = innerObject.getJSONArray(Config.PAYMENT);
                            for (int i = 0; i < innerObject.length(); i++) {
                                JSONObject cardObj = innerObject.getJSONObject(i);
                                PaymentHistory card = new PaymentHistory();
                                card.setPayable(1);
                                //   card.setId(cardObj.getString(Keys.KEY_ID));
                                // card.setUserId(cardObj.getString(Keys.KEY_USER_ID));
                                card.setDeliveryTitle(cardObj.getString(Keys.DELIVERY_TITLE));
                                // card.setAmount(cardObj.getString(Keys.AMOUNT));
                                // card.setCardNumber(cardObj.getString(Keys.CARD_NUMBER));
                                //   card.setCardType(cardObj.getString(Keys.CARD_TYPE));
                                try {
                                    card.setDeliveryId(cardObj.getString(Keys.KEY_JOB_ID));
                                }catch (Exception ex){
                                    card.setDeliveryId("");
                                }
                                card.setPayToDriverAmount(cardObj.getString(Keys.PAY_TO_DRIVER_AMOUNT));
                                //card.setPayToDriverStatus(cardObj.getString(Keys.PAY_TO_DRIVER_STATUS));
                                // card.setPayToDriverAt(cardObj.getString(Keys.PAY_TO_DRIVER_AT));
                                //  card.setStatus(cardObj.getString(Keys.STATUS));
                                // card.setTransactionId(cardObj.getString(Keys.TRANSACTION_ID));
                                card.setDate((cardObj.optString(Keys.CREATED_AT)));
                                //   card.setCommision_amount(cardObj.optString(Keys.COMMISION_AMOUNT));
                                card.setPaymentMethodName(cardObj.optString(Keys.TYPE));
                                card.setDelStatus(cardObj.optString(Keys.DELIVERY_STATUS));
                                   /* JSONArray childArray = cardObj.optJSONArray("payment_detail");
                                    if (childArray != null && childArray.length() > 0) {
                                        JSONObject childJson = childArray.optJSONObject(0);
                                        if (childJson.has(Keys.PAYMENT_METHOD)) {
                                            JSONObject innerJson = childJson.optJSONObject(Keys.PAYMENT_METHOD);
                                            card.setPaymentMethodName(innerJson.optString(Keys.PAYMENT_METHOD_NAME));
                                        }

                                    }*/
                                historyData.add(card);
                                // }
                                list.setVisibility(View.VISIBLE);
                                noData.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }
                            if (historyData.size() == 0) {
                                noData.setVisibility(View.VISIBLE);
                                list.setVisibility(View.GONE);
                            } else {
                                noData.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                            }
                           /* try {
                                if (innerObject.has("_meta")) {
                                    JSONObject metaCount = innerObject.getJSONObject("_meta");
                                    totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                                    Log.v("totalcount", String.valueOf(totalCount));

                                }
                            } catch (Exception e) {
                                e.toString();
                            }*/
                        } catch (Exception ex) {
                            noData.setVisibility(View.VISIBLE);
                            list.setVisibility(View.GONE);
                        }
                    }
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {

                    boolean Ismessage = false;
                    if (!Ismessage) {
                        try {
                            if (outJson.optJSONArray("data") != null) {
                                try {
                                    JSONObject Json = outJson.optJSONArray("data").optJSONObject(0);
                                    if (Json.has(Config.MESSAGE)) {
                                        AlertManager.messageDialog(getActivity(), "Alert!", Json.optString(Config.MESSAGE));
                                        Ismessage = true;
                                    } else
                                        Ismessage = false;
                                } catch (Exception e) {
                                    e.toString();
                                }
                            }
                        } catch (Exception e) {
                            e.toString();
                        }
                    }

                    if (!Ismessage)
                        showMessage("Error", outJson.getString(Config.MESSAGE));
                } else {
                    showMessage("Error", getResources().getString(R.string.no_response));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    BankDetail info;

    private void appendData(BankDetail card) {
        info = card;
        bank.setText(card.getBankName());
        id = card.getId();
        holderName.setText(card.getHolderName());
        branchCode.setText(card.getBranchCode());
        accNumber.setText(card.getAccountNumber());
        //swiftCode.setText(card.getSwiftCode());
        submit.setText("DELETE");
        HomeActivity.edit.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if (submit.getText().toString().equals("UPDATE")) {
                    if (isValid())
                        updateBankDetail();
                } else if (submit.getText().toString().equals("DELETE")) {
                    showDeleteDialog();
                    //deleteBankDetail();
                } else {
                    if (isValid())
                        saveBankDetail();
                }
                break;
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

    private boolean isValid() {
        if (bank.getText().toString().trim().length() == 0 && holderName.getText().toString().trim().length() == 0
                && accNumber.getText().toString().trim().length() == 0 && branchCode.getText().toString().trim().length() == 0) {
            showMessage(TITLE, getActivity().getString(R.string.completeallfield));
            return false;
        }
        if (bank.getText().toString().trim().length() == 0) {
            showMessage(TITLE, getActivity().getString(R.string.bankname));
            return false;
        } else if (holderName.getText().toString().trim().length() == 0) {
            showMessage(TITLE, getActivity().getString(R.string.accountholdname));
            return false;
        } else if (accNumber.getText().toString().trim().length() == 0) {
            showMessage(TITLE, getActivity().getString(R.string.accountno));
            return false;
        } else if (accNumber.getText().toString().trim().length() < 5) {
            showMessage(TITLE, getActivity().getString(R.string.accountnomax));
            return false;
        } else if (branchCode.getText().toString().trim().length() == 0) {
            showMessage(TITLE, getActivity().getString(R.string.bsbno));
            return false;
        } else if (branchCode.getText().toString().trim().length() < 6) {
            showMessage(TITLE, getActivity().getString(R.string.bsbnomax));
            return false;
        }
        /*else if(swiftCode.getText().toString().trim().length() < 4){
            showMessage(TITLE, "Please enter swift code.");
            return false;
        }*/
        return true;
    }

    String TITLE = "Alert!";

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    public void messageDialog(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getFragmentManager().popBackStack();

            }
        });
        Dialog d = builder.create();
        d.show();
    }

    public void messageDialogMap(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Fragment fragment = new HomeMap();
                String backStateName = "com.grabid.activities.HomeActivity";
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();

            }
        });
        Dialog d = builder.create();
        d.show();
    }

    private void getBankInfo() {
        this.type = GET_CARD;
        String url = Config.SERVER_URL + Config.BANK_DETAIL + "/0";
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void saveBankDetail() {
        type = ADD_CARD;
        String url = Config.SERVER_URL + Config.BANK_DETAIL + "/0";
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.BANK_NAME, bank.getText().toString());
        params.put(Keys.HOLDER_NAME, holderName.getText().toString());
        params.put(Keys.ACCOUNT_NUMBER, accNumber.getText().toString());
        params.put(Keys.BRANCH_CODE, branchCode.getText().toString());
        //params.put(Keys.SWIFT_CODE, swiftCode.getText().toString());
        Log.d("prms", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void updateBankDetail() {
        type = UPDATE_CARD;
        String url = Config.SERVER_URL + Config.BANK_DETAIL + "/0";
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.BANK_NAME, bank.getText().toString());
        params.put(Keys.HOLDER_NAME, holderName.getText().toString());
        params.put(Keys.ACCOUNT_NUMBER, accNumber.getText().toString());
        params.put(Keys.BRANCH_CODE, branchCode.getText().toString());
        //params.put(Keys.SWIFT_CODE, swiftCode.getText().toString());
//        params.put(Keys.ACCOUNT_NUMBER, info.getAccountNumber());
        Log.d("prms", params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void deleteBankDetail() {
        type = DELETE_CARD;
        String url = Config.SERVER_URL + Config.BANK_DETAIL + "/" + id;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.DELETE, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(getResources().getString(R.string.deletebankdetail));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteBankDetail();
            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void updateUI(int type) {
        if (type == 1) {
            upComing.setBackgroundResource(R.drawable.border_back);
            upComing.setTextColor(Color.WHITE);
            history.setBackgroundResource(R.drawable.border_black);
            history.setTextColor(Color.BLACK);
            detail.setBackgroundResource(R.drawable.border_black);
            detail.setTextColor(Color.BLACK);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);

        } else if (type == 2) {
            upComing.setBackgroundResource(R.drawable.border_black);
            upComing.setTextColor(Color.BLACK);
            history.setBackgroundResource(R.drawable.border_back);
            history.setTextColor(Color.WHITE);
            detail.setBackgroundResource(R.drawable.border_black);
            detail.setTextColor(Color.BLACK);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            upComing.setBackgroundResource(R.drawable.border_black);
            upComing.setTextColor(Color.BLACK);
            history.setBackgroundResource(R.drawable.border_black);
            history.setTextColor(Color.BLACK);
            detail.setBackgroundResource(R.drawable.border_back);
            detail.setTextColor(Color.WHITE);
            layList.setVisibility(View.GONE);
            layInfo.setVisibility(View.VISIBLE);
        }
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        overlay.setVisibility(View.GONE);
        if (type == 3) {
            this.type = GET_CARD;
            getBankInfo();
        } else {
            page = 1;
            totalCount = 1;
            getList(type);
        }
    }

    private void getList(int type) {
        if (page == 1)
            historyData.clear();
        String url = Config.SERVER_URL;
        switch (type) {
            case 1:
                this.type = GET_UPCOMING;
                url = url + Config.BANK_UPCOMING + "?page=" + page;
                break;
            case 2:
                this.type = GET_HISTORY;
                url = url + Config.BANK_HISTORY + "?page=" + page;
                break;
        }
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }
}
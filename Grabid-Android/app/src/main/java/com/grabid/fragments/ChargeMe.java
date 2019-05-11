package com.grabid.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.grabid.common.BackPressed;
import com.grabid.common.EditTextListener;
import com.grabid.common.Internet;
import com.grabid.common.MonthYearPickerDialog;
import com.grabid.common.SessionManager;
import com.grabid.models.Card;
import com.grabid.models.Payment;
import com.grabid.models.PaymentHistory;
import com.grabid.models.UserInfo;
import com.grabid.util.AsteriskStarTransformationMethod;
import com.grabid.views.MonitoringEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vinod on 10/14/2016.
 */
public class ChargeMe extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener, BackPressed {
    EditText holderName, cvv;
    MonitoringEditText cardNumber;
    TextView submit, cardType, expiry;
    private static int GET_CARD_TYPES = 49;
    private static int ADD_CARD = 51;
    private static int UPDATE_CARD = 50;
    private static int DELETE_CARD = 52;
    private static int GET_CARD = 55;
    private static int GET_UPCOMING = 56;
    private static int GET_HISTORY = 57;
    private static int GET_PENALTY = 58;
    int type = 0;
    String id;
    RelativeLayout overlay, layInfo, layList;
    TextView upComing, history, detail;
    ListView list;
    HistoryAdapter adatper;
    SessionManager session;
    TextView noData;
    TabLayout mTabLayout;
    UserInfo userInfo;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    int totalCount = 1;
    boolean loadingMore = false;
    LinearLayout mLayRight, mLayLeft;
    LinearLayout.LayoutParams halfview, fullview;
    int menutype = 0;


    ;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("", "oncreate");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_credit_card, null);
        init(view);
        initTopBar();
        Bundle bundle = getArguments();
        if (bundle != null) {
            try {
                String typr = bundle.getString("UITYPE");
                if (typr.contentEquals("2")) {
//                    updateUI(2);
                    mTabLayout.getTabAt(2).select();
                    mTabLayout.setVisibility(View.GONE);

                } else if (typr.contentEquals("5")) {
                    mTabLayout.getTabAt(2).select();
                } else {
                    mTabLayout.getTabAt(0).select();
//                    updateUI(1);
                }
            } catch (Exception e) {
                e.toString();
            }
        } else {
            //   updateUI(1);
            if (userInfo.getCreditCard() != null && !userInfo.getCreditCard().contentEquals("null"))
                mTabLayout.getTabAt(0).select();
            else {
                mTabLayout.getTabAt(2).select();
                mTabLayout.setVisibility(View.GONE);
            }
        }

        // updateUI(1);
        return view;
    }

    public String makeFirstLetterCapitel(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    private Fragment getVisibleFragment() {
        return getActivity().getFragmentManager().findFragmentById(R.id.container);
    }

    public void UpdateDesign(int menuType) {
        this.menutype = menuType;
        HomeActivity.title.setText(getResources().getString(R.string.charge_me));
        if (menuType == 1)
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        else
            HomeActivity.navDrawer.setBackgroundResource(R.drawable.back_icon);
    }

    @Override
    public void onStart() {
        super.onStart();
        //  HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.IsCreditCard = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        HomeActivity.IsCreditCard = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(getCreditInfoRec, new IntentFilter("getcreditinfo"));

    }

    private BroadcastReceiver getCreditInfoRec = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String str = intent.getAction();
            String del_id = "";
            try {
                if (str != null && str.contentEquals("getcreditinfo")) {
                    getSavedCreditCard();
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
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(getCreditInfoRec);

    }

    private void initTopBar() {
        HomeActivity.edit.setVisibility(View.GONE);
        mLayRight.setVisibility(View.VISIBLE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
//        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.title.setText(getResources().getString(R.string.charge_me));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.edit.setBackgroundResource(R.drawable.edit_top);
        HomeActivity.addmore.setVisibility(View.GONE);
        HomeActivity.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.edit.setVisibility(View.GONE);
                mLayRight.setVisibility(View.VISIBLE);
                overlay.setVisibility(View.GONE);
                //  cardType.setText("");
                //  holderName.setText("");
                cardNumber.setText("");
                expiry.setText("");
                cvv.setText("");
                //setEditTextMaxLength(cardNumber, 16);
                mLayLeft.setLayoutParams(halfview);
                submit.setText("UPDATE");
            }
        });
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        builder.setTitle("Alert!");
        builder.setMessage(message);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void setEditTextMaxLength(final EditText editText, int length) {
        try {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(length);
            editText.setFilters(FilterArray);
        } catch (Exception e) {
            e.toString();
        }
    }

    int abnCount = 0;

    private void init(View view) {
        session = new SessionManager(getActivity());
        userInfo = session.getUserDetails();
        submit = (TextView) view.findViewById(R.id.submit);
        submit.setOnClickListener(this);
        cardType = (TextView) view.findViewById(R.id.card_type);
        mTabLayout = (TabLayout) view.findViewById(R.id.tabs);
        cardType.setOnClickListener(this);
        expiry = (TextView) view.findViewById(R.id.card_expiry);
        expiry.setOnClickListener(this);
        holderName = (EditText) view.findViewById(R.id.card_holder);
        cardNumber = (MonitoringEditText) view.findViewById(R.id.card_number);
        cardNumber.addListener(new EditTextListener() {
            @Override
            public void onUpdate() {
                StringBuilder s;
                s = new StringBuilder(cardNumber.getText().toString());
                for (int i = 4; i < s.length(); i += 5) {
                    s.insert(i, "-");
                }
                cardNumber.setText(s.toString());
            }
        });

        //setEditTextMaxLength(cardNumber, 16);
        cvv = (EditText) view.findViewById(R.id.card_cvv);
        mLayRight = (LinearLayout) view.findViewById(R.id.lay_right);
        mLayLeft = (LinearLayout) view.findViewById(R.id.lay_left);
        //    cvv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        cvv.setTransformationMethod(new AsteriskStarTransformationMethod());
        overlay = (RelativeLayout) view.findViewById(R.id.layOver);
        overlay.setOnClickListener(this);
        layInfo = (RelativeLayout) view.findViewById(R.id.lay_info);
        layList = (RelativeLayout) view.findViewById(R.id.lay_list);

        noData = (TextView) view.findViewById(R.id.no_data);
        /*upComing = (TextView) view.findViewById(R.id.upcoming);
        upComing.setOnClickListener(this);*/
        detail = (TextView) view.findViewById(R.id.detail);
        detail.setOnClickListener(this);
        history = (TextView) view.findViewById(R.id.history);
        history.setOnClickListener(this);
        list = (ListView) view.findViewById(R.id.list);
        adatper = new HistoryAdapter(getActivity(), historyData, "credit");
        halfview = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        halfview.setMargins(0, 0, 10, 0);

        fullview = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT,
                2.0f
        );
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adatper);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (type == GET_HISTORY || type == GET_PENALTY) {
                    String backStateName = this.getClass().getName();
                    Bundle bundle = new Bundle();
                    HashMap<String, PaymentHistory> data = new HashMap<String, PaymentHistory>();
                    data.put("data", historyData.get(i));
                    bundle.putString("delivery_id", historyData.get(i).getDeliveryId());
                    bundle.putSerializable("incoming_type", "card");
                    bundle.putSerializable("data", data);
                    bundle.putSerializable("incoming_delivery_type", "");
                    Fragment fragment = new WalletTransactionDetails();
                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction().add(R.id.container, fragment, backStateName)
                            .addToBackStack(null)
                            .commitAllowingStateLoss();
                }
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                if (type == GET_HISTORY) {
                    firstVisibleItem = firstVisibleItemm;
                    visibleItemCount = visibleItemCountt;
                    totalItemCount = totalItemCountt;
                }


            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (type == GET_HISTORY || type == GET_PENALTY) {
                    final int lastItem = firstVisibleItem + visibleItemCount;
                    if (firstVisibleItem > 0 && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                        if (!loadingMore) {
                            if (totalCount >= page) {
                                loadingMore = true;
                                if (type == GET_HISTORY)
                                    getList(1);
                                else
                                    getList(2);
                            }
                        }
                    }

                    //  new AsyncTask().execute();

                    //get next 10-20 items(your choice)items

                }
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

        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (abnCount <= cardNumber.getText().toString().length()
                        && (cardNumber.getText().toString().length() == 4
                        || cardNumber.getText().toString().length() == 9
                        || cardNumber.getText().toString().length() == 14)) {
                    cardNumber.setText(cardNumber.getText().toString() + "-");
                    int pos = cardNumber.getText().length();
                    cardNumber.setSelection(pos);
                } else if (abnCount >= cardNumber.getText().toString().length()
                        && (cardNumber.getText().toString().length() == 4
                        || cardNumber.getText().toString().length() == 9
                        || cardNumber.getText().toString().length() == 14)) {
                    cardNumber.setText(cardNumber.getText().toString().substring(0, cardNumber.getText().toString().length() - 1));
                    int pos = cardNumber.getText().length();
                    cardNumber.setSelection(pos);
                }
                abnCount = cardNumber.getText().toString().length();
            }
        });
    }

    public void onTabTapped(int position) {
        updateUI(position);
    }

    private void appendData(Card card) {
        cardType.setText(card.getCardType());
        id = card.getId();
        expiry.setText(card.getExpiry());
        holderName.setText(card.getNameOnCard());
        // setEditTextMaxLength(cardNumber, 20);
        cardNumber.setText(card.getCardNumber());
        cvv.setText(card.getCvv());
        mLayLeft.setLayoutParams(fullview);
        submit.setText("DELETE");
        HomeActivity.edit.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        //  mLayRight.setVisibility(View.GONE);

    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Alert!");
        builder.setMessage(getResources().getString(R.string.deletecrcarddetail));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteCreditCard();
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

    private void clearData() {
        cardType.setText("");
        holderName.setText("");
        cardNumber.setText("");
        expiry.setText("");
        cvv.setText("");
        submit.setText("SAVE DETAILS");
        overlay.setVisibility(View.GONE);
        // setEditTextMaxLength(cardNumber, 16);
        HomeActivity.edit.setVisibility(View.GONE);
        session.saveCreditCard("null");
        mLayRight.setVisibility(View.VISIBLE);
    }

    private void handleResponse(String result) {
        if (type == DELETE_CARD) {
            if (Integer.parseInt(result) == APIStatus.SUCCESS) {
                clearData();
                messageDialogMap(getActivity(), "Success!", getResources().getString(R.string.cr_wallet_delete));
                // showMessage("Success!", getResources().getString(R.string.cr_wallet_delete));
            } else
                showMessage("Error!", getResources().getString(R.string.fail));
        } else {
            try {
                JSONObject outJson = new JSONObject(result);
                if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                    if (type == ADD_CARD) {
                        try {
                            JSONObject cardObj = outJson.getJSONObject(Config.DATA);
                            Card card = new Card();
                            card.setId(cardObj.getString(Keys.KEY_ID));
                            card.setNameOnCard(cardObj.getString(Keys.NAME_ON_CARD));
                            card.setCardNumber(cardObj.getString(Keys.CARD_NUMBER));
                            card.setCardType(cardObj.getString(Keys.CARD_TYPE));
                            card.setExpiry(cardObj.getString(Keys.EXPIRY));
                            card.setCvv("");
                            card.setCardToken(cardObj.getString(Keys.CARD_TOKEN));
                            appendData(card);
                            session.saveCreditCard(outJson.optJSONObject(Config.DATA).toString());
                            HomeActivity.edit.setVisibility(View.VISIBLE);
                            //    mLayRight.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.toString();
                        }
                        //showMessage("Success!", getResources().getString(R.string.wallet_success));
                        messageDialog(getActivity(), "Success!", getResources().getString(R.string.cr_wallet_success));

                    } else if (type == UPDATE_CARD) {
                        //  initTopBar();
                        try {
                            JSONObject cardObj = outJson.getJSONObject(Config.DATA);
                            Card card = new Card();
                            card.setId(cardObj.getString(Keys.KEY_ID));
                            card.setNameOnCard(cardObj.getString(Keys.NAME_ON_CARD));
                            card.setCardNumber(cardObj.getString(Keys.CARD_NUMBER));
                            card.setCardType(cardObj.getString(Keys.CARD_TYPE));
                            card.setExpiry(cardObj.getString(Keys.EXPIRY));
                            card.setCvv("");
                            card.setCardToken(cardObj.getString(Keys.CARD_TOKEN));
                            appendData(card);
                            session.saveCreditCard(outJson.optJSONObject(Config.DATA).toString());
                            HomeActivity.edit.setVisibility(View.VISIBLE);
                            // mLayRight.setVisibility(View.GONE);
                            SubmitStepOne.mCreditCardDetail = true;
                        } catch (Exception e) {
                            e.toString();
                        }
                        messageDialog(getActivity(), "Success!", getResources().getString(R.string.cr_wallet_update));

                        // showMessage("Success!", getResources().getString(R.string.cr_wallet_update));
                    } else if (type == GET_CARD_TYPES) {
                        if (cardData.size() > 0)
                            cardData.clear();
                        JSONObject innerObject = outJson.getJSONObject(Config.DATA);
                        JSONArray arr = innerObject.getJSONArray("items");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject cardTypeObject = arr.getJSONObject(i);
                            HashMap<String, String> cardTypeMap = new HashMap<String, String>();
                            cardTypeMap.put(Keys.KEY_ID, cardTypeObject.getString(Keys.KEY_ID));
                            cardTypeMap.put(Keys.KEY_NAME, cardTypeObject.getString(Keys.KEY_NAME));
                            cardTypeMap.put(Keys.KEY_ICON, cardTypeObject.getString(Keys.KEY_ICON));
                            cardData.add(cardTypeMap);
                        }
                    } else if (type == GET_CARD) {
                        JSONObject innerObject = outJson.getJSONObject(Config.DATA);
                        JSONObject cardObj = innerObject.getJSONObject(Config.CREDITCARD);
                        Card card = new Card();
                        card.setId(cardObj.getString(Keys.KEY_ID));
                        card.setNameOnCard(cardObj.getString(Keys.NAME_ON_CARD));
                        card.setCardNumber(cardObj.getString(Keys.CARD_NUMBER));
                        card.setCardType(cardObj.getString(Keys.CARD_TYPE));
                        card.setExpiry(cardObj.getString(Keys.EXPIRY));
                        card.setCvv("000");
                        card.setCardToken(cardObj.getString(Keys.CARD_TOKEN));
                        appendData(card);
                        try {
                            session.saveCreditCard(outJson.optJSONObject(Config.DATA).toString());
                        } catch (Exception e) {
                            e.toString();
                        }
                    } else if (type == GET_PENALTY) {
                        historyData.clear();
                        adatper = new HistoryAdapter(getActivity(), historyData, "penality");

                        try {
                            JSONObject innerObject = outJson.getJSONObject(Config.DATA);
                            if (innerObject.has(Config.PAYMENT)) {
                                JSONArray payment = innerObject.getJSONArray(Config.PAYMENT);
                                for (int i = 0; i < payment.length(); i++) {
                                    JSONObject cardObj = payment.getJSONObject(i);
                                    PaymentHistory card = new PaymentHistory();
                                    card.setId(cardObj.getString(Keys.KEY_ID));
                                    card.setDeliveryTitle(cardObj.getString(Keys.DELIVERY_TITLE));
                                    card.setAmount(cardObj.getString(Keys.AMOUNT));
                                    try {
                                        card.setDeliveryId(cardObj.getString(Keys.KEY_JOB_ID));
                                    }catch (Exception ex){
                                        card.setDeliveryId("");
                                    }
                                    card.setDate(cardObj.getString(Keys.PAID_ON));
                                    card.setDelStatus(cardObj.optString(Keys.DELIVERY_STATUS));
                                    card.setUserType(cardObj.optString(Keys.USER_TYPE)); // aded now
                                    card.setPayable(0);
                                    historyData.add(card);
                                }
                                noData.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                                list.setAdapter(adatper);
                                adatper.notifyDataSetChanged();
                            } else {
                                noData.setVisibility(View.VISIBLE);
                                list.setVisibility(View.GONE);
                            }
                        } catch (Exception ex) {
                            noData.setVisibility(View.VISIBLE);
                            list.setVisibility(View.GONE);
                        }
                        //appendData(card);
                    } else if (type == GET_HISTORY) {
                        if (page == 1) {
                            historyData.clear();
                            adatper = new HistoryAdapter(getActivity(), historyData, "credit");
                        }
                        ++page;
                        loadingMore = false;
                        try {
                            JSONObject innerObject = outJson.getJSONObject(Config.DATA);
                            if (innerObject.has(Config.PAYMENT)) {
                                JSONArray payment = innerObject.getJSONArray(Config.PAYMENT);
                                for (int i = 0; i < payment.length(); i++) {
                                    JSONObject cardObj = payment.getJSONObject(i);
                                    PaymentHistory card = new PaymentHistory();
                                    card.setId(cardObj.getString(Keys.KEY_ID));
                                    card.setUserId(cardObj.getString(Keys.KEY_USER_ID));
                                    card.setDeliveryTitle(cardObj.getString(Keys.DELIVERY_TITLE));
                                    card.setAmount(cardObj.getString(Keys.AMOUNT));
                                    card.setCardNumber(cardObj.getString(Keys.CARD_NUMBER));
                                    card.setCardType(cardObj.getString(Keys.CARD_TYPE));
                                    try {
                                        card.setDeliveryId(cardObj.getString(Keys.KEY_JOB_ID));
                                    }catch (Exception ex){
                                        card.setDeliveryId("");
                                    }
                                    card.setPayToDriverAmount(cardObj.getString(Keys.PAY_TO_DRIVER_AMOUNT));
                                    card.setPayToDriverStatus(cardObj.getString(Keys.PAY_TO_DRIVER_STATUS));
                                    card.setPayToDriverAt(cardObj.getString(Keys.PAY_TO_DRIVER_AT));
                                    card.setStatus(cardObj.getString(Keys.STATUS));
                                    card.setTransactionId(cardObj.getString(Keys.TRANSACTION_ID));
                                    card.setDate(cardObj.getString(Keys.CREATED_AT));
                                    card.setDelStatus(cardObj.optString(Keys.DELIVERY_STATUS));
                                    card.setPayable(1);
                                    JSONArray childArray = cardObj.optJSONArray("payment_detail");
                                    if (childArray != null && childArray.length() > 0) {
                                        JSONObject childJson = childArray.optJSONObject(0);
                                        if (childJson.has(Keys.AMOUNT)) {
                                            card.setChargedAmount(childJson.getString(Keys.AMOUNT));
                                            Log.v("", card.getChargedAmount());
                                        }
                                        if (childJson.has(Keys.PAYMENT_METHOD)) {
                                            JSONObject innerJson = childJson.optJSONObject(Keys.PAYMENT_METHOD);
                                            card.setPaymentMethodName(innerJson.optString(Keys.PAYMENT_METHOD_NAME));
                                        }

                                    }
                                    historyData.add(card);
                                }
                                noData.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                                list.setAdapter(adatper);
                                adatper.notifyDataSetChanged();
                            } else {
                                noData.setVisibility(View.VISIBLE);
                                list.setVisibility(View.GONE);
                            }
                            try {
                                if (innerObject.has("_meta")) {
                                    JSONObject metaCount = innerObject.getJSONObject("_meta");
                                    totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                                    Log.v("totalcount", String.valueOf(totalCount));

                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                        } catch (Exception ex) {
                            noData.setVisibility(View.VISIBLE);
                            list.setVisibility(View.GONE);
                        }
                    }
                } else if (outJson.getInt(Config.STATUS) == APIStatus.UNPROCESSABLE) {
                    if (outJson.getString(Config.MESSAGE).equals(""))
                        showMessage("Error", outJson.getJSONArray(Config.DATA).getJSONObject(0).getString(Config.MESSAGE));
                    else
                        showMessage("Error", outJson.getString(Config.MESSAGE));
                } else {
                    showMessage("Error", getResources().getString(R.string.no_response));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ArrayList<Payment> upcomingData = new ArrayList<Payment>();
    ArrayList<PaymentHistory> historyData = new ArrayList<PaymentHistory>();

    private void doGetCardTypes() {
        String url = Config.SERVER_URL + Config.CREDIT_CARD;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    public boolean nameValidate(String name) {
        if (name.contains(" ")) {
            try {
                String[] parts = name.split(" ");
                String part1 = parts[0]; // 004
                String part2 = parts[1];
                if (part2.length() >= 1) {
                    return true;
                } else
                    return false;
            } catch (Exception e) {
                e.toString();
            }
        }
        return false;
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

    public void messageDialog(Context ctx, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (menutype == 1) {
                    //Log.v("", "");
                } else {
                    getFragmentManager().popBackStack();
                }
                //  getFragmentManager().popBackStack();

            }
        });
        Dialog d = builder.create();
        d.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if (submit.getText().toString().equals("DELETE")) {
                    type = DELETE_CARD;
                    showDeleteDialog();
                } else if (isValidate()) {
                    if (submit.getText().toString().equals("UPDATE")) {
                        if (!nameValidate(holderName.getText().toString())) {
                            AlertManager.messageDialog(getActivity(), "Alert!", getString(R.string.accountholdername));
                            return;
                        }
                        type = UPDATE_CARD;
                        updateCreditCard();
                    } else {
                        if (!nameValidate(holderName.getText().toString())) {
                            AlertManager.messageDialog(getActivity(), "Alert!", getString(R.string.accountholdername));
                            return;
                        }
                        type = ADD_CARD;
                        saveCreditCard();
                    }
                }
                break;
            case R.id.card_type:
                showCardTypeDialog();
                break;
            case R.id.card_expiry:
                showDatePicker();
                break;
            case R.id.layOver:
                break;
           /* case R.id.upcoming:
                updateUI(1);
                break;*/
            case R.id.history:
                updateUI(1);
                break;
            case R.id.detail:
                updateUI(2);
                break;
        }
    }

    private void showMessage(String title, String message) {
        AlertManager.messageDialog(getActivity(), title, message);
    }

    String TITLE = "Alert!";

    private boolean isValidate() {
        if (holderName.getText().toString().trim().isEmpty()
                || cardNumber.getText().toString().trim().isEmpty()
                || cardType.getText().toString().trim().isEmpty()
                || expiry.getText().toString().trim().isEmpty()) {
            showMessage(TITLE, getResources().getString(R.string.completeallfield));
            return false;
        } else {
            if (cardNumber.getText().toString().length() < 19) {
                showMessage(TITLE, getResources().getString(R.string.invalidcardno));
                return false;
            } else if (cvv.getText().toString().length() < 3) {
                showMessage(TITLE, getResources().getString(R.string.invalidcvv));
                return false;
            } else if (cvv.getText().toString().length() > 3) {
                showMessage(TITLE, getResources().getString(R.string.invalidcvv));
                return false;
            }
        }/*if (!cardType.getText().toString().equalsIgnoreCase("American Express")) else if (cardType.getText().toString().equalsIgnoreCase("American Express")) {
            if (cardNumber.getText().toString().length() < 16) {
                showMessage(TITLE, "Invalid card number. Please enter valid 16 digit card number.");
                return false;
            } else if (cardNumber.getText().toString().length() > 16) {
                showMessage(TITLE, "Invalid card number. Please enter valid 16 digit card number.");
                return false;
            } else if (cvv.getText().toString().length() < 3) {
                showMessage(TITLE, "Invalid CVV. Please enter valid 4 digit CVV number.");
                return false;
            }
        }*/
        /*else if(isCardExpiryDateValid(
                Integer.parseInt(v3),
				Integer.parseInt(expiry.getText().toString())))
		{
			alertManager.showAlert("Alert!","Please select valid card expiry date.");
			return false;
		}*/

        return true;
    }

    private void saveCreditCard() {
        String cardtype = "";
        if (cardType.getText().toString().contentEquals("MasterCard"))
            cardtype = "master card";
        else
            cardtype = "visa";
        String url = Config.SERVER_URL + Config.CREDIT_CARD;
        HashMap<String, String> params = new HashMap<>();
        params.put(Keys.CARD_NUMBER, cardNumber.getText().toString().trim().replaceAll("-", ""));
        params.put(Keys.CARD_TYPE, cardtype);
        params.put(Keys.EXPIRY, expiry.getText().toString());
        params.put(Keys.CVV, cvv.getText().toString());
        params.put(Keys.NAME_ON_CARD, holderName.getText().toString());
        Log.d(Config.TAG, params.toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.POST, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void getSavedCreditCard() {
        this.type = GET_CARD;
        String url = Config.SERVER_URL + Config.CREDIT_CARD + "/0";
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage(TITLE, getResources().getString(R.string.no_internet));
    }

    private void updateCreditCard() {
        String cardtype = "";
        if (cardType.getText().toString().contentEquals("MasterCard"))
            cardtype = "master card";
        else
            cardtype = "visa";
        String url = Config.SERVER_URL + Config.CREDIT_CARD + "/" + id;
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(Keys.CARD_NUMBER, cardNumber.getText().toString().trim().replaceAll("-", ""));
        params.put(Keys.CARD_TYPE, cardtype);
        params.put(Keys.EXPIRY, expiry.getText().toString());
        params.put(Keys.CVV, cvv.getText().toString());
        params.put(Keys.NAME_ON_CARD, holderName.getText().toString());
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.PUT, this, params);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage("Alert!", getResources().getString(R.string.no_internet));
    }

    ArrayList<HashMap<String, String>> cardData = new ArrayList<HashMap<String, String>>();

    public String getCardTypeID(String name) {
        String id = null;

        for (int i = 0; i < cardData.size(); i++) {
            if (name.equalsIgnoreCase(cardData.get(i).get(Keys.KEY_NAME)))
                id = cardData.get(i).get(Keys.KEY_ID);
        }
        return id;
    }

    public void updateUI(String cardType) {
        cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
        cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
       /* if (cardType.equalsIgnoreCase("American Express")) {
          *//*  cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});
            cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});*//*
            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            //cardNumber.setText("");
            //cvv.setText("");
        } else {
            cardNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(19)});
            cvv.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            //cardNumber.setText("");
            //cvv.setText("");
        }*/
    }

    public void showCardTypeDialog() {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog.isShowing())
                    mDialog.dismiss();
            }
        });
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.dialog_textview, R.id.textItem, getCardTypeList());
        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                cardType.setText(parent.getItemAtPosition(position).toString());
                updateUI(cardType.getText().toString().trim());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  HomeActivity.edit.setVisibility(View.GONE);
    }

    public String[] getCardTypeList() {
        String[] listContent = new String[cardData.size()];
        for (int i = 0; i < cardData.size(); i++) {
            listContent[i] = cardData.get(i).get(Keys.KEY_NAME);
        }
        return getResources().getStringArray(R.array.cardTypes);
    }

    private void deleteCreditCard() {
        String url = Config.SERVER_URL + Config.CREDIT_CARD + "/" + id;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.DELETE, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            showMessage("Alert!", getResources().getString(R.string.no_internet));
    }

    private void showDatePicker() {
        MonthYearPickerDialog pd = new MonthYearPickerDialog();
        pd.setListener(onDateListen);
        pd.show(getFragmentManager(), "MonthYearPickerDialog");
    }

    DatePickerDialog.OnDateSetListener onDateListen = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String m;
            if (monthOfYear < 10)
                m = "0" + monthOfYear;
            else
                m = String.valueOf(monthOfYear);
            expiry.setText(m + "/" + String.valueOf(year));
        }
    };

    private void updateUI(int type) {
        /*if(type == 1){
            *//*upComing.setBackgroundResource(R.drawable.border_back);
            upComing.setTextColor(Color.WHITE);*//*
            history.setBackgroundResource(R.drawable.border_black);
            history.setTextColor(Color.BLACK);
            detail.setBackgroundResource(R.drawable.border_black);
            detail.setTextColor(Color.BLACK);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
        } else */
        if (type == 1) {
            /*upComing.setBackgroundResource(R.drawable.border_black);
            upComing.setTextColor(Color.BLACK);*/
            history.setBackgroundResource(R.drawable.border_back);
            history.setTextColor(Color.WHITE);
            detail.setBackgroundResource(R.drawable.border_black);
            detail.setTextColor(Color.BLACK);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
        } else if (type == 2) {
            /*upComing.setBackgroundResource(R.drawable.border_black);
            upComing.setTextColor(Color.BLACK);*/
            /*history.setBackgroundResource(R.drawable.border_black);
            history.setTextColor(Color.BLACK);
            detail.setBackgroundResource(R.drawable.border_back);
            detail.setTextColor(Color.WHITE);*/
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
        } else if (type == 3) {
            /*upComing.setBackgroundResource(R.drawable.border_black);
            upComing.setTextColor(Color.BLACK);*/
            history.setBackgroundResource(R.drawable.border_black);
            history.setTextColor(Color.BLACK);
            detail.setBackgroundResource(R.drawable.border_back);
            detail.setTextColor(Color.WHITE);
            layList.setVisibility(View.GONE);
            layInfo.setVisibility(View.VISIBLE);
        }
        HomeActivity.edit.setVisibility(View.GONE);
        mLayRight.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.GONE);
        if (type == 3) {
            this.type = GET_CARD;
            getSavedCreditCard();
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
            /*case 1:
                this.type = GET_UPCOMING;
                url = url + Config.CARD_UPCOMING ;
                break;*/
            case 1:
                this.type = GET_HISTORY;
                url = url + Config.CARD_HISTORY + "?page=" + page;
                break;
            case 2:
                this.type = GET_PENALTY;
                url = url + Config.CARD_PENALTY + "?page=" + page;
                break;
        }
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    @Override
    public boolean onBackPressed() {
        // getFragmentManager().popBackStack();
        return false;
    }
}
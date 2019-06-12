package com.bookmyride.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.bookmyride.R;
import com.bookmyride.adapters.PlaceAutoCompleteAdapter;
import com.bookmyride.adapters.TransactionAdapter;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.api.OnLoadMoreListener;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.models.CardType;
import com.bookmyride.models.Payment;
import com.bookmyride.util.AsteriskPasswordTransformationMethod;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChargeMe extends Fragment implements AsyncTaskCompleteListener,
        View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    SessionHandler session;
    EditText nameOnCard, cardNumber, cvv;
    TextView expiry, save, cardType, gateway;
    EditText /*address_1,*/ address_2, city, state, country, postalCode;
    LinearLayout layAddress;
    RelativeLayout /*overlay,*/ layList;
    LinearLayout layInfo;
    LinearLayout history, detail;
    ListView list;
    TextView noData;
    TransactionAdapter adapter;
    TextView historyView, detailView;
    ArrayList<Payment> allData = new ArrayList<Payment>();
    AutoCompleteTextView address;
    private PlaceAutoCompleteAdapter mAdapter;
    private static final LatLngBounds BOUNDS = new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));
    protected GoogleApiClient mGoogleApiClient;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.charge_me, null);
        init(view);
        updateUI(1);
        layAddress.setVisibility(View.GONE);
        if (!session.getCardAddress().equals("") && !session.getCardAddress().equals("null")) {
            appendAddress();
        }
        return view;
    }

    private void appendAddress() {
        try {
            JSONObject addressObj = new JSONObject(session.getCardAddress());
            //address_1.setText(addressObj.getString("address"));
            address_2.setText("");
            city.setText(addressObj.getString("city"));
            state.setText("");
            country.setText(addressObj.getString("country"));
            postalCode.setText(addressObj.getString("postalcode"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.history:
                updateUI(1);
                break;
            case R.id.detail:
                updateUI(2);
                break;
        }
    }

    int count = 0;

    private void init(View view) {
        session = new SessionHandler(getActivity());
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        layAddress = (LinearLayout) view.findViewById(R.id.lay_address);
        nameOnCard = (EditText) view.findViewById(R.id.name_on_card);
        nameOnCard.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        cardNumber = (EditText) view.findViewById(R.id.card_number);
        cardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (count <= cardNumber.getText().toString().length()
                        && (cardNumber.getText().toString().length() == 4
                        || cardNumber.getText().toString().length() == 9
                        || cardNumber.getText().toString().length() == 14)) {
                    cardNumber.setText(cardNumber.getText().toString() + " ");
                    int pos = cardNumber.getText().length();
                    cardNumber.setSelection(pos);
                } else if (count >= cardNumber.getText().toString().length()
                        && (cardNumber.getText().toString().length() == 4
                        || cardNumber.getText().toString().length() == 9
                        || cardNumber.getText().toString().length() == 14)) {
                    cardNumber.setText(cardNumber.getText().toString().substring(0, cardNumber.getText().toString().length() - 1));
                    int pos = cardNumber.getText().length();
                    cardNumber.setSelection(pos);
                }
                count = cardNumber.getText().toString().length();
            }
        });
        cvv = (EditText) view.findViewById(R.id.card_cvv);
        cvv.addTextChangedListener(new AsteriskPasswordTransformationMethod());
        expiry = (TextView) view.findViewById(R.id.card_expiry);
        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
        //address_1 = (EditText) view.findViewById(R.id.address_1);
        address = (AutoCompleteTextView) view.findViewById(R.id.address);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        address.setTypeface(font);
        mAdapter = new PlaceAutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS, null);
        address.setAdapter(mAdapter);
        address_2 = (EditText) view.findViewById(R.id.address_2);
        city = (EditText) view.findViewById(R.id.city);
        state = (EditText) view.findViewById(R.id.state);
        country = (EditText) view.findViewById(R.id.country);
        postalCode = (EditText) view.findViewById(R.id.postal_code);
        cardType = (TextView) view.findViewById(R.id.card_type);
        cardType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRideDialog(1);
            }
        });
        gateway = (TextView) view.findViewById(R.id.gateway);
        gateway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRideDialog(2);
            }
        });
        save = (TextView) view.findViewById(R.id.done);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (save.getText().toString().equals("Edit")) {
                    //overlay.setVisibility(View.GONE);
                    layAddress.setVisibility(View.VISIBLE);
                    updateInputs();
                    cardNumber.setText("");
                    save.setText("Update");
                } else {
                    if (isValidate())
                        saveCreditCard();
                }
            }
        });

        //overlay = (RelativeLayout) view.findViewById(R.id.layOver);
        //overlay.setOnClickListener(this);
        layInfo = (LinearLayout) view.findViewById(R.id.lay_info);
        layList = (RelativeLayout) view.findViewById(R.id.lay_list);

        noData = (TextView) view.findViewById(R.id.no_data);
        detail = (LinearLayout) view.findViewById(R.id.detail);
        detail.setOnClickListener(this);
        history = (LinearLayout) view.findViewById(R.id.history);
        history.setOnClickListener(this);
        historyView = (TextView) view.findViewById(R.id.h_view);
        detailView = (TextView) view.findViewById(R.id.d_view);
        list = (ListView) view.findViewById(R.id.list);
        adapter = new TransactionAdapter(getActivity(), allData, 1, loadMore);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        if (allData.size() == 0)
            noData.setVisibility(View.VISIBLE);
        else noData.setVisibility(View.GONE);

        nameOnCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    nameOnCard.requestFocus();
                    nameOnCard.setFocusable(true);
                    nameOnCard.setCursorVisible(true);
                    nameOnCard.setFocusableInTouchMode(true);
                    nameOnCard.setSelection(nameOnCard.getText().length());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(nameOnCard, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        cardNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    cardNumber.requestFocus();
                    cardNumber.setFocusable(true);
                    cardNumber.setCursorVisible(true);
                    cardNumber.setFocusableInTouchMode(true);
                    cardNumber.setSelection(cardNumber.getText().length());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(cardNumber, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });

        cvv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    cvv.requestFocus();
                    cvv.setFocusable(true);
                    cvv.setCursorVisible(true);
                    cvv.setFocusableInTouchMode(true);
                    cvv.setSelection(cvv.getText().length());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(cvv, InputMethodManager.SHOW_FORCED);
                }
                return true; // return is important...
            }
        });
        address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);

                /*
                Issue a request to the Places Geo Data API to retrieve a Place object with additional
                details about the place.
                */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if (!places.getStatus().isSuccess()) {
                            // Request did not complete successfully
                            Log.e(Config.TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                            places.release();
                            return;
                        }
                        // Get the Place object from the buffer.
                        final Place place = places.get(0);
                        double latitude = place.getLatLng().latitude;
                        double longitude = place.getLatLng().longitude;
                        getAddress(latitude, longitude);
                    }
                });
            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int startNum, int before, int count) {
                /*if (start != null) {
                    start = null;
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (Internet.hasInternet(getActivity())) {
            doGetCardTypes();
        } else {
            Alert("Alert!", getResources().getString(R.string.no_internet));
        }
    }

    public static boolean hasMore = false;
    int pageCount, currentPage = 0;
    private OnLoadMoreListener loadMore = new OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            if (hasMore)
                getList();
        }
    };

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Key.STATUS) == APIStatus.SUCCESS) {
                if (type == 0) {
                    if (outJson.get(Key.DATA) instanceof JSONArray) {
                        layAddress.setVisibility(View.VISIBLE);
                    } else {
                        JSONArray dataArray = outJson.getJSONObject(Key.DATA).getJSONArray(Key.ITEMS);
                        JSONObject dataObj = dataArray.getJSONObject(0);
                        cardType.setText(dataObj.getString(Key.TYPE));
                        nameOnCard.setText(dataObj.getString("first_name") + " " + dataObj.getString("last_name"));
                        cardNumber.setText(dataObj.getString(Key.NUMBER));
                        expiry.setText(dataObj.getString("expire_month") + "/" + dataObj.getString("expire_year"));
                        gateway.setText(dataObj.getString(Key.GATEWAY));
                        layAddress.setVisibility(View.GONE);
                        if (dataObj.has(Key.ADDRESS_LINE_1) && !dataObj.getString(Key.ADDRESS_LINE_1).equals("null"))
                            address.setText(dataObj.getString(Key.ADDRESS_LINE_1));
                        if (dataObj.has(Key.ADDRESS_LINE_2) && !dataObj.getString(Key.ADDRESS_LINE_2).equals("null"))
                            address_2.setText(dataObj.getString(Key.ADDRESS_LINE_2));
                        if (dataObj.has(Key.ADDRESS_CITY) && !dataObj.getString(Key.ADDRESS_CITY).equals("null"))
                            city.setText(dataObj.getString(Key.ADDRESS_CITY));
                        if (dataObj.has(Key.ADDRESS_STATE) && !dataObj.getString(Key.ADDRESS_STATE).equals("null"))
                            state.setText(dataObj.getString(Key.ADDRESS_STATE));
                        if (dataObj.has(Key.ADDRESS_COUNTRY) && !dataObj.getString(Key.ADDRESS_COUNTRY).equals("null"))
                            country.setText(dataObj.getString(Key.ADDRESS_COUNTRY));
                        if (dataObj.has(Key.ADDRESS_POSTAL_CODE) && !dataObj.getString(Key.ADDRESS_POSTAL_CODE).equals("null"))
                            postalCode.setText(dataObj.getString(Key.ADDRESS_POSTAL_CODE));
                        save.setText("Edit");
                        disableInputs();
                        //overlay.setVisibility(View.VISIBLE);
                    }
                } else if (type == 1) {
                    save.setText("Edit");
                    cvv.setText("");
                    //overlay.setVisibility(View.VISIBLE);
                    cardNumber.clearFocus();
                    nameOnCard.clearFocus();
                    cvv.clearFocus();
                    layAddress.setVisibility(View.GONE);
                    disableInputs();
                    String number = cardNumber.getText().toString();
                    String output = "XXXX-XXXX-XXXX-" + number.substring(number.length() - 4);
                    cardNumber.setText(output);
                    Alert("Success!", outJson.getString(Key.MESSAGE));
                } else if (type == 2) {
                    JSONArray items = outJson.getJSONObject(Key.DATA).getJSONArray("transaction");
                    if (items.length() > 0 && !hasMore) {
                        allData.clear();
                    }
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject obj = items.getJSONObject(i);
                        Payment payment = new Payment();
                        payment.setAmount("$" + obj.getString(Key.AMOUNT));
                        payment.setBookingID(obj.getString("booking_id"));
                        //payment.setCurrency(obj.getString(Key));
                        payment.setDate(obj.getString(Key.CREATED_AT));
                        if (obj.has("payment_amount_detail")) {
                            String paymentDetail = obj.getString("payment_amount_detail");
                            if (!paymentDetail.equals("") && !paymentDetail.equals("null")) {
                                JSONObject payObj = new JSONObject(paymentDetail);
                                payment.setGateway(payObj.getString("payment_gateway"));
                            } else payment.setGateway("");
                        } else payment.setGateway("");
                        //payment.setStatus(obj.getString("status"));
                        if (!obj.getString(Key.AMOUNT).equals("null")
                                && !obj.getString(Key.AMOUNT).equals("") &&
                                !obj.getString("payment_amount_detail").equals("null") &&
                                !obj.getString("payment_amount_detail").equals("")) {
                            allData.add(payment);
                        }
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
                } else {
                    if (cardData.size() > 0)
                        cardData.clear();
                    JSONArray arr = outJson.getJSONArray(Key.DATA);
                    for (int i = 0; i < arr.length(); i++) {
                        CardType cardType = new CardType();
                        //cardType.setId(cardTypeObject.getString(Key.ID));
                        cardType.setName("" + arr.get(i));
                        //cardType.setIcon(cardTypeObject.getString(Key.ICON));
                        cardData.add(cardType);
                    }
                    if (Internet.hasInternet(getActivity())) {
                        getSavedCard();
                    } else {
                        Alert("Alert!", getResources().getString(R.string.no_internet));
                    }
                }
            } else if (outJson.getInt(Key.STATUS) == APIStatus.UNPROCESSABLE) {
                Alert("Alert!", outJson.getJSONArray(Key.DATA).getJSONObject(0).getString(Key.MESSAGE));
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    ArrayList<CardType> cardData = new ArrayList<CardType>();
    int type = 0;

    private void getSavedCard() {
        type = 0;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.CREDIT_CARD, session.getToken());
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
        if (cardType.getText().toString().trim().isEmpty()
                || nameOnCard.getText().toString().trim().isEmpty()
                || cardNumber.getText().toString().trim().isEmpty()
                || expiry.getText().toString().trim().isEmpty()
                || cvv.getText().toString().trim().isEmpty()
                /*|| gateway.getText().toString().trim().isEmpty()*/) {
            Alert("Oops !!!", "Please complete all fields.");
            return false;
        } else if (!nameOnCard.getText().toString().contains(" ")) {
            Alert("Oops !!!", "Please enter full name of Card Holder.");
            return false;
        } else if (cardNumber.getText().toString().length() < 16) {
            Alert("Oops !!!", "Invalid card number. Please enter valid 16 digit card number.");
            return false;
        } else if (cvv.getText().toString().length() < 3) {
            Alert("Oops !!!", "Invalid CVV. Please enter valid 3 digit CVV number.");
            return false;
        } else if (isCardExpiryDateValid(expiry.getText().toString())) {
            Alert("Oops !!!", "Please select valid card expiry date.");
            return false;
        } else if (address.getText().toString().length() <= 0) {
            Alert("Oops !!!", "Please enter address.");
            return false;
        } else if (city.getText().toString().length() <= 0) {
            Alert("Oops !!!", "Please enter city.");
            return false;
        } else if (country.getText().toString().length() <= 0) {
            Alert("Oops !!!", "Please enter country.");
            return false;
        }
        return true;
    }

    private boolean isCardExpiryDateValid(String input) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);
        Date expiry = null;
        try {
            expiry = simpleDateFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        boolean expired = expiry.before(new Date());
        return expired;
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

    private void saveCreditCard() {
        type = 1;
        String exp = expiry.getText().toString();
        String expiryMonth = exp.substring(0, exp.indexOf("/"));
        String expiryYear = exp.substring(exp.indexOf("/") + 1, exp.length());
        String name = nameOnCard.getText().toString();
        String fName;
        String lName;
        if (name.contains(" ")) {
            fName = name.substring(0, name.indexOf(" "));
            lName = name.substring(name.indexOf(" ") + 1, name.length());
        } else {
            fName = name;
            lName = ".";
        }
        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put(Key.TYPE, cardType.getText().toString());
        jsonParams.put(Key.NUMBER, cardNumber.getText().toString().replace(" ", "").trim());
        //Westpac params
        /*jsonParams.put(Key.CARD_EXPIRY_MONTH, expiryMonth);
        jsonParams.put(Key.CARD_EXPIRY_YEAR, expiryYear);
        jsonParams.put(Key.CARD_CVV, cvv.getText().toString());
        jsonParams.put(Key.FIRST_NAME, fName);
        jsonParams.put(Key.LAST_NAME, lName);*/
        //Pin Payment params
        jsonParams.put(Key.NAME, nameOnCard.getText().toString());
        jsonParams.put(Key.EXPIRY_MONTH, expiryMonth);
        jsonParams.put(Key.EXPIRY_YEAR, expiryYear);
        jsonParams.put(Key.CVC, cvv.getText().toString());
        jsonParams.put(Key.GATEWAY, "pinpay"/*"westpac"gateway.getText().toString()*/);
        jsonParams.put(Key.EMAIL, session.getPassengerData().getEmail());
        jsonParams.put(Key.ADDRESS_LINE_1, address.getText().toString());
        jsonParams.put(Key.ADDRESS_LINE_2, address_2.getText().toString());
        jsonParams.put(Key.ADDRESS_CITY, city.getText().toString());
        jsonParams.put(Key.ADDRESS_COUNTRY, country.getText().toString());
        jsonParams.put(Key.ADDRESS_STATE, state.getText().toString());
        jsonParams.put(Key.ADDRESS_POSTAL_CODE, postalCode.getText().toString());
        APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.POST, this, jsonParams);
        apiHandler.execute(Config.CREDIT_CARD, session.getToken());
    }

    private void doGetCardTypes() {
        type = 3;
        if (Internet.hasInternet(getActivity())) {
            APIHandler apiHandler = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            apiHandler.execute(Config.CARD_TYPES, session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    Dialog mDialog;

    public void showRideDialog(final int type) {
        mDialog = new Dialog(getActivity(), R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        if (type == 1)
            title.setText("Select Card Type");
        else
            title.setText("Select Gateway");
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
        if (type == 1)
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.simple_list_item, R.id.textItem, getCardTypeList());
        else
            adapter = new ArrayAdapter<>(getActivity(),
                    R.layout.simple_list_item, R.id.textItem, getGatewayList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1)
                    cardType.setText(parent.getItemAtPosition(position).toString());
                else
                    gateway.setText(parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    public String[] getCardTypeList() {
        String[] listContent = new String[cardData.size()];
        for (int i = 0; i < cardData.size(); i++) {
            listContent[i] = cardData.get(i).getName();
        }
        return listContent;
    }

    public String[] getGatewayList() {
        String[] gateway = getResources().getStringArray(R.array.gateway);
        return gateway;
    }

    private void updateUI(int type) {
        if (type == 1) {
            currentPage = 0;
            hasMore = false;
            historyView.setBackgroundResource(R.color.driver_color);
            detailView.setBackgroundResource(R.color.white);
            layInfo.setVisibility(View.GONE);
            layList.setVisibility(View.VISIBLE);
            save.setVisibility(View.GONE);
        } else if (type == 2) {
            currentPage = 0;
            hasMore = false;
            historyView.setBackgroundResource(R.color.white);
            detailView.setBackgroundResource(R.color.driver_color);
            layList.setVisibility(View.GONE);
            layInfo.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);
            layAddress.setVisibility(View.GONE);
        }
        if (type == 2) {
            cvv.setText("");
            doGetCardTypes();
        } else {
            getList();
        }
    }

    private void getList() {
        type = 2;
        if (Internet.hasInternet(getActivity())) {
            APIHandler mobileAPI = new APIHandler(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(Config.TRANSACTIONS + "?page=" + (++currentPage), session.getToken());
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void updateInputs() {
        cardType.setEnabled(true);
        nameOnCard.setEnabled(true);
        cardNumber.setEnabled(true);
        expiry.setEnabled(true);
        cvv.setEnabled(true);
    }

    private void disableInputs() {
        cardType.setEnabled(false);
        cardType.setCursorVisible(false);
        nameOnCard.setEnabled(false);
        nameOnCard.setCursorVisible(false);
        cardNumber.setEnabled(false);
        cardNumber.setCursorVisible(false);
        expiry.setEnabled(false);
        expiry.setCursorVisible(false);
        cvv.setEnabled(false);
        cvv.setCursorVisible(false);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(Config.TAG, connectionResult.toString());
    }

    private void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                // Here are some results you can geocode
                String ZIP = "";
                String city = "";
                String state = "";
                String postalCode = "";
                String country = "";

                if (addresses.get(0).getPostalCode() != null) {
                    ZIP = addresses.get(0).getPostalCode();
                    Log.d("ZIP", ZIP);
                }

                if (addresses.get(0).getLocality() != null) {
                    city = addresses.get(0).getLocality();
                    this.city.setText(city);
                    Log.d("city", city);
                }

                if (addresses.get(0).getAdminArea() != null) {
                    state = addresses.get(0).getAdminArea();
                    this.state.setText(state);
                    Log.d("state", state);
                }

                if (addresses.get(0).getPostalCode() != null) {
                    postalCode = addresses.get(0).getPostalCode();
                    this.postalCode.setText(postalCode);
                    Log.d("state", postalCode);
                }

                if (addresses.get(0).getCountryName() != null) {
                    country = addresses.get(0).getCountryName();
                    this.country.setText(country);
                    Log.d("country", country);
                    Log.d("country_code", addresses.get(0).getCountryCode());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
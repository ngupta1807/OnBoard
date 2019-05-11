package com.grabid.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Delivery;
import com.grabid.util.PlaceJSONParser;
import com.grabid.views.BoldAutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by vinod on 10/14/2016.
 */
public class Search extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener {
    TextView search, reset, pickDate, dropDate, bookingType, mTransferType;
    String deliveryTypeID, transferTypeId;
    SessionManager session;
    BoldAutoCompleteTextView source, destination;
    int apiType;
    int type; /* type 1 for PickUP & type 2 for DropOff */
    final int PLACES = 0;
    int searchType = 0;
    ParserTask placesParserTask;
    DownloadTask placesDownloadTask;
    String url;
    int totalCount = 1;
    EditText max_bid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setText(getResources().getString(R.string.search_delivery));
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.top_bar_title_color));
        View view = inflater.inflate(R.layout.search, null);
        deliveryTypeID = srcCity = srcState = srcCountry = destCity = transferTypeId = destState = destCountry = "";
        init(view);
//        getDeliveryTypes();
        return view;
    }

    private void init(View view) {
        session = new SessionManager(getActivity());
        search = (TextView) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        reset = (TextView) view.findViewById(R.id.reset);
        reset.setOnClickListener(this);
        pickDate = (TextView) view.findViewById(R.id.pick_date);
        pickDate.setOnClickListener(this);
        dropDate = (TextView) view.findViewById(R.id.drop_date);
        dropDate.setOnClickListener(this);
        bookingType = (TextView) view.findViewById(R.id.booking_type);
        bookingType.setOnClickListener(this);
        max_bid = (EditText) view.findViewById(R.id.max_bid);
        mTransferType = (TextView) view.findViewById(R.id.transfer_type);
        mTransferType.setOnClickListener(this);
        source = (BoldAutoCompleteTextView) view.findViewById(R.id.from);
        destination = (BoldAutoCompleteTextView) view.findViewById(R.id.to);
        source.setThreshold(1);
        source.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchType = 0;
                placesDownloadTask = new DownloadTask(PLACES);
                String url = getAutoCompleteUrl(s.toString());
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        source.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {
                searchType = 0;
                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) lv.getAdapter();
                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                String address = hm.get("description");
                srcCity = address.substring(0, address.indexOf(","));
                if (countCharOfString(',', address) == 2)
                    srcState = address.substring(address.indexOf(",") + 2, address.lastIndexOf(","));
                else srcState = address.substring(0, address.indexOf(","));
                srcCountry = address.substring(address.lastIndexOf(",") + 2, address.length());
                Log.d("data", srcCity + "-" + srcState + "-" + srcCountry);
            }
        });
        destination.setThreshold(1);
        destination.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchType = 1;
                placesDownloadTask = new DownloadTask(PLACES);
                String url = getAutoCompleteUrl(s.toString());
                placesDownloadTask.execute(url);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        destination.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index,
                                    long id) {
                searchType = 1;
                ListView lv = (ListView) arg0;
                SimpleAdapter adapter = (SimpleAdapter) lv.getAdapter();
                HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);
                String address = hm.get("description");
                destCity = address.substring(0, address.indexOf(","));
                if (countCharOfString(',', address) == 2)
                    destState = address.substring(address.indexOf(",") + 2, address.lastIndexOf(","));
                else destState = address.substring(0, address.indexOf(","));
                destCountry = address.substring(address.lastIndexOf(",") + 2, address.length());
                Log.d("data", destCity + "-" + destState + "-" + destCountry);
            }
        });
    }

    public int countCharOfString(char c, String s) {
        final String removedQuoted = s.replaceAll("\".*?\"", "");
        int total = 0;
        for (int i = 0; i < removedQuoted.length(); ++i)
            if (removedQuoted.charAt(i) == c)
                ++total;
        return total;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                searchDeliveries();
                break;
            case R.id.pick_date:
                type = 1;
                showDatePicker();
                break;
            case R.id.drop_date:
                type = 2;
                showDatePicker();
                break;
            case R.id.booking_type:
                showGrabidDialog();
                break;
            case R.id.transfer_type:
                showTransferDialog();
                break;
            case R.id.reset:
                reset();
                break;
        }
    }

    public void reset(){
        source.setText("");
        destination.setText("");
        bookingType.setText("");
        pickDate.setText("");
        dropDate.setText("");
        mTransferType.setText("");
        transferTypeId = "";
        deliveryTypeID = "";
        srcCountry = "";
        srcState = "";
        srcCity = "";
        destCountry = "";
        destState = "";
        destCity = "";
        max_bid.setText("");
    }



    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        handleResponse(result);
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        // Create the DatePickerDialog instance
        DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                datePickerListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        datePicker.setCancelable(false);
        datePicker.setTitle("Select the date");
        datePicker.show();
    }

    // Listener
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            if (type == 1)
                pickDate.setText(formattedDate(selectedYear, selectedMonth + 1, selectedDay));
                //  pickDate.setText(day1 + "-" + month1 + "-" + year1);
            else if (type == 2)
                dropDate.setText(formattedDate(selectedYear, selectedMonth + 1, selectedDay));
            // dropDate.setText(day1 + "-" + month1 + "-" + year1);
        }
    };

    private String formattedDate(int year, int month, int day) {
        String date = String.format("%d-%02d-%02d", year, month, day);
        return date;
    }

    public void showTransferDialog() {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Please Select");
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
        final ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter = null;

        adapter = new ArrayAdapter<>(getActivity(),
                R.layout.dialog_textview, R.id.textItem, getTransferList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                mTransferType.setText(parent.getItemAtPosition(position).toString());
                transferTypeId = String.valueOf(position);




                mDialog.dismiss();
            }
        });
        mDialog.show();
    }



    public void showGrabidDialog() {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText("Please Select");
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
        final ListView dialog_ListView = (ListView) mDialog.findViewById(R.id.list);
        ArrayAdapter<String> adapter = null;

        adapter = new ArrayAdapter<>(getActivity(),
                R.layout.dialog_textview, R.id.textItem, getJobList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                bookingType.setText(parent.getItemAtPosition(position).toString());
                if (position == 0)
                    deliveryTypeID = "1";
                else
                    deliveryTypeID = "2";
                //deliveryTypeID = getDeliveryId(parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }



    public String[] getJobList() {
        return getActivity().getResources().getStringArray(R.array.jobypes);
    }



    public String[] getDeliveryList() {
        String[] listContent = new String[deliveryType.size()];
        for (int i = 0; i < deliveryType.size(); i++) {
            listContent[i] = deliveryType.get(i).get(Keys.KEY_NAME);
        }
        return listContent;
    }

    public String[] getTransferList() {
        return getActivity().getResources().getStringArray(R.array.transfertypes);
    }

    public String getDeliveryId(String countryName) {
        for (int i = 0; i < deliveryType.size(); i++) {
            if (deliveryType.get(i).get(Keys.KEY_NAME).equalsIgnoreCase(countryName))
                return deliveryType.get(i).get(Keys.KEY_ID);
        }
        return "";
    }

    ArrayList<HashMap<String, String>> deliveryType = new ArrayList<>();
    ArrayList<Delivery> deliveryData = new ArrayList<>();

    private void getDeliveryTypes() {
        apiType = 1;
        String url = Config.SERVER_URL + Config.DELIVERY_TYPE;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void searchDeliveries() {
        apiType = 2;
        //   url = Config.SERVER_URL + Config.DELIVERY + Config.SEARCH + "?expand=delivery";
        url = Config.SERVER_URL + Config.JOB + Config.SEARCH + "?expand=delivery";
        if (!srcCountry.equals(""))
            url = url + "&pick_up_country=" + srcCountry;
        if (!srcState.equals(""))
            url = url + "&pick_up_state=" + srcState;
        if (!srcCity.equals(""))
            url = url + "&pick_up_city=" + srcCity;
        if (!destCountry.equals(""))
            url = url + "&drop_off_country=" + destCountry;
        if (!destState.equals(""))
            url = url + "&drop_off_state=" + destState;
        if (!destCity.equals(""))
            url = url + "&drop_off_city=" + destCity;
        if (pickDate.getText().toString().length() > 0)
            url = url + "&pick_up_day=" + pickDate.getText().toString();
        if (dropDate.getText().toString().length() > 0)
            url = url + "&drop_off_day=" + dropDate.getText().toString();
        if (!deliveryTypeID.equals(""))
            url = url + "&item_type=" + deliveryTypeID;
        if (!transferTypeId.equals(""))
            url = url + "&delivery_status_type=" + transferTypeId;
        if (!max_bid.getText().toString().equals(""))
            url = url + "&offer_price=" + max_bid.getText().toString();
        String urlc = url + "&page=" + 1;
        Log.d("EndPoint", urlc);
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(urlc, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void handleResponse(String result) {
        try {
            JSONObject outterJson = new JSONObject(result);
            if (Integer.parseInt(outterJson.getString(Config.STATUS)) == APIStatus.SUCCESS) {
                if (apiType == 1) {
                    deliveryType.clear();
                    JSONArray outterArray = outterJson.getJSONArray(Config.DATA);
                    for (int i = 0; i < outterArray.length(); i++) {
                        JSONObject innerJson = outterArray.getJSONObject(i);
                        HashMap<String, String> map = new HashMap<>();
                        map.put(Keys.KEY_ID, innerJson.get(Keys.KEY_ID).toString());
                        map.put(Keys.KEY_NAME, innerJson.getString(Keys.KEY_NAME));
                        map.put(Keys.KEY_IS_SELECTED, innerJson.getString(Keys.KEY_IS_SELECTED));
                        deliveryType.add(map);
                    }
                } else if (apiType == 2) {
                    deliveryData.clear();
                    try {
                        JSONObject dataObj = outterJson.getJSONObject(Config.DATA);
                        JSONArray deliveryArray = dataObj.getJSONArray(Config.JOB);
                        for (int i = 0; i < deliveryArray.length(); i++) {
                            JSONObject deliveryObj = deliveryArray.getJSONObject(i);
                            Delivery delivery = new Delivery();
                            Log.v("data", deliveryObj.toString());
                            delivery.setPaymentAmount(deliveryObj.getString(Keys.PAYMENT_AMOUNT));
                            delivery.setBookmarked((deliveryObj.get(Config.BOOKMARK).toString().equals("null")) ? false : true);
                            try {
                                delivery.setJob_ID(deliveryObj.getString(Keys.KEY_JOB_ID));
                            }catch (Exception ex){
                                delivery.setJob_ID("");
                            }
                            delivery.setId(deliveryObj.getString(Keys.KEY_ID));
                            //  delivery.setLiftequipement(deliveryObj.getString(Keys.LIFT_EQUIPMENT));
                            //delivery.setAnimalName(deliveryObj.getString(Keys.ITEM_ANIMAL_NAME));
                            //delivery.setDliftequipement(deliveryObj.getString(Keys.DROP_EQUIPMENT));
                            delivery.setAsap((deliveryObj.get(Config.ASAP).toString().equals("1")) ? true : false);
                            delivery.setRoundTrip((deliveryObj.get(Config.ROUNDTRIP).toString().equals("1")) ? true : false);
                            delivery.setItemtype(deliveryObj.getString(Config.ITEMTYPE));
                            //delivery.setBreed(deliveryObj.getString(Keys.ITEM_ANIMAL_BREED));
                            delivery.setTitle(deliveryObj.getString(Keys.ITEM_DELIVERY_TITLE));
                            delivery.setBidID(deliveryObj.getString(Keys.BID_ID));
                            delivery.setBidStatus(deliveryObj.get(Keys.BID_STATUS).toString());
                            delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                            delivery.setDropoffAdress(deliveryObj.getString(Keys.DROPOFF_ADDRESS));
                            delivery.setDriverID(deliveryObj.getString(Keys.DRIVER_ID));
                            //delivery.setItemPhoto(deliveryObj.getString(Keys.ITEM_PHOTO));

                            //Added By VK
                            delivery.setIsAbleToAllocate(deliveryObj.getBoolean(Config.IS_ABLE_TO_ALLOCATE_DRIVER));
                            delivery.setAllocateDriverID(deliveryObj.get(Config.ALLOCATE_DRIVER_ID).toString());
                            delivery.setAllocationStatus(deliveryObj.get(Config.ALLOCATION_STATUS).toString());
                            //VK end

                            delivery.setObjData(deliveryObj.toString());
                            delivery.setCompletedAt(deliveryObj.getString(Keys.COMPLETED_AT));
                            delivery.setPickUpAddress(deliveryObj.getString(Keys.PICKUP_ADDRESS));
                            delivery.setUserID(deliveryObj.getString(Keys.KEY_USER_ID));
                            delivery.setPickUpDate(deliveryObj.getString(Keys.PICKUP_DATE));
                            //delivery.setDropOffDate(deliveryObj.getString(Keys.DROPOFF_DATE));
                            delivery.setPickupCountry(deliveryObj.getString(Keys.PICKUP_COUNTRY));
                            delivery.setPickupState(deliveryObj.getString(Keys.PICKUP_STATE));
                            delivery.setPickupCity(deliveryObj.getString(Keys.PICKUP_CITY));
                            delivery.setDropoffCountry(deliveryObj.getString(Keys.DROPOFF_COUNTRY));
                            delivery.setDropoffState(deliveryObj.getString(Keys.DROPOFF_STATE));
                            delivery.setDropoffCity(deliveryObj.getString(Keys.DROPOFF_CITY));
                            //delivery.setWidth(deliveryObj.getString(Keys.ITEM_WIDTH));
                            delivery.setStatus(deliveryObj.getString(Keys.STATUS));
                            // delivery.setStackable(deliveryObj.getString(Keys.ITEM_STACKABLE));
                            delivery.setReceiver(deliveryObj.getString(Keys.RECEIVER_NAME));
                            delivery.setReceiverSign(deliveryObj.getString(Keys.RECEIVER_SIGN));
                            delivery.setSender(deliveryObj.getString(Keys.FROM_PICKUP_NAME));
                            delivery.setSenderSign(deliveryObj.getString(Keys.FROM_PICKUP_SIGN));
                            delivery.setQty(deliveryObj.getString(Keys.ITEM_QTY));
                            //delivery.setWeight(deliveryObj.getString(Keys.ITEM_WEIGHT));
                            delivery.setPuMobile(deliveryObj.getString(Keys.PICKUP_MOBILE));
                            delivery.setPuContactPerson(deliveryObj.getString(Keys.PICKUP_CONTACT_PERSON));
                            //delivery.setDoMobile(deliveryObj.getString(Keys.DROPOFF_MOBILE));
                            delivery.setPuLat(deliveryObj.getString(Keys.PICKUP_LATITUDE));  //plz chk
                            delivery.setPuLng(deliveryObj.getString(Keys.PICKUP_LONGITUDE));
                            delivery.setDoContactPerson(deliveryObj.getString(Keys.DROPOFF_CONTACT));
                            delivery.setDoLat(deliveryObj.getString(Keys.DROPOFF_LATITUDE)); //plz chk
                            delivery.setDoLng(deliveryObj.getString(Keys.DROPOFF_LONGITUDE));
                            //delivery.setPuLiftEquipment(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIPMENT));
                            //delivery.setPuBuildType(deliveryObj.getString(Keys.PICKUP_BUILD_TYPE));
                            //delivery.setDoBuildType(deliveryObj.getString(Keys.DROPOFF_BUILD_TYPE));
                            //delivery.setDoLiftEquipment(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIPMENT));
                            delivery.setFixedOffer(deliveryObj.getString(Keys.FIXED_OFFER));
                            delivery.setAuctionStart(deliveryObj.getString(Keys.AUCTION_START_TIME));
                            delivery.setAuctionEnd(deliveryObj.getString(Keys.AUCTION_END_TIME));
                            delivery.setMaxOpeningBid(deliveryObj.getString(Keys.MAX_AUCTION_BID));
                            delivery.setAuctionBid(deliveryObj.getString(Keys.AUCTION_BID));
                            delivery.setCompanyLogo(deliveryObj.getString(Keys.COMPANY_LOGO));
                            delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                            delivery.setCompleted_at(deliveryObj.getString("completed_at"));
                            //delivery.setDoAppoint(deliveryObj.getString(Keys.DROPOFF_APPOINTMENT));
                            //delivery.setMore(deliveryObj.getString(Keys.ITEM_MORE));
                            delivery.setDoCall(deliveryObj.getString(Keys.DROPOFF_CALL));
                            delivery.setUser_Group(deliveryObj.getString(Keys.USER_GROUP));
                            delivery.setPayment_mode(deliveryObj.getString(Keys.PAYMENT_MODE));
                            try {
                                delivery.setRelistNotification(deliveryObj.getString(Keys.RELIST_NOTIFICATION));
                            } catch (Exception e) {
                                e.toString();
                            }
                    /*if (!deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID).equals("null")) {
                        delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID));
                        delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_NAME));
                    } else {
                        delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_ID));
                        delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_NAME));
                    }
                    */
                            if (deliveryObj.get(Config.BOOKMARK).toString().equals("null")) {
                            } else {
                                delivery.setBookmarkID(deliveryObj.getJSONObject(Config.BOOKMARK).getString(Keys.KEY_ID));
                            }
                            try {
                                delivery.setFav_user_id(deliveryObj.getString(Keys.FAVOURITE_USER_GROUP_IDS));
                            } catch (Exception e) {
                                e.toString();
                            }
                            delivery.setGeo(deliveryObj.getString(Keys.GEO_ZONE));
                            delivery.setRadius(deliveryObj.getString(Keys.RADIUS));

                            delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS)); //added
                            delivery.setDeliveryName(deliveryObj.getString(Keys.DELIVERY_TYPE_NAME));
                            // delivery.setDeliveryTypeID(deliveryObj.getString(Keys.DELIVERY_TYPE_ID));
                            //delivery.setDeliveryTypeSubSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID));
                            // delivery.setDeliveryTypeSubSubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_NAME));
                            //delivery.setCurrentVaccination(deliveryObj.getString(Keys.ITEM_CURRENT_VACCINATIONS));
                            //delivery.setAnimalCarrier(deliveryObj.getString(Keys.ITEM_ANIMAL_CARRIER));
                            delivery.setShipperName(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                            delivery.setShipperImage(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                            delivery.setShipperRating(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                            delivery.setShipperID(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                            try {
                                if (deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).has(Keys.MOBILE))
                                    delivery.setSender_Mobile(deliveryObj.getJSONArray(Keys.SHIPPER_DETAIL).getJSONObject(0).getString(Keys.MOBILE));
                            } catch (Exception e) {
                                e.toString();
                            }
                            delivery.setBidArray(deliveryObj.get(Keys.BIDS).toString());
                            delivery.setChoosedBidsArray(deliveryObj.get(Keys.BID_CHOOSED).toString());
                            delivery.setHasDriverFeedback(deliveryObj.get(Keys.FEEDBACK_TO_DRIVER).toString().equals("null") ? false : true);
                            delivery.setHasShipperFeedback(deliveryObj.get(Keys.FEEDBACK_TO_SHIPPER).toString().equals("null") ? false : true);
                            // delivery.setItemsData(deliveryObj.get(Keys.DELIVERY_ITEMS).toString());
                            delivery.setSuitableVehicles(deliveryObj.get(Keys.SUITABLE_VEHICLE_TEXT).toString());
                            // delivery.setDropoffDateType(deliveryObj.getString(Keys.DROPOFF_DAY_TYPE));
                            //delivery.setPickupDateType(deliveryObj.getString(Keys.PICKUP_DAY_TYPE));
                            //delivery.setDropOffEndDate(deliveryObj.getString(Keys.DROPOFF_END_DAY));
                            //delivery.setPickupEndDate(deliveryObj.getString(Keys.PICKUP_END_DAY));
                            //delivery.setPickupinductionRequire(deliveryObj.getString(Keys.PICKUP_INDUCTION_REQUIRE));
                            delivery.setPickupSpecialRestriction(deliveryObj.getString(Keys.PICKUP_SPECIAL_RESTRICTION));
                            // delivery.setDropoffinductionRequire(deliveryObj.getString(Keys.DROPOFF_INDUCTION_REQUIRE));
                            // delivery.setDropoffSpecialRestriction(deliveryObj.getString(Keys.DROPOFF_SPECIAL_RESTRICTION));
                            //delivery.setSpecialPermit(deliveryObj.getString(Keys.SPECIAL_PERMIT));
                            delivery.setSpecialPermitDetail(deliveryObj.getString(Keys.SPECIAL_PERMIT_DETAIL));
                            delivery.setSuitabelVehicle(deliveryObj.getString(Keys.SUITABLE_VEHICAL_IDS));
                            // delivery.setPickUpLiftEquiAvailableIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_IDS));
                            //delivery.setDropOffLiftEquiAvailableIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_IDS));
                            //delivery.setPickUpLiftEquiNeededIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_IDS));
                            //delivery.setDropOffLiftEquiNeededIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_IDS));
                   /* try {
                        delivery.setPuLiftEquipmentText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT));
                        delivery.setPuLiftEquipmentNeededText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT));
                        delivery.setDoLiftEquipmentText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT));
                        delivery.setDoLiftEquipmentNeededText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT));

                    } catch (Exception e) {
                        e.toString();

                    }*/
                            delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                            delivery.setCompleted_at(deliveryObj.getString("completed_at"));
                            try {
                                if (!delivery.getDeliveryStatus().equals("0")) {
                                    delivery.setDriver_name(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.USERNAME));
                                    delivery.setDriver_image(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.PROFILE_IMAGE));
                                    delivery.setDriver_rating(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.RATING));
                                    delivery.setDriver_id(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.KEY_ID));
                                    delivery.setIsFavouriteUser(deliveryObj.getJSONArray(Keys.DRIVER_DETAIL).getJSONObject(0).getString(Keys.ISFAVOURITE_USER));
                                }
                            } catch (Exception e) {
                                e.toString();
                            }
                            deliveryData.add(delivery);
                        }
                        try {
                            if (dataObj.has("_meta")) {
                                JSONObject metaCount = dataObj.getJSONObject("_meta");
                                totalCount = Integer.parseInt(metaCount.optString("pageCount"));
                                Log.v("totalcount", String.valueOf(totalCount));

                            }
                        } catch (Exception e) {
                            e.toString();
                        }

                        if (deliveryData.size() > 0) {
                            String backStateName = this.getClass().getName();
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("data", deliveryData);
                            bundle.putString("url", url);
                            bundle.putInt("totalcount", totalCount);
                            reset();
                            Fragment fragment = new SearchResult();
                            fragment.setArguments(bundle);
                            getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName).addToBackStack(null).commitAllowingStateLoss();
                        } else {

                        }
                    } catch (Exception ex) {
                       String backStateName = this.getClass().getName();
                       Fragment fragment = new SearchResult();
                       getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName).addToBackStack(null).commitAllowingStateLoss();
                        //AlertManager.messageDialog(getActivity(), "Alert!", outterJson.getString(Config.MESSAGE));
                    }
                }
            } else{
                String backStateName = this.getClass().getName();
                Fragment fragment = new SearchResult();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName).addToBackStack(null).commitAllowingStateLoss();
            }
                //AlertManager.messageDialog(getActivity(), "Alert!", outterJson.getString(Config.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getAutoCompleteUrl(String place) {
        String key = "key=AIzaSyCpbcLPcwz1NiJpl_WWJ2H2GfFaqwj748Q";
        //String key = "key=AIzaSyDxBqk-VhEklzJyUW3cQ1PE9tsbFQQPYWo";
        String input = "input=" + place;
        String types = "types=geocode";
        String sensor = "sensor=false";
        String compp = "components=country:au|country:in";
        String parameters = input + "&" + types + "&" + sensor + "&" + key + "&" + compp;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
        return url;
    }

    private String getPlaceDetailsUrl(String ref) {
        String key = "key=AIzaSyBl2oMPV7lzRd9L2MWKg6QAkk1ybiBP0DU";
        String reference = "reference=" + ref;
        String sensor = "sensor=false";
        String parameters = reference + "&" + sensor + "&" + key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/place/details/" + output + "?" + parameters;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        private int downloadType = 0;

        public DownloadTask(int type) {
            this.downloadType = type;
        }

        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            switch (downloadType) {
                case PLACES:
                    placesParserTask = new ParserTask(PLACES);
                    placesParserTask.execute(result);
                    break;
            }
        }
    }

    String srcCity, srcState, srcCountry, destCity, destState, destCountry;

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
        int parserType = 0;

        public ParserTask(int type) {
            this.parserType = type;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<HashMap<String, String>> list = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                switch (parserType) {
                    case PLACES:
                        PlaceJSONParser placeJsonParser = new PlaceJSONParser();
                        list = placeJsonParser.parse(jObject);
                        break;
                }
            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return list;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            switch (parserType) {
                case PLACES:
                    try {
                        String[] from = new String[]{"description"};
                        int[] to = new int[]{android.R.id.text1};
                        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
                        if (searchType == 0)
                            source.setAdapter(adapter);
                        else if (searchType == 1)
                            destination.setAdapter(adapter);
                    } catch (Exception e) {
                        e.toString();
                    }
                    break;
            }
        }
    }
}
package com.bookmyride.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.PlaceSearchAdapter;
import com.bookmyride.api.APIHandlerInBack;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.models.LocationModel;
import com.bookmyride.views.AlertDialog;

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

public class LocationSearch extends AppCompatActivity {
    int type = 0;
    private RelativeLayout back;
    private EditText et_search;
    private ListView listview;
    private RelativeLayout alert_layout;
    private TextView alert_textview;
    private TextView tv_emptyText;
    private ProgressBar progresswheel;

    private APIHandlerInBack mRequest;

    Context context;
    ArrayList<String> itemList_location = new ArrayList<String>();
    ArrayList<String> itemList_placeId = new ArrayList<String>();

    PlaceSearchAdapter adapter;
    private boolean isdataAvailable = false;
    private boolean isEstimateAvailable = false;

    private String Slatitude = "", Slongitude = "", Sselected_location = "";

    Dialog dialog;
    ArrayList<LocationModel> ratecard_list = new ArrayList<LocationModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_search);
        context = getApplicationContext();
        initialize();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sselected_location = itemList_location.get(position);

                if (Internet.hasInternet(LocationSearch.this)) {
                    type = 1;
                    //LatLongRequest(Config.GetAddressFrom_LatLong_url + itemList_placeId.get(position));
                    DownloadTask dt = new DownloadTask();
                    String url = Config.GetAddressFrom_LatLong_url + itemList_placeId.get(position);
                    dt.execute(url);
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.no_internet));
                }
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (Internet.hasInternet(LocationSearch.this)) {
                    /*if (mRequest != null) {
                        mRequest.cancel(true);
                    }
                    CitySearchRequest(Config.place_search_url + et_search.getText().toString().toLowerCase().replace(" ", "%20"));*/
                    type = 0;
                    String url = getAutoCompleteUrl(s.toString());
                    DownloadTask placesDownloadTask = new DownloadTask();
                    placesDownloadTask.execute(url);
                } else {
                    alert_layout.setVisibility(View.VISIBLE);
                    alert_textview.setText(getResources().getString(R.string.no_internet));
                }

            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    CloseKeyboard(et_search);
                }
                return false;
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

                LocationSearch.this.finish();
                LocationSearch.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
    }

    private String getAutoCompleteUrl(String place) {
        String key = "key=AIzaSyDxBqk-VhEklzJyUW3cQ1PE9tsbFQQPYWo";
        String input = "input=" + place;
        String types = "types=geocode";
        String sensor = "sensor=false";
        String parameters = input + "&" + types + "&" + sensor + "&" + key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
        return url;
    }

    private void initialize() {
        alert_layout = (RelativeLayout) findViewById(R.id.location_search_alert_layout);
        alert_textview = (TextView) findViewById(R.id.location_search_alert_textView);
        back = (RelativeLayout) findViewById(R.id.location_search_back_layout);
        et_search = (EditText) findViewById(R.id.location_search_editText);
        listview = (ListView) findViewById(R.id.location_search_listView);
        progresswheel = (ProgressBar) findViewById(R.id.location_search_progressBar);
        tv_emptyText = (TextView) findViewById(R.id.location_search_empty_textview);
    }

    private void CloseKeyboard(EditText edittext) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(edittext.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //--------------Alert Method-----------
    private void Alert(String title, String alert) {
        final AlertDialog mDialog = new AlertDialog(LocationSearch.this, true);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(alert);
        mDialog.setPositiveButton(getResources().getString(android.R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject object = new JSONObject(result);
                if (type == 0) {
                    if (object.getString(Key.STATUS).equals("OK")) {
                        JSONArray place_array = object.getJSONArray("predictions");
                        if (place_array.length() > 0) {
                            itemList_location.clear();
                            itemList_placeId.clear();
                            for (int i = 0; i < place_array.length(); i++) {
                                JSONObject place_object = place_array.getJSONObject(i);
                                itemList_location.add(place_object.getString("description"));
                                itemList_placeId.add(place_object.getString("place_id"));
                            }
                            isdataAvailable = true;
                        } else {
                            itemList_location.clear();
                            itemList_placeId.clear();
                            isdataAvailable = false;
                        }
                    } else {
                        itemList_location.clear();
                        itemList_placeId.clear();
                        isdataAvailable = false;
                    }
                    adapter = new PlaceSearchAdapter(LocationSearch.this, itemList_location);
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    JSONObject place_object = object.getJSONObject("result");
                    JSONObject geometry_object = place_object.getJSONObject("geometry");
                    JSONObject location_object = geometry_object.getJSONObject("location");
                    Slatitude = location_object.getString("lat");
                    Slongitude = location_object.getString("lng");

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", Slatitude);
                    returnIntent.putExtra("Selected_Longitude", Slongitude);
                    returnIntent.putExtra("Selected_Location", Sselected_location);
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------Get Latitude and Longitude from Address(Place ID) Request----------------
    private void LatLongRequest(String Url) {
        System.out.println("--------------LatLong url-------------------" + Url);

        mRequest = new APIHandlerInBack(LocationSearch.this, HTTPMethods.GET, new AsyncTaskCompleteListener() {
            @Override
            public void onTaskComplete(String result) {
                System.out.println("--------------LatLong  reponse-------------------" + result);
                String status = "";
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.length() > 0) {
                        status = object.getString("status");
                        JSONObject place_object = object.getJSONObject("result");
                        if (status.equalsIgnoreCase("OK")) {
                            if (place_object.length() > 0) {
                                JSONObject geometry_object = place_object.getJSONObject("geometry");
                                if (geometry_object.length() > 0) {
                                    JSONObject location_object = geometry_object.getJSONObject("location");
                                    if (location_object.length() > 0) {
                                        Slatitude = location_object.getString("lat");
                                        Slongitude = location_object.getString("lng");
                                        isdataAvailable = true;
                                    } else {
                                        isdataAvailable = false;
                                    }
                                } else {
                                    isdataAvailable = false;
                                }
                            } else {
                                isdataAvailable = false;
                            }
                        } else {
                            isdataAvailable = false;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (isdataAvailable) {

                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("Selected_Latitude", Slatitude);
                    returnIntent.putExtra("Selected_Longitude", Slongitude);
                    returnIntent.putExtra("Selected_Location", Sselected_location);
                    setResult(RESULT_OK, returnIntent);
                    onBackPressed();
                    finish();

                } else {
                    dialog.dismiss();
                    Alert("Alert!", status);
                }
            }
        }, null);
        mRequest.execute("", "");

    }


    //-----------------Move Back on pressed phone back button------------------
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)) {

            // close keyboard
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(back.getWindowToken(), 0);

            LocationSearch.this.finish();
            LocationSearch.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return true;
        }
        return false;
    }

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
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}


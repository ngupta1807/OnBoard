package com.bookmyride.activities.register;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bookmyride.R;
import com.bookmyride.api.APIHandler;
import com.bookmyride.api.APIStatus;
import com.bookmyride.api.AsyncTaskCompleteListener;
import com.bookmyride.api.Config;
import com.bookmyride.api.HTTPMethods;
import com.bookmyride.api.Key;
import com.bookmyride.common.Internet;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.common.Validator;
import com.bookmyride.imageCrop.Constants;
import com.bookmyride.imageCrop.ImageCropActivity;
import com.bookmyride.imageCrop.PicModeSelectDialogFragment;
import com.bookmyride.models.Category;
import com.bookmyride.util.ImageLoader;
import com.bookmyride.views.AlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
/**
 * Created by Vinod on 2017-01-07.
 */
public class Location extends AppCompatActivity implements
        View.OnClickListener, AsyncTaskCompleteListener,
        PicModeSelectDialogFragment.IPicModeSelectListener{
    TextView pic, location, carType, next, expiry;
    ImageView editImg, img, back;
    RelativeLayout layImg;
    ScrollView scroll;
    String locationLat = "", locationLng = "";
    String locationID = "";
    String carTypeID = "";
    LinearLayout layTaxi;
    SessionHandler session;
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    ImageLoader imgLoader;
    TextView title;
    TextView reg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        init();
        getLocations();

        deleteFiles(session.getTaxiImgPath());
        deleteFiles(session.getLicenceImgPath());
        deleteFiles(session.getProfileImgPath());

        deleteFiles(getFile("ride_taxi_photo.jpg"));
        deleteFiles(getFile("ride_licence_photo.jpg"));
        deleteFiles(getFile("ride_profile_photo.jpg"));
    }
    private void deleteFiles(String path) {
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("file Deleted :" + path);
            } else {
                System.out.println("file not Deleted :" + path);
            }
        }
    }
    private void init(){
        imgLoader = new ImageLoader(this);
        session = new SessionHandler(this);
        location = (TextView) findViewById(R.id.location);
        location.setOnClickListener(this);
        carType = (TextView) findViewById(R.id.car_type);
        carType.setOnClickListener(this);
        pic = (TextView) findViewById(R.id.pic);
        pic.setOnClickListener(this);
        title = (TextView) findViewById(R.id.signin_header_Tv);
        layImg = (RelativeLayout) findViewById(R.id.lay_img);
        editImg = (ImageView) findViewById(R.id.edit_img);
        editImg.setOnClickListener(this);
        img = (ImageView) findViewById(R.id.img);
        scroll = (ScrollView)findViewById(R.id.scroll);
        back  = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        layTaxi = (LinearLayout) findViewById(R.id.lay_taxi);
        next = (TextView) findViewById(R.id.continu);
        next.setOnClickListener(this);
        expiry = (TextView) findViewById(R.id.accreditation_expiry);
        expiry.setOnClickListener(this);
        reg = (TextView) findViewById(R.id.reg);

    }
    String isCustomer = "0";
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.accreditation_expiry:
                showDatePicker();
                break;
            case R.id.continu:
                if(isValid()) {
                    if(getIntent().hasExtra("is_customer"))
                        isCustomer = getIntent().getStringExtra("is_customer");
                    session.saveLocation(isCustomer, location.getText().toString(), locationID, jArray, expiry.getText().toString());
                    /*session.saveLocation("", locationID, "", carTypeID, expiry.getText().toString());*/
                    Intent intent = new Intent(getApplicationContext(), DriverInfo.class);
                    if(getIntent().hasExtra("social_data")){
                        intent.putExtra("social_data",getIntent().getStringExtra("social_data"));
                        intent.putExtra("type",getIntent().getStringExtra("type"));
                    }
                    if(getIntent().hasExtra("upgrade"))
                        intent.putExtra("upgrade",getIntent().getStringExtra("upgrade"));
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
                break;
            case R.id.location:
                showRideDialog(1);
                break;
            case R.id.car_type:
                if(location.getText().toString().equals(""))
                    Alert("Alert!", "Please select driver location first.");
                else
                    showRideDialog(2);
                break;
            case R.id.pic:
                showAddProfilePicDialog();
                break;
            case R.id.edit_img:
                showAddProfilePicDialog();
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    public boolean isValid() {
        if(location.getText().toString().equals("")) {
            Alert("Oops !!!","Please select driver location.");
            return false;
        } else if(carType.getText().toString().equals("")) {
            Alert("Oops !!!","Please select ride category.");
            return false;
        } else if(carType.getText().toString().contains("Taxi")) {
            if(expiry.getText().toString().equals("")) {
                Alert("Oops !!!", "Please select taxi accreditation expiry.");
                return false;
            } else if(!Validator.isValidDate(expiry.getText().toString())) {
                Alert("Oops !!!", "Invalid Date. Taxi accreditation expiry date should be greater than OR equal to current date.");
                return false;
            } else if(!hasImage(img) && !hasImgUrl) {
                Alert("Oops !!!", "Please choose taxi accreditation image.");
                return false;
            }
        }
        return true;
    }
    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);

        if (hasImage && (drawable instanceof BitmapDrawable)) {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }

        return hasImage;
    }

    Dialog mDialog;
    public void showRideDialog(final int type) {
        mDialog = new Dialog(Location.this, R.style.rideDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list);
        mDialog.setCanceledOnTouchOutside(true);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);

        if (type == 1)
            title.setText("Select Driver Location");
        else if (type == 2)
            title.setText("Select Ride Category");

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
        ArrayAdapter<String> adapter = null;
        CategoryAdapter catAdapter = null;

        if (type == 1) {
            adapter = new ArrayAdapter<>(this,
                    R.layout.simple_list_item, R.id.textItem, getCountryList());

            dialog_ListView.setAdapter(adapter);
            dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    if (type == 1) {
                        location.setText(parent.getItemAtPosition(position).toString());
                        carType.setText("");
                        expiry.setText("");
                        layTaxi.setVisibility(View.GONE);
                        session.saveProfileImgPath("");
                        img.setImageBitmap(null);
                        pic.setVisibility(View.VISIBLE);
                        locationID = getCountryId(parent.getItemAtPosition(position).toString());
                        getCarType(locationID);
                    } else if (type == 2) {
                        String category = parent.getItemAtPosition(position).toString();
                        carType.setText(category);
                        carTypeID = getStateId(parent.getItemAtPosition(position).toString());
                        if(category.equalsIgnoreCase("taxi")) {
                            layTaxi.setVisibility(View.VISIBLE);
                        } else layTaxi.setVisibility(View.GONE);

                    }
                    mDialog.dismiss();
                }
            });
        }
        else if (type == 2) {
            if(carType.getText().toString().equals(""))
                categoryList = getStateList("");
            catAdapter = new CategoryAdapter(Location.this);
            dialog_ListView.setAdapter(catAdapter);
        }
        mDialog.show();
    }

    public String[] getCountryList() {
        String[] listContent = new String[locationData.size()];
        for (int i = 0; i < locationData.size(); i++) {
            listContent[i] = locationData.get(i).get(Key.CITY);
        }
        return listContent;
    }
    public void onBack(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        session.saveSessionCode("");
        Location.this.finish();
    }

    public String getCountryId(String countryName) {
        for (int i = 0; i < locationData.size(); i++) {
            if (locationData.get(i).get(Key.CITY).equalsIgnoreCase(countryName))
                return locationData.get(i).get(Key.ID);
        }

        return "";
    }

    private void getCarType(String locationID) {
        carTypeData.clear();
        int index = getIndex(locationID);
        String carData = locationData.get(index).get(Key.DRIVER_CATEGORY);
        try {
            JSONArray jsonArray = new JSONArray(carData);
            for(int i =0; i<jsonArray.length(); i++){
                JSONObject innerJson = jsonArray.getJSONObject(i);
                HashMap<String, String> map = new HashMap<>();
                map.put(Key.ID, innerJson.get(Key.ID).toString());
                map.put(Key.NAME, innerJson.getString(Key.NAME));
                carTypeData.add(map);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private int getIndex(String locationID) {
        for(int i=0; i<locationData.size(); i++){
            if(locationData.get(i).get(Key.ID).equals(locationID))
                return i;
        }
        return 0;
    }

    public String getStateId(String stateName) {
        for (int i = 0; i < carTypeData.size(); i++) {
            if (carTypeData.get(i).get(Key.NAME).equalsIgnoreCase(stateName))
                return carTypeData.get(i).get(Key.ID);
        }
        return "";
    }
    public ArrayList<Category> getStateList(String ids) {
        jArray = new JSONArray();
        ArrayList<Category> category = new ArrayList<Category>();
        for(int i = 0; i< carTypeData.size(); i++) {
            Category cat = new Category();
            cat.setId(carTypeData.get(i).get(Key.ID));
            cat.setName(carTypeData.get(i).get(Key.NAME));
            if(ids.contains(carTypeData.get(i).get(Key.ID))) {
                cat.setSelected(true);
                builder += carTypeData.get(i).get(Key.NAME)+", ";
                try {
                    JSONObject object = new JSONObject();
                    object.put("name", carTypeData.get(i).get(Key.NAME));
                    object.put("id", carTypeData.get(i).get(Key.ID));
                    jArray.put(object);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            } else
                cat.setSelected(false);
            category.add(cat);
        }
        return category;
    }

    int type;
    ArrayList<HashMap<String, String>> locationData = new ArrayList<>();
    ArrayList<HashMap<String, String>> carTypeData = new ArrayList<>();

    private void getLocations() {
        type = 1;
        if(Internet.hasInternet(this)){
            APIHandler apiHandler = new APIHandler(this, HTTPMethods.GET, this,null);
            apiHandler.execute(Config.LOCATION, "");
        } else
            Alert("Alert!", getResources().getString(R.string.no_internet));
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (Integer.parseInt(outJson.getString(Key.STATUS)) == APIStatus.SUCCESS) {
                locationData.clear();
                carTypeData.clear();

                JSONArray outerArray = outJson.getJSONArray(Key.DATA);

                for (int i = 0; i < outerArray.length(); i++) {
                    JSONObject innerJson = outerArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(Key.ID, innerJson.get(Key.ID).toString());
                    map.put(Key.CITY, innerJson.getString(Key.CITY));
                    map.put(Key.LOCATION, innerJson.get(Key.LOCATION).toString());
                    map.put(Key.DRIVER_CATEGORY, innerJson.get(Key.DRIVER_CATEGORY).toString());
                    locationData.add(map);
                }
            } else
                Alert("Alert!", outJson.getString(Key.MESSAGE));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTaskComplete(String result) {
        handleResponse(result);
        if(getIntent().hasExtra("LoginData")) {
            if (!getIntent().getExtras().getString("LoginData").equals("")) {
                saveUserInfo(getIntent().getExtras().getString("LoginData"));
                setData();
            }
        }
    }

    private void Alert(String title, String message) {
        final AlertDialog mDialog = new AlertDialog(Location.this, true);
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

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date
        DatePickerDialog datePicker = new DatePickerDialog(Location.this,
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
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            String year1 = String.valueOf(selectedYear);
            String month1 = String.valueOf(selectedMonth + 1);
            String day1 = String.valueOf(selectedDay);
            //expiry.setText(day1 + "/" + month1 + "/" + year1);
            expiry.setText(year1 + "-" + month1 + "-" + day1);
        }
    };
    ArrayList<Category> categoryList = new ArrayList<Category>();
    JSONArray jArray;
    private class CategoryAdapter extends BaseAdapter{
        Context ctx;
        CategoryAdapter(Context ctx){
            this.ctx = ctx;
        }
        @Override
        public int getCount() {
            return categoryList.size();
        }

        @Override
        public Object getItem(int i) {
            return categoryList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater vi = (LayoutInflater)getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_list_item_check, null);
            Category country = (Category) getItem(position);
            final TextView name = (TextView) convertView.findViewById(R.id.textItem);
            final CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
            TextView done = (TextView) convertView.findViewById(R.id.done);
            name.setTag(position);

            if(position == categoryList.size()-1){
                done.setVisibility(View.VISIBLE);
            } else done.setVisibility(View.GONE);

            name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    if(check.isChecked())
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    Category country = categoryList.get((int)name.getTag());
                    country.setSelected(check.isChecked());
                    categoryList.set((int)name.getTag(), country);
                    notifyDataSetChanged();
                }
            });
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    Category country = categoryList.get((int)name.getTag());
                    country.setSelected(isChecked);
                    categoryList.set((int)name.getTag(), country);
                    notifyDataSetChanged();
                }
            });
            done.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    String builder = "";
                    jArray = new JSONArray();
                    try {
                        for (int i = 0; i < categoryList.size(); i++) {
                            if (categoryList.get(i).isSelected()) {
                                builder += categoryList.get(i).getName() + ", ";
                                JSONObject object = new JSONObject();
                                object.put("name", categoryList.get(i).getName());
                                object.put("id",categoryList.get(i).getId());
                                jArray.put(object);
                            }
                        }
                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                    if(builder.equals("")){
                        Alert("Oops !!!", "Please select driver category");
                    } else {
                        carType.setText(builder.substring(0, builder.length() - 2));
                        if (carType.getText().toString().contains("Taxi")) {
                            layTaxi.setVisibility(View.VISIBLE);
                        } else {
                            //expiry.setText("");
                            //session.saveProfileImgPath("");
                            //img.setImageBitmap(null);
                            pic.setVisibility(View.VISIBLE);
                            layTaxi.setVisibility(View.GONE);
                        }
                        if (mDialog != null && mDialog.isShowing())
                            mDialog.dismiss();
                    }
                }
            });
            name.setText(country.getName());
            check.setChecked(country.isSelected());
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            } else if (resultCode == RESULT_CANCELED) {

            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        intent.putExtra("rect", "");
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            session.saveTaxiImgPath(mImagePath);
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            img.setImageBitmap(myBitmap);
            layImg.setVisibility(View.VISIBLE);
            pic.setVisibility(View.GONE);
        }
    }
    private void showAddProfilePicDialog() {
        ImageCropActivity.TEMP_PHOTO_FILE_NAME = "ride_taxi_photo.jpg";
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private String getFile(String fileName) {
        File mFileTemp;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), fileName);
        } else {
            mFileTemp = new File(getFilesDir(), fileName);
        }
        return mFileTemp.getPath();
    }

    boolean hasImgUrl = false;
    private void setData() {
        title.setText("Profile");
        reg.setText("Profile Details ");
        try {
            JSONObject locationObject = new JSONObject(locationSelectedData);
            location.setText(getCountryName(locationObject.getString(Key.LOCATION_ID)));
            locationID = locationObject.getString(Key.LOCATION_ID);
            if(!locationObject.getString(Key.EXPIRY).equals("") &&
                    !locationObject.getString(Key.EXPIRY).equals("null"))
                expiry.setText(locationObject.getString(Key.EXPIRY));
            getCarType(locationID);
            categoryList = getStateList(locationObject.getString(Key.CATEGORY));
            carType.setText(builder.substring(0, builder.length() - 2));
            if (carType.getText().toString().contains("Taxi"))
                layTaxi.setVisibility(View.VISIBLE);
            else
                layTaxi.setVisibility(View.GONE);

            if(!locationObject.getString(Key.TAXI_ACCREDITATION_CARD).equals("") &&
                    !locationObject.getString(Key.TAXI_ACCREDITATION_CARD).equals("null")){
                hasImgUrl = true;
                pic.setVisibility(View.GONE);
                layImg.setVisibility(View.VISIBLE);
                imgLoader.DisplayImage(locationObject.getString(Key.TAXI_ACCREDITATION_CARD), img);
            } else {
                hasImgUrl = false;
                pic.setVisibility(View.VISIBLE);
                layImg.setVisibility(View.GONE);
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    String builder = "";
    public String getCountryName(String countryId) {
        String countryName = "";
        for (int i = 0; i < locationData.size(); i++) {
            if (locationData.get(i).get(Key.ID).equalsIgnoreCase(countryId)) {
                countryName = locationData.get(i).get(Key.CITY);
                break;
            }
        }
        return countryName;
    }
    public void saveUserInfo(String response) {
        try {
            JSONObject responseObj=new JSONObject(response);

            JSONObject allData = responseObj.getJSONObject(Key.DATA);
            isCustomer = allData.get(Key.IS_CUSTOMER).toString();
            isPremium = allData.get(Key.IS_PREMIUM).toString();
            JSONObject driverObject = new JSONObject();
            //get basic info
            driverObject.put(Key.FIRST_NAME, allData.getString(Key.FIRST_NAME));
            driverObject.put(Key.LAST_NAME, allData.getString(Key.LAST_NAME));
            driverObject.put(Key.EMAIL, allData.getString(Key.EMAIL));
            driverObject.put(Key.USERNAME, allData.getString(Key.USERNAME));
            driverObject.put(Key.USER_TYPE, allData.getString(Key.USER_TYPE));
            driverObject.put(Key.IMAGE, allData.getString(Key.IMAGE));
            driverObject.put(Key.LOCATION_ID, allData.getString(Key.LOCATION_ID));

            //object.put(Key.REFERRAL_CODE,allData.getString(Key.REFERRAL_CODE));

            JSONObject isOnline = new JSONObject(allData.getString("isOnline"));
            String driver_cat_id = isOnline.getString("driverCategory_id");
            String status = isOnline.getString(Key.STATUS);

            //get location
            JSONObject location = new JSONObject();
            JSONObject profileData = allData.getJSONObject("profile");

            //object.put(Key.ID,profileData.getString(Key.ID));
            location.put(Key.LOCATION_ID, allData.getString(Key.LOCATION_ID));
            location.put(Key.IS_CUSTOMER, isCustomer);
            location.put(Key.CATEGORY, profileData.getString(Key.CATEGORY));
            location.put(Key.EXPIRY, profileData.getString("taxiAccreditationExpiryDate"));
            location.put(Key.TAXI_ACCREDITATION_CARD, profileData.getString(Key.TAXI_ACCREDITATION_CARD));

            //object.put(Key.LICENCE_IMAGE,profileData.getString(Key.LICENCE_IMAGE));

            driverObject.put(Key.LICENCE_NUMBER, profileData.getString("licenceNumber"));
            driverObject.put(Key.LICENCE_EXPIRY, profileData.getString("licenceExpiry"));
            driverObject.put(Key.LICENCE_IMAGE, profileData.getString("licenceImage"));
            //get address
            JSONObject addressObject = new JSONObject();
            String addressData = profileData.getString("address");

            JSONObject addObject = new JSONObject(addressData);

            addressObject.put(Key.CITY, addObject.getString(Key.CITY));
            addressObject.put(Key.STATE, addObject.getString(Key.STATE));
            addressObject.put(Key.COUNTRY, addObject.getString(Key.COUNTRY));
            addressObject.put(Key.ADDRESS, addObject.getString(Key.ADDRESS));
            addressObject.put(Key.LONGITUDE, addObject.getString(Key.LONGITUDE));
            addressObject.put(Key.LATITUDE, addObject.getString(Key.LATITUDE));
            addressObject.put(Key.POSTALCODE, addObject.getString(Key.POSTALCODE));
            addressObject.put(Key.PHONE, allData.getString(Key.PHONE));
            addressObject.put(Key.DIAL_CODE, allData.getString(Key.DIAL_CODE));

            //get vehicle data
            JSONArray vehicleObject = new JSONArray();

            if(!profileData.getString("vehicleDetail").equals("") &&
                    !profileData.getString("vehicleDetail").equals("null")) {
                JSONArray array = new JSONArray(profileData.getString("vehicleDetail"));

                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    JSONObject vehicleObj = new JSONObject();
                    vehicleObj.put(Key.VEHICLE_TYPE, obj.getString(Key.VEHICLE_TYPE));
                    vehicleObj.put(Key.TYPE, obj.getString(Key.TYPE));
                    vehicleObj.put(Key.TYPE_NAME, obj.getString(Key.TYPE_NAME));
                    vehicleObj.put(Key.YEAR, obj.has(Key.YEAR) ? obj.getString(Key.YEAR) : "2000");
                    vehicleObj.put(Key.MAKER, obj.getString(Key.MAKER));
                    vehicleObj.put(Key.MAKER_NAME, obj.getString(Key.MAKER_NAME));
                    vehicleObj.put(Key.MAKER_NAME_OTHER, obj.getString(Key.MAKER_NAME_OTHER));
                    vehicleObj.put(Key.MODEL, obj.getString(Key.MODEL));
                    vehicleObj.put(Key.MODEL_NAME, obj.getString(Key.MODEL_NAME));
                    vehicleObj.put(Key.MODEL_NAME_OTHER, obj.getString(Key.MODEL_NAME_OTHER));
                    vehicleObj.put(Key.CATEGORY, obj.getString(Key.CATEGORY));
                    vehicleObj.put(Key.AC, obj.getString(Key.AC));
                    vehicleObj.put(Key.FLEET_NAME, obj.getString(Key.FLEET_NAME));
                    vehicleObj.put(Key.FLEET_ID, obj.getString(Key.FLEET_ID));
                    vehicleObj.put(Key.IS_FLEET_SELECTED, obj.getString(Key.IS_FLEET_SELECTED));
                    vehicleObj.put(Key.REGISTRATION_NUMBER, obj.getString(Key.REGISTRATION_NUMBER));
                    vehicleObject.put(vehicleObj);
                }
            }
            if(allData.getString("referralCode") != null)
                selectedRefferelCode = allData.getString("referralCode");

            locationSelectedData = location.toString();
            driverSelectedData = driverObject.toString();
            addressSelectedData = addressObject.toString();
            vehicalSelectedData = vehicleObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String locationSelectedData = "",
            driverSelectedData = "",addressSelectedData = "",
            vehicalSelectedData="",selectedRefferelCode="", isPremium = "";
}
package com.grabid.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.common.AlertManager;
import com.grabid.models.Category;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by graycell on 22/11/17.
 */

public class VehicleList extends Activity {
    View view;
    HashMap<Integer, String> vehicleTypeID = new HashMap<>();
    ArrayList<Category> suitableVehicleData;
    ArrayList<Category> arraylist;
    Category[] suitableVehicleData1;
    int index;
    String val = "", strvalue = "";
    String vehicleTypeIdStr = "";
    EditText searchET;
    ImageView mBack;
    TextView mTitle, mDone;
    RelativeLayout mLayout;
    int topVal = 2;


    public void clickme(View view) {
        switch (view.getId()) {
            case R.id.back:
                cancelresult();
                break;
            case R.id.done:
                done();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            e.toString();
        }
        setContentView(R.layout.vehiclelist);
        suitableVehicleData = new ArrayList<Category>();
        this.arraylist = new ArrayList<Category>();
        Bundle extras = getIntent().getExtras();
        try {
            if (extras.containsKey("topcolor"))
                topVal = 1;
            else
                topVal = 2;
        } catch (Exception e) {
            e.toString();
            topVal = 2;
        }

        if (extras.containsKey("array")) {
            suitableVehicleData.addAll((ArrayList<Category>) getIntent().getSerializableExtra("array"));
            if (suitableVehicleData != null)
                Log.v("", suitableVehicleData.toString());
            index = extras.getInt("index");
            if (extras.containsKey("vehicletypeid")) {
                vehicleTypeIdStr = extras.getString("vehicletypeid");
                String[] vehicles = vehicleTypeIdStr.trim().split("\\s*,\\s*");
                if (vehicleTypeIdStr != null && !vehicleTypeIdStr.contentEquals("")) {
                    for (int i = 0; i < suitableVehicleData.size(); i++) {
                        String id = suitableVehicleData.get(i).getId();
                        for (int j = 0; j < vehicles.length; j++) {
                            if ((vehicles[j]).equals(id)) {
                                suitableVehicleData.get(i).setSelected(true);
                            }
                        }

                    }

                }
            }
        }
        this.arraylist.addAll(suitableVehicleData);
        ListView ListView = (ListView) findViewById(R.id.list);
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mDone = (TextView) findViewById(R.id.done);
        mLayout = (RelativeLayout) findViewById(R.id.toolbar);
        final CategoryAdapter catAdapter = new CategoryAdapter(VehicleList.this);
        ListView.setAdapter(catAdapter);
        catAdapter.notifyDataSetChanged();
        searchET = (EditText) findViewById(R.id.search_et);
        setTopColor(topVal);
        // Capture Text in EditText
        searchET.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = searchET.getText().toString().toLowerCase(Locale.getDefault());
                catAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

    }

    public void setTopColor(int val) {
        if (val == 1) {
            mBack.setImageResource(R.drawable.back_white);
            mTitle.setTextColor(getResources().getColor(R.color.text_color_white));
            mDone.setTextColor(getResources().getColor(R.color.text_color_white));
            mLayout.setBackgroundColor(getResources().getColor(R.color.seagreen));
        } else {
            mBack.setImageResource(R.drawable.back_icon);
            mTitle.setTextColor(getResources().getColor(R.color.top_bar_title_color));
            mDone.setTextColor(getResources().getColor(R.color.blue));
            mLayout.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        }

    }

    public void sendData() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("value", val);
        returnIntent.putExtra("index", index);
        returnIntent.putExtra("strvalue", strvalue);
        returnIntent.putExtra("suitableidbuilder", suitableIdBuilder);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancelresult() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    String suitableIdBuilder = "";

    private class CategoryAdapter extends BaseAdapter {
        Context ctx;

        CategoryAdapter(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        public int getCount() {
            return suitableVehicleData.size();
        }

        @Override
        public Object getItem(int i) {
            return suitableVehicleData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_list_item_check, null);
            Category category = (Category) getItem(position);
            final TextView name = (TextView) convertView.findViewById(R.id.textItem);
            final CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
            TextView done = (TextView) convertView.findViewById(R.id.done);
            name.setTag(position);

            if (position == suitableVehicleData.size() - 1) {
                done.setVisibility(View.GONE);
            } else done.setVisibility(View.GONE);

            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (topVal == 2) {
                        if (check.isChecked())
                            check.setChecked(false);
                        else
                            check.setChecked(true);
                    } else {
                        if (check.isChecked())
                            check.setChecked(false);
                        else
                            check.setChecked(true);
                        Category country = new Category();
                        country = suitableVehicleData.get((int) name.getTag());
                        country.setSelected(check.isChecked());
                        suitableVehicleData.set((int) name.getTag(), country);
                    }
                }
                //notifyDataSetChanged();
            });

            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    int tag = (int) name.getTag();
                    if (topVal == 2) {
                        boolean val = true;
                        for (int i = 0; i < suitableVehicleData.size(); i++) {
                            if (i != position)
                                if (suitableVehicleData.get(i).isSelected()) {
                                    AlertManager.messageDialog(VehicleList.this, "Alert!", "Only one type of vehicle can be selected for each vehicle. ");
                                    val = false;
                                }
                        }
                        if (val) {
                            for (int i = 0; i < suitableVehicleData.size(); i++) {
                                if (i != position) {
                                    suitableVehicleData.get(i).setSelected(false);
                                } else
                                    suitableVehicleData.get(i).setSelected(isChecked);

                            }
                        }
                        notifyDataSetChanged();

                    } else {
                        Category country = new Category();
                        country = suitableVehicleData.get((int) name.getTag());
                        country.setSelected(isChecked);
                        suitableVehicleData.set((int) name.getTag(), country);
                    }
                }
            });
            done.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                }
            });
            name.setText(category.getName());
            check.setChecked(category.isSelected());
            return convertView;
        }


        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            suitableVehicleData.clear();
            if (charText.length() == 0) {
                suitableVehicleData.addAll(arraylist);
            } else {
                for (Category wp : arraylist) {
                    if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                        suitableVehicleData.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }

    }

    private void showMessage(String message) {
        AlertManager.messageDialog(VehicleList.this, "Alert!", message);
    }

    public void done() {
        String builder = "";
        try {
            suitableIdBuilder = "";
            for (int i = 0; i < suitableVehicleData.size(); i++) {
                if (suitableVehicleData.get(i).isSelected()) {
                    builder += suitableVehicleData.get(i).getName() + ", ";
                    suitableIdBuilder += suitableVehicleData.get(i).getId() + ",";
                    JSONObject object = new JSONObject();
                    object.put("name", suitableVehicleData.get(i).getName());
                    object.put("id", suitableVehicleData.get(i).getId());
                    //  suitableArray.put(object);
                }
            }
            if (!suitableIdBuilder.equals("")) {
                try {
                    val = suitableIdBuilder.substring(0, suitableIdBuilder.length() - 1);
                    vehicleTypeID.put(index, val);
                } catch (Exception e) {
                    e.toString();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (builder.equals("")) {
            showMessage("Please select suitable vehicle");
        } else {
            strvalue = builder.substring(0, builder.length() - 2);
            sendData();
                       /* viewType.setText(builder.substring(0, builder.length() - 2));
                        if (mDialog != null && mDialog.isShowing())
                            mDialog.dismiss();*/
        }
    }
}
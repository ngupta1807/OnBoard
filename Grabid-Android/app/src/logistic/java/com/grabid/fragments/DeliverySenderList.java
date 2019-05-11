package com.grabid.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
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
import android.widget.TextView;
import android.widget.TimePicker;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.adapters.ListAdapter;
import com.grabid.api.APIStatus;
import com.grabid.api.AsyncTaskCompleteListener;
import com.grabid.api.Config;
import com.grabid.api.HTTPMethods;
import com.grabid.api.Keys;
import com.grabid.api.RestAPICall;
import com.grabid.common.AlertManager;
import com.grabid.common.Internet;
import com.grabid.common.SessionManager;
import com.grabid.models.Category;
import com.grabid.models.Delivery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by vinod on 10/14/2016.
 */
public class DeliverySenderList extends Fragment implements View.OnClickListener,
        AsyncTaskCompleteListener {
    TextView pending, upComing, inTransit, completed, cancelled, search;
    ListView list;
    TextView noData;
    ListAdapter adapter;
    SessionManager session;
    LinearLayout drop_down;
    ImageView filter;
    TextView mReset;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    int page = 1;
    boolean IsSearched = false;
    int totalCount = 1;
    String url;
    boolean loadingMore = false;
    ArrayList<Category> statusArray;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        HomeActivity.edit.setVisibility(View.GONE);
        HomeActivity.track_delivery.setVisibility(View.GONE);
        HomeActivity.markread.setVisibility(View.GONE);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.title.setText(getResources().getString(R.string.pending_del));
        HomeActivity.filter.setVisibility(View.VISIBLE);
        HomeActivity.filter.setBackgroundResource(R.drawable.filter_white_btn);
        // HomeActivity.filter.setTextColor(getResources().getColor(top_bar_title_color));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_black));
        HomeActivity.filter.setVisibility(View.VISIBLE);
        HomeActivity.title.setText(getResources().getString(R.string.pending_del));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.filter.setBackgroundResource(R.drawable.filter_white_btn);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.filter.setTextColor(getResources().getColor(R.color.text_color_black));

        HomeActivity.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop_down.setVisibility(View.VISIBLE);
                low_opacity.setVisibility(View.VISIBLE);

            }
        });

        View view = inflater.inflate(R.layout.deliveries_upcoming, null);
        init(view);
        HomeActivity.title.setText(getResources().getString(R.string.pending_del));
        pending.setBackgroundResource(R.drawable.border_back);
        pending.setTextColor(Color.WHITE);
        // deliveryData.clear();
        if (page == 1)
            getDelivery(type);
        //updateUI(1);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
       /* HomeActivity.filter.setVisibility(View.VISIBLE);
        HomeActivity.title.setText(getResources().getString(R.string.pending_del));
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.filter.setBackgroundResource(R.drawable.filter_white_btn);
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_white);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.seagreen));
        HomeActivity.filter.setTextColor(getResources().getColor(R.color.text_color_black));
*/

    }

    @Override
    public void onPause() {
        super.onPause();
     /*   HomeActivity.filter.setVisibility(View.GONE);
        HomeActivity.title.setTextColor(getResources().getColor(R.color.text_color_white));
        HomeActivity.filter.setBackgroundResource(R.drawable.filter_btn);
        HomeActivity.mToolbar.setBackgroundColor(getResources().getColor(R.color.top_bar_color));
        HomeActivity.navDrawer.setBackgroundResource(R.drawable.menu_icon);
        HomeActivity.filter.setTextColor(getResources().getColor(R.color.text_color_white));
    */
    }


    TextView shipment_type, dshipment_type, status, pickup_date, pickup_time, drop_date, dropoff_time, shipment_id, booking_name;
    RelativeLayout low_opacity;

    private void init(View view) {
        session = new SessionManager(getActivity());
        statusArray = new ArrayList<Category>();
        for (int i = 0; i < getStatusList().length; i++) {
            Category cat = new Category();
            cat.setName(getStatusList()[i]);
            cat.setId(String.valueOf(i));
            cat.setSelected(false);
            statusArray.add(cat);
        }
        noData = (TextView) view.findViewById(R.id.no_data);
        pending = (TextView) view.findViewById(R.id.pending);
        pending.setOnClickListener(this);
        upComing = (TextView) view.findViewById(R.id.upcoming);
        upComing.setOnClickListener(this);
        drop_down = (LinearLayout) view.findViewById(R.id.drop_down);
        low_opacity = (RelativeLayout) view.findViewById(R.id.low_opacity);
        filter = (ImageView) view.findViewById(R.id.filter);
        filter.setOnClickListener(this);
        search = (TextView) view.findViewById(R.id.search);
        search.setOnClickListener(this);
        shipment_type = (TextView) view.findViewById(R.id.shipment_type);
        dshipment_type = (TextView) view.findViewById(R.id.dshipment_type);
        shipment_id = (TextView) view.findViewById(R.id.shipment_id);
        booking_name = (TextView) view.findViewById(R.id.booking_name);
        status = (TextView) view.findViewById(R.id.status);
        shipment_type.setOnClickListener(this);
        dshipment_type.setOnClickListener(this);
        mReset = (TextView) view.findViewById(R.id.resettxt);
        mReset.setOnClickListener(this);
        status.setOnClickListener(this);
        pickup_date = (TextView) view.findViewById(R.id.pickup_date);
        pickup_date.setOnClickListener(this);
        pickup_time = (TextView) view.findViewById(R.id.pickup_time);
        pickup_time.setOnClickListener(this);
        drop_date = (TextView) view.findViewById(R.id.drop_date);
        drop_date.setOnClickListener(this);
        dropoff_time = (TextView) view.findViewById(R.id.dropoff_time);
        dropoff_time.setOnClickListener(this);
        inTransit = (TextView) view.findViewById(R.id.intransit);
        inTransit.setOnClickListener(this);
        completed = (TextView) view.findViewById(R.id.completed);
        completed.setOnClickListener(this);
        cancelled = (TextView) view.findViewById(R.id.cancelled);
        cancelled.setOnClickListener(this);
        list = (ListView) view.findViewById(R.id.list);
        adapter = new ListAdapter(getActivity(), deliveryData, 1);
        list.setSmoothScrollbarEnabled(true);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (drop_down.getVisibility() == View.GONE) {
                    String backStateName = this.getClass().getName();
                    Bundle bundle = new Bundle();
                    HashMap<String, Delivery> data = new HashMap<String, Delivery>();
                    data.put("data", deliveryData.get(i));
                    bundle.putSerializable("data", data);
                    bundle.putSerializable("incoming_type", "shipper");
                    bundle.putSerializable("incoming_delivery_type", deliveryData.get(i).getDeliveryStatus());
                    Fragment fragment = new DeliveryInfo();
                    fragment.setArguments(bundle);
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, fragment, backStateName)
                            .addToBackStack(null)
                            //.commit();
                            .commitAllowingStateLoss();
                }
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItemm, int visibleItemCountt,
                                 int totalItemCountt) {
                firstVisibleItem = firstVisibleItemm;
                visibleItemCount = visibleItemCountt;
                totalItemCount = totalItemCountt;


            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if (firstVisibleItem > 0 && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
                    if (!loadingMore) {
                        if (totalCount >= page) {
                            loadingMore = true;
                            if (IsSearched)
                                getSubList(url);
                            else
                                getDelivery(type);
                        }

                    }
                    //  new AsyncTask().execute();

                    //get next 10-20 items(your choice)items

                }
            }
        });
    }

    public void resetFields() {
        drop_date.setText("");
        pickup_date.setText("");
        booking_name.setText("");
        Build = "";
        sBuild = "";
        dBuild = "";
        status.setText("");
        shipment_type.setText("");
        dshipment_type.setText("");
        shipment_id.setText("");
        pickup_time.setText("");
        dropoff_time.setText("");
        drop_date.setHint("Drop off Date");
        pickup_date.setHint("Pick up Date");
        pickup_time.setHint("Pick up Time");
        dropoff_time.setHint("Drop of Time");
        status.setHint("Status");
        booking_name.setHint("Booking Name");
        shipment_id.setHint("Shipment ID");
        shipment_type.setHint("PickUp Shipment Type");
        dshipment_type.setHint("DropOff Shipment Type");
        for (int i = 0; i < statusArray.size(); i++)
            statusArray.get(i).setSelected(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.resettxt: {
                resetFields();
                break;
            }
            case R.id.shipment_type:
                showGrabidDialog(1, 1);
                break;
            case R.id.dshipment_type:
                showGrabidDialog(1, 2);
                break;
            case R.id.pickup_date:
                showDatePicker(1);
                break;
            case R.id.pickup_time:
                Log.v("click", "click");
                showDatePicker(2);
                break;
            case R.id.drop_date:
                showDatePicker(3);
                break;
            case R.id.dropoff_time:
                showDatePicker(4);
                break;
            case R.id.status:
                // showGrabidDialog(2, 0);
                showStatusTyps();
                break;
            case R.id.filter:
                drop_down.setVisibility(View.GONE);
                low_opacity.setVisibility(View.GONE);
                break;
            case R.id.search:
                drop_down.setVisibility(View.GONE);
                low_opacity.setVisibility(View.GONE);
                searchDeliveries();
                break;
        }
    }

    private void searchDeliveries() {
        page = 1;
        IsSearched = true;
        url = Config.SERVER_URL + "deliveries/all-deliveries?user_type=1";
        if (!dropoff_time.getText().toString().equals("") || !drop_date.getText().toString().equals(""))
            url = url + "&drop_off_day=" + drop_date.getText().toString()+"T"+dropoff_time.getText().toString().replace(" ","");
        /*if (!drop_date.equals(""))
            url = url + "&drop_off_day=" + drop_date.getText().toString();*/
        if (!pickup_time.getText().toString().equals("") || !pickup_date.getText().toString().equals(""))
            url = url + "&pick_up_day=" + pickup_date.getText().toString()+"T"+pickup_time.getText().toString().replace(" ","");
       /* if (!pickup_date.equals(""))
            url = url + "&pick_up_day=" +  pickup_date.getText().toString();*/
        if (!booking_name.getText().toString().equals(""))
            url = url + "&item_delivery_title=" + booking_name.getText().toString();
        if (shipment_type.getText().toString().length() > 0)
            url = url + "&pick_up_building_type=" + Build;
        if (status.getText().toString().length() > 0) {
            if (!sBuild.contains("5")) {
                url = url + "&delivery_status=" + sBuild;
            } else {
                //  url = url + "&status=";
                url = url + "&delivery_status=0,1,2,3,4";
            }
        } else {
            url = url + "&delivery_status=";
        }
        if (!shipment_id.getText().toString().equals(""))
            url = url + "&id=" + shipment_id.getText().toString();
        if (dshipment_type.getText().toString().length() > 0)
            url = url + "&drop_off_building_type=" + dBuild;

        Log.d("EndPoint", url);
        getSubList(url);
       /* url = url + "&page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));*/
    }

    public void getSubList(String url) {
        url = url + "&page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    private void showDatePicker(int id) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault()); // Get current date

        // Create the DatePickerDialog instance
        if (id == 1) {
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                    datePickerListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            datePicker.setCancelable(false);
            datePicker.setTitle("Select the date");
            datePicker.show();
        }
        if (id == 2) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePicker = new TimePickerDialog(getActivity(), timePickerListener, hour, minute, false);
            timePicker.setCancelable(false);
            timePicker.setTitle("Select the time");
            timePicker.show();

        }
        if (id == 3) {
            DatePickerDialog datePicker = new DatePickerDialog(getActivity(),
                    dropOffdatePickerListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH));
            datePicker.setCancelable(false);
            datePicker.setTitle("Select the date");
            datePicker.show();
        }
        if (id == 4) {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePicker = new TimePickerDialog(getActivity(), droptimePickerListener, hour, minute, false);
            timePicker.setCancelable(false);
            timePicker.setTitle("Select the time");
            timePicker.show();

        }

    }

    // Listener
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            pickup_date.setText(formattedDate(selectedYear, selectedMonth + 1, selectedDay));
        }
    };

    // Listener
    private DatePickerDialog.OnDateSetListener dropOffdatePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            drop_date.setText(formattedDate(selectedYear, selectedMonth + 1, selectedDay));
        }
    };


    // Listener
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            String timeSet = "";
            if (selectedHour > 12) {
                selectedHour -= 12;
                timeSet = "PM";
            } else if (selectedHour == 0) {
                selectedHour += 12;
                timeSet = "AM";
            } else if (selectedHour == 12) {
                timeSet = "PM";
            } else {
                timeSet = "AM";
            }

            String min = "";
            if (selectedMinute < 10)
                min = "0" + selectedMinute;
            else
                min = String.valueOf(selectedMinute);

            // Append in a StringBuilder
            String aTime = new StringBuilder().append(selectedHour).append(':')
                    .append(min).append(" ").append(timeSet).toString();
            pickup_time.setText(aTime);
        }
    };

    // Listener
    private TimePickerDialog.OnTimeSetListener droptimePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
            String timeSet = "";
            if (selectedHour > 12) {
                selectedHour -= 12;
                timeSet = "PM";
            } else if (selectedHour == 0) {
                selectedHour += 12;
                timeSet = "AM";
            } else if (selectedHour == 12) {
                timeSet = "PM";
            } else {
                timeSet = "AM";
            }

            String min = "";
            if (selectedMinute < 10)
                min = "0" + selectedMinute;
            else
                min = String.valueOf(selectedMinute);

            // Append in a StringBuilder
            String aTime = new StringBuilder().append(selectedHour).append(':')
                    .append(min).append(" ").append(timeSet).toString();
            dropoff_time.setText(aTime);
        }
    };

    private String formattedDate(int year, int month, int day) {
        String date = String.format("%d-%02d-%02d", year, month, day);
        return date;
    }

    public void showGrabidDialog(final int type, final int sub_type) {
        final Dialog mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        if (type == 1)
            title.setText("Select Build Type");
        else if (type == 2)
            title.setText("Select Status");

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

        if (type == 1)
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getBuildList());
        else if (type == 2)
            adapter = new ArrayAdapter<String>(getActivity(),
                    R.layout.dialog_textview, R.id.textItem, getStatusList());

        dialog_ListView.setAdapter(adapter);
        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (type == 1) {
                    if (sub_type == 1)
                        shipment_type.setText(parent.getItemAtPosition(position).toString());
                    else
                        dshipment_type.setText(parent.getItemAtPosition(position).toString());
                } else if (type == 2) {
                    status.setText(parent.getItemAtPosition(position).toString());
                }
                setupValues(type, sub_type, parent.getItemAtPosition(position).toString());
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    Dialog mDialog;

    public void showStatusTyps() {
        mDialog = new Dialog(getActivity(), R.style.GrabidDialog);
        mDialog.setCancelable(false);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setContentView(R.layout.fragment_list_new);
        mDialog.setCanceledOnTouchOutside(false);
        TextView title = (TextView) mDialog.findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        ImageView close = (ImageView) mDialog.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done();
                /*if (type == 1 || type == 2) {
                    if (mDialog != null && mDialog.isShowing())
                        mDialog.dismiss();
                } else
                    done(cattype);*/
            }
        });

        title.setText("Select Status");

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
        CategoryAdapter catAdapter = new CategoryAdapter(getActivity());
        dialog_ListView.setAdapter(catAdapter);

        mDialog.show();
    }

    private class CategoryAdapter extends BaseAdapter {
        Context ctx;


        CategoryAdapter(Context ctx) {
            this.ctx = ctx;


        }

        @Override
        public int getCount() {
            return statusArray.size();
        }

        @Override
        public Object getItem(int i) {
            return statusArray.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.simple_list_item_check, null);
            Category category = (Category) getItem(position);
            final TextView name = (TextView) convertView.findViewById(R.id.textItem);
            final CheckBox check = (CheckBox) convertView.findViewById(R.id.check);
            final TextView done = (TextView) convertView.findViewById(R.id.done);
            name.setTag(position);


            if (position == statusArray.size() - 1) {
                done.setVisibility(View.VISIBLE);
            } else done.setVisibility(View.GONE);

            name.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (check.isChecked())
                        check.setChecked(false);
                    else
                        check.setChecked(true);
                    check.setChecked(true);

                    Category country = statusArray.get((int) name.getTag());
                    country.setSelected(check.isChecked());
                    statusArray.set((int) name.getTag(), country);
                    if ((int) name.getTag() == 5) {
                        for (int i = 0; i < statusArray.size(); i++)
                            if (!(i == (int) name.getTag()))
                                statusArray.get(i).setSelected(false);
                        notifyDataSetChanged();
                    } else {
                        statusArray.get(5).setSelected(false);
                        notifyDataSetChanged();
                    }

                    //notifyDataSetChanged();
                }
            });
            check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    Category country = statusArray.get((int) name.getTag());
                    country.setSelected(isChecked);
                    statusArray.set((int) name.getTag(), country);
                    if (statusArray.get((int) name.getTag()).isSelected()) {
                        if ((int) name.getTag() == 5) {
                            for (int i = 0; i < statusArray.size(); i++)
                                if (!(i == (int) name.getTag()))
                                    statusArray.get(i).setSelected(false);
                            notifyDataSetChanged();
                        } else {
                            statusArray.get(5).setSelected(false);
                            notifyDataSetChanged();
                        }

                    }

                }
            });
            done.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    done();
                }
            });
            name.setText(category.getName());
            check.setChecked(category.isSelected());
            return convertView;
        }
    }

    String suitableIdBuilder = "";

    public void done() {
        String builder = "";
        try {
            suitableIdBuilder = "";
            for (int i = 0; i < statusArray.size(); i++) {
                if (statusArray.get(i).isSelected()) {
                    builder += statusArray.get(i).getName() + ", ";
                    suitableIdBuilder += statusArray.get(i).getId() + ",";
                }
            }
            if (!builder.equals(""))
                status.setText(builder.substring(0, builder.length() - 2));
            if (!suitableIdBuilder.equals("")) {
                try {
                    sBuild = suitableIdBuilder.substring(0, suitableIdBuilder.length() - 1);
                } catch (Exception e) {
                    e.toString();
                }
            }

            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();
        } catch (Exception e) {
            e.toString();
        }
    }

    private void showMessage(String message) {
        AlertManager.messageDialog(getActivity(), "Alert!", message);
    }

    String Build, sBuild, dBuild;

    private void setupValues(int type, int sub_type, String s) {
        if (type == 1) {
            if (sub_type == 1) {
                if (s.equals("Commercial"))
                    Build = "1";
                else if (s.equals("Residential")) Build = "2";
            } else {
                if (s.equals("Commercial"))
                    dBuild = "1";
                else if (s.equals("Residential")) dBuild = "2";
            }
        } else {
            if (s.equals("Available")) sBuild = "0";
            else if (s.equals("Upcoming")) sBuild = "1";
            else if (s.equals("In-Transit")) sBuild = "2";
            else if (s.equals("Completed")) sBuild = "3";
            else if (s.equals("Cancelled")) sBuild = "4";
            else if (s.equals("All")) sBuild = "5";
        }
    }


    public String[] getBuildList() {
        return getActivity().getResources().getStringArray(R.array.build_type);
    }

    public String[] getStatusList() {
        return getActivity().getResources().getStringArray(R.array.shipper_status_type);
    }

    @Override
    public void onTaskComplete(String result) {
        Log.d(Config.TAG, result);
        drop_date.setText("");
        pickup_date.setText("");
        booking_name.setText("");
        Build = "";
        sBuild = "";
        dBuild = "";
        status.setText("");
        shipment_type.setText("");
        dshipment_type.setText("");
        shipment_id.setText("");
        pickup_time.setText("");
        dropoff_time.setText("");
      /*  for (int i = 0; i < statusArray.size(); i++)
            statusArray.get(i).setSelected(false);*/
        handleResponse(result);
    }

    private void handleResponse(String result) {
        try {
            JSONObject outJson = new JSONObject(result);
            if (outJson.getInt(Config.STATUS) == APIStatus.SUCCESS) {
                if (page == 1)
                    deliveryData.clear();
                ++page;
                loadingMore = false;
                JSONObject dataObj = outJson.getJSONObject(Config.DATA);
                JSONArray deliveryArray = dataObj.getJSONArray(Config.DELIVERY);
                for (int i = 0; i < deliveryArray.length(); i++) {
                    JSONObject deliveryObj = deliveryArray.getJSONObject(i);
                    Delivery delivery = new Delivery();
                    Log.v("data", deliveryObj.toString());
                    delivery.setPaymentAmount(deliveryObj.getString(Keys.PAYMENT_AMOUNT));
                    delivery.setBookmarked((deliveryObj.get(Config.BOOKMARK).toString().equals("null")) ? false : true);
                    delivery.setId(deliveryObj.getString(Keys.KEY_ID));
                    try {
                        delivery.setJob_ID(deliveryObj.getString(Keys.KEY_JOB_ID));
                    }catch (Exception ex){
                        delivery.setJob_ID("");
                    }
                    delivery.setLiftequipement(deliveryObj.getString(Keys.LIFT_EQUIPMENT));
                    delivery.setAnimalName(deliveryObj.getString(Keys.ITEM_ANIMAL_NAME));
                    delivery.setDliftequipement(deliveryObj.getString(Keys.DROP_EQUIPMENT));
                    delivery.setBreed(deliveryObj.getString(Keys.ITEM_ANIMAL_BREED));
                    delivery.setTitle(deliveryObj.getString(Keys.ITEM_DELIVERY_TITLE));
                    delivery.setBidID(deliveryObj.getString(Keys.BID_ID));
                    delivery.setBidStatus(deliveryObj.get(Keys.BID_STATUS).toString());
                    delivery.setDeliveryStatus(deliveryObj.getString(Keys.DELIVERY_STATUS));
                    delivery.setDropoffAdress(deliveryObj.getString(Keys.DROPOFF_ADDRESS));
                    delivery.setDriverID(deliveryObj.getString(Keys.DRIVER_ID));
                    delivery.setItemPhoto(deliveryObj.getString(Keys.ITEM_PHOTO));

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
                    delivery.setDropOffDate(deliveryObj.getString(Keys.DROPOFF_DATE));
                    delivery.setPickupCountry(deliveryObj.getString(Keys.PICKUP_COUNTRY));
                    delivery.setPickupState(deliveryObj.getString(Keys.PICKUP_STATE));
                    delivery.setPickupCity(deliveryObj.getString(Keys.PICKUP_CITY));
                    delivery.setDropoffCountry(deliveryObj.getString(Keys.DROPOFF_COUNTRY));
                    delivery.setDropoffState(deliveryObj.getString(Keys.DROPOFF_STATE));
                    delivery.setDropoffCity(deliveryObj.getString(Keys.DROPOFF_CITY));
                    delivery.setWidth(deliveryObj.getString(Keys.ITEM_WIDTH));
                    delivery.setStatus(deliveryObj.getString(Keys.STATUS));
                    delivery.setStackable(deliveryObj.getString(Keys.ITEM_STACKABLE));
                    delivery.setReceiver(deliveryObj.getString(Keys.RECEIVER_NAME));
                    delivery.setReceiverSign(deliveryObj.getString(Keys.RECEIVER_SIGN));
                    delivery.setSender(deliveryObj.getString(Keys.FROM_PICKUP_NAME));
                    delivery.setSenderSign(deliveryObj.getString(Keys.FROM_PICKUP_SIGN));
                    delivery.setQty(deliveryObj.getString(Keys.ITEM_QTY));
                    delivery.setWeight(deliveryObj.getString(Keys.ITEM_WEIGHT));
                    delivery.setPuMobile(deliveryObj.getString(Keys.PICKUP_MOBILE));
                    delivery.setPuContactPerson(deliveryObj.getString(Keys.PICKUP_CONTACT_PERSON));
                    delivery.setDoMobile(deliveryObj.getString(Keys.DROPOFF_MOBILE));
                    delivery.setPuLat(deliveryObj.getString(Keys.PICKUP_LATITUDE));  //plz chk
                    delivery.setPuLng(deliveryObj.getString(Keys.PICKUP_LONGITUDE));
                    delivery.setDoContactPerson(deliveryObj.getString(Keys.DROPOFF_CONTACT));
                    delivery.setDoLat(deliveryObj.getString(Keys.DROPOFF_LATITUDE)); //plz chk
                    delivery.setDoLng(deliveryObj.getString(Keys.DROPOFF_LONGITUDE));
                    delivery.setPuLiftEquipment(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIPMENT));
                    delivery.setPuBuildType(deliveryObj.getString(Keys.PICKUP_BUILD_TYPE));
                    delivery.setDoBuildType(deliveryObj.getString(Keys.DROPOFF_BUILD_TYPE));
                    delivery.setDoLiftEquipment(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIPMENT));
                    delivery.setFixedOffer(deliveryObj.getString(Keys.FIXED_OFFER));
                    delivery.setAuctionStart(deliveryObj.getString(Keys.AUCTION_START_TIME));
                    delivery.setAuctionEnd(deliveryObj.getString(Keys.AUCTION_END_TIME));
                    delivery.setMaxOpeningBid(deliveryObj.getString(Keys.MAX_AUCTION_BID));
                    delivery.setAuctionBid(deliveryObj.getString(Keys.AUCTION_BID));
                    delivery.setDoAppoint(deliveryObj.getString(Keys.DROPOFF_APPOINTMENT));
                    delivery.setMore(deliveryObj.getString(Keys.ITEM_MORE));
                    delivery.setUser_Group(deliveryObj.getString(Keys.USER_GROUP));
                    delivery.setDoCall(deliveryObj.getString(Keys.DROPOFF_CALL));
                    try {
                        delivery.setRelistNotification(deliveryObj.getString(Keys.RELIST_NOTIFICATION));
                        delivery.setItem_delivery_other(deliveryObj.getString(Keys.ITEM_DELIVAR_OTHER));
                        delivery.setPickUpComName(deliveryObj.getString(Keys.PICKUP_BUILD_COMPANYNAME));
                        delivery.setDropOffComName(deliveryObj.getString(Keys.DROPOFF_BUILD_COMPANYNAME));
                    } catch (Exception e) {
                        e.toString();
                    }
                    if (!deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID).equals("null")) {
                        delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_ID));
                        delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_EQUIPMENT_TYPE_NAME));
                    } else {
                        delivery.setDeliveryTypeSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_ID));
                        delivery.setDeliverySubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_NAME));
                    }
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
                    delivery.setDeliveryTypeID(deliveryObj.getString(Keys.DELIVERY_TYPE_ID));
                    delivery.setDeliveryTypeSubSubID(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_ID));
                    delivery.setDeliveryTypeSubSubName(deliveryObj.getString(Keys.ITEM_DELIVERY_TYPE_SUB_SUB_NAME));
                    delivery.setCurrentVaccination(deliveryObj.getString(Keys.ITEM_CURRENT_VACCINATIONS));
                    delivery.setAnimalCarrier(deliveryObj.getString(Keys.ITEM_ANIMAL_CARRIER));
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
                    delivery.setItemsData(deliveryObj.get(Keys.DELIVERY_ITEMS).toString());
                    delivery.setSuitableVehicles(deliveryObj.get(Keys.SUITABLE_VEHICLE_TEXT).toString());
                    delivery.setDropoffDateType(deliveryObj.getString(Keys.DROPOFF_DAY_TYPE));
                    delivery.setPickupDateType(deliveryObj.getString(Keys.PICKUP_DAY_TYPE));
                    delivery.setDropOffEndDate(deliveryObj.getString(Keys.DROPOFF_END_DAY));
                    delivery.setPickupEndDate(deliveryObj.getString(Keys.PICKUP_END_DAY));
                    delivery.setPickupinductionRequire(deliveryObj.getString(Keys.PICKUP_INDUCTION_REQUIRE));
                    delivery.setPickupSpecialRestriction(deliveryObj.getString(Keys.PICKUP_SPECIAL_RESTRICTION));
                    delivery.setDropoffinductionRequire(deliveryObj.getString(Keys.DROPOFF_INDUCTION_REQUIRE));
                    delivery.setDropoffSpecialRestriction(deliveryObj.getString(Keys.DROPOFF_SPECIAL_RESTRICTION));
                    delivery.setSpecialPermit(deliveryObj.getString(Keys.SPECIAL_PERMIT));
                    delivery.setSpecialPermitDetail(deliveryObj.getString(Keys.SPECIAL_PERMIT_DETAIL));
                    delivery.setSuitabelVehicle(deliveryObj.getString(Keys.SUITABLE_VEHICAL_IDS));
                    delivery.setPickUpLiftEquiAvailableIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_IDS));
                    delivery.setDropOffLiftEquiAvailableIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_IDS));
                    delivery.setPickUpLiftEquiNeededIds(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_IDS));
                    delivery.setDropOffLiftEquiNeededIds(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_IDS));
                    try {
                        delivery.setPuLiftEquipmentText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_AVAILABLE_TEXT));
                        delivery.setPuLiftEquipmentNeededText(deliveryObj.getString(Keys.PICKUP_LIFT_EQUIP_NEEDED_TEXT));
                        delivery.setDoLiftEquipmentText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_AVAILABLE_TEXT));
                        delivery.setDoLiftEquipmentNeededText(deliveryObj.getString(Keys.DROPOFF_LIFT_EQUIP_NEEDED_TEXT));

                    } catch (Exception e) {
                        e.toString();

                    }
                    delivery.setFromPickUpAt(deliveryObj.getString("from_pick_up_at"));
                    delivery.setCompleted_at(deliveryObj.getString("completed_at"));
                    try {
                        delivery.setPickUpBarcode(deliveryObj.getString(Keys.PICK_UPBARCODE));
                        delivery.setDropOffBarCode(deliveryObj.getString(Keys.DROP_OFFBARCODE));
                    } catch (Exception e) {
                        e.toString();
                    }
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
                adapter.notifyDataSetChanged();
                if (deliveryData.size() == 0) {
                    list.setVisibility(View.GONE);
                    noData.setVisibility(View.VISIBLE);
                } else {
                    noData.setVisibility(View.GONE);
                    list.setVisibility(View.VISIBLE);
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
            } else {
                //AlertManager.messageDialog(getActivity(), "Alert!", outJson.getString(Config.MESSAGE));
                list.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            list.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
    }

    private void getDelivery(int type) {
        this.type = type;
        String url = Config.SERVER_URL;
        url = url + "deliveries/all-deliveries?user_type=1&delivery_status=" + "&page=" + page;
        if (Internet.hasInternet(getActivity())) {
            RestAPICall mobileAPI = new RestAPICall(getActivity(), HTTPMethods.GET, this, null);
            mobileAPI.execute(url, session.getToken());
        } else
            AlertManager.messageDialog(getActivity(), "Alert!", getResources().getString(R.string.no_internet));
    }

    int type;
    ArrayList<Delivery> deliveryData = new ArrayList<>();
}
package com.bookmyride.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.adapters.PrebookingAdapter;
import com.bookmyride.common.SessionHandler;
import com.bookmyride.db.DatabaseHandler;
import com.bookmyride.models.Ride;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinod on 10/10/2017.
 */

public class FilteredPreBookings extends AppCompatActivity {
    SessionHandler session;
    TextView title, noData;
    ListView mList;
    ImageView previousDay;
    ImageView nextDay;
    TextView currentDate;
    Calendar cal = Calendar.getInstance();
    DatabaseHandler db;
    PrebookingAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filtered_rides);
        init();
    }

    public void onBack(View view) {
        onBackPressed();
    }

    private void init() {
        session = new SessionHandler(this);
        db = new DatabaseHandler(this);
        title = (TextView) findViewById(R.id.title);
        noData = (TextView) findViewById(R.id.no_data);
        mList = (ListView) findViewById(R.id.list_view);
        Date mDate = getDay(getIntent().getStringExtra("selected_date"));
        cal.setTime(mDate);
        int calDay = cal.get(Calendar.DAY_OF_MONTH);
        int calMonth = cal.get(Calendar.MONTH) + 1;
        int calYear = cal.get(Calendar.YEAR);

        currentDate = (TextView) findViewById(R.id.display_current_date);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
        previousDay = (ImageView) findViewById(R.id.previous_day);
        nextDay = (ImageView) findViewById(R.id.next_day);
        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousCalendarDate();
            }
        });
        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextCalendarDate();
            }
        });
    }

    private String displayDateInString(Date mDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH);
        return formatter.format(mDate).toUpperCase();
    }

    private void previousCalendarDate() {
        cal.add(Calendar.DAY_OF_MONTH, -1);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
    }

    private void nextCalendarDate() {
        cal.add(Calendar.DAY_OF_MONTH, 1);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
    }

    private void displayDailyEvents() {
        Date calendarDate = cal.getTime();
        List<Ride> dailyEvent = db.getAllRides(calendarDate);
        if (dailyEvent.size() > 0) {
            noData.setVisibility(View.GONE);
            mList.setVisibility(View.VISIBLE);
        } else {
            mList.setVisibility(View.GONE);
            noData.setVisibility(View.VISIBLE);
        }
        adapter = new PrebookingAdapter(getApplicationContext(), dailyEvent);
        mList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        /*for (Ride eObject : dailyEvent) {
            //Ride eObject = dailyEvent.get(1);
            Date eventDate = getDate(eObject.getPuDate());
            Date endDate = getDate(eObject.getDoDate());
            String eventMessage = "";
            try {
                JSONObject jobj = new JSONObject(eObject.getPuInfo());
                eventMessage = "Pick Up: " + jobj.getString("address") + "\n";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                JSONObject jobj = new JSONObject(eObject.getDoInfo());
                eventMessage = eventMessage + "Drop Off: " + jobj.getString("address");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    private Date getDate(String dtStart) {
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = format.parse(dtStart);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return date;
    }

    private Date getDay(String dtStart) {
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = format.parse(dtStart);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return date;
    }
}

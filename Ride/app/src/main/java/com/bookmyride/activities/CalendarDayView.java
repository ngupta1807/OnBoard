package com.bookmyride.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bookmyride.R;
import com.bookmyride.db.DatabaseHandler;
import com.bookmyride.models.Ride;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vinod on 6/6/2017.
 */
public class CalendarDayView extends AppCompatActivity {

    private static final String TAG = CalendarDayView.class.getSimpleName();
    private ImageView previousDay;
    private ImageView nextDay;
    private TextView currentDate;
    private Calendar cal = Calendar.getInstance();
    private DatabaseHandler db;
    private RelativeLayout mLayout;
    private int eventIndex;

    public void onBack(View view) {
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);
        init();
    }

    private void init() {
        db = new DatabaseHandler(this);
        Date mDate = getDay(getIntent().getStringExtra("selected_date"));
        cal.setTime(mDate);
        int calDay = cal.get(Calendar.DAY_OF_MONTH);
        int calMonth = cal.get(Calendar.MONTH) + 1;
        int calYear = cal.get(Calendar.YEAR);

        mLayout = (RelativeLayout) findViewById(R.id.left_event_column);
        eventIndex = mLayout.getChildCount();
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
        return formatter.format(mDate);
    }

    private void previousCalendarDate() {
        //mLayout.removeViewAt(eventIndex - 1);
        try {
            mLayout.removeViewAt(eventIndex - 1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_MONTH, -1);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
    }

    private void nextCalendarDate() {
        try {
            mLayout.removeViewAt(eventIndex - 1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DAY_OF_MONTH, 1);
        currentDate.setText(displayDateInString(cal.getTime()));
        displayDailyEvents();
    }

    private void displayDailyEvents() {
        Date calendarDate = cal.getTime();
        //List<EventObjects> dailyEvent = mQuery.getAllFutureEvents(calendarDate);
        List<Ride> dailyEvent = db.getAllRides(calendarDate);
        for (Ride eObject : dailyEvent) {
            //    Ride eObject = dailyEvent.get(1);
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
            int eventBlockHeight = getEventTimeFrame(eventDate, endDate);
            displayEventSection(eventDate, eventBlockHeight, eventMessage);
        }
    }

    private int getEventTimeFrame(Date start, Date end) {
        long timeDifference;
        if (end != null)
            timeDifference = end.getTime() - start.getTime();
        else {
            timeDifference = start.getTime() - start.getTime();
        }
        Calendar mCal = Calendar.getInstance();
        mCal.setTimeInMillis(timeDifference);
        int hours = mCal.get(Calendar.HOUR);
        int minutes = mCal.get(Calendar.MINUTE);
        return (hours * 60) + ((minutes * 60) / 100);
    }

    private void displayEventSection(Date eventDate, int height, String message) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        String displayValue = timeFormatter.format(eventDate);
        String[] hourMinutes = displayValue.split(":");
        int hours = Integer.parseInt(hourMinutes[0]);
        int minutes = Integer.parseInt(hourMinutes[1]);
        int topViewMargin = (hours * 60) + ((minutes * 60) / 100);
        createEventView(topViewMargin, height, message);
    }

    private void createEventView(int topMargin, int height, String message) {
        TextView mEventView = new TextView(CalendarDayView.this);
        RelativeLayout.LayoutParams lParam = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lParam.topMargin = topMargin * 2;
        lParam.leftMargin = 24;
        mEventView.setLayoutParams(lParam);
        mEventView.setPadding(24, 0, 24, 0);
        mEventView.setHeight(height * 2);
        mEventView.setGravity(0x11);
        mEventView.setTextColor(Color.parseColor("#FFFFFF"));
        mEventView.setText(message);
        mEventView.setBackgroundColor(Color.parseColor("#3F51B5"));
        mLayout.addView(mEventView, eventIndex - 1);
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

package com.bookmyride.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bookmyride.R;
import com.bookmyride.db.DatabaseHandler;

/**
 * Created by vinod on 10/9/2017.
 */

public class RidesOnCalendar extends AppCompatActivity {
    CalendarMonthView monthView;
    DatabaseHandler db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride_on_calendar);
        init();
    }

    private void init() {
        db = new DatabaseHandler(this);
        monthView = (CalendarMonthView) findViewById(R.id.month_view);
    }

    public void onBack(View view) {
        onBackPressed();
    }
}

package com.bookmyride.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.models.Ride;
import com.bookmyride.views.CircularTextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GridAdapter extends ArrayAdapter {

    private static final String TAG = GridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    private List<Ride> allEvents;

    public GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate,
                       List<Ride> allEvents) {
        super(context, R.layout.single_cell_layout);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
    }

    public class ViewHolder {
        private CircularTextView calendar_date_id;
        private TextView eventIndicator;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Date mDate = monthlyDates.get(position);
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        ViewHolder holder;
        if (convertView != null) {
            holder = (ViewHolder) convertView.getTag();
        } else {
            convertView = mInflater.inflate(R.layout.single_cell_layout, parent, false);
            holder = new ViewHolder();
            holder.calendar_date_id = (CircularTextView) convertView.findViewById(R.id.calendar_date_id);
            holder.eventIndicator = (TextView) convertView.findViewById(R.id.event_id);
            convertView.setTag(holder);
        }
        holder.calendar_date_id.setStrokeWidth(1);
        if (displayMonth == currentMonth && displayYear == currentYear) {
            holder.calendar_date_id.setSolidColor("#FB7F04");
            holder.calendar_date_id.setStrokeColor("#cccccc");
            //holder.calendar_date_id.setBackgroundColor(Color.parseColor("#FF5733"));
        } else {
            holder.calendar_date_id.setSolidColor("#cccccc");
            holder.calendar_date_id.setStrokeColor("#ffffff");
            //holder.calendar_date_id .setBackgroundColor(Color.parseColor("#cccccc"));
        }
        holder.calendar_date_id.setText(String.valueOf(dayValue));
        Calendar eventCalendar = Calendar.getInstance();  //Add events to the calendar
        for (int i = 0; i < allEvents.size(); i++) {
            eventCalendar.setTime(convertStringToDate(allEvents.get(i).getPuDate()));
            int month = eventCalendar.get(Calendar.MONTH) + 1;
            if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH)
                    && displayMonth == month && displayYear == eventCalendar.get(Calendar.YEAR)
                    && displayMonth == currentMonth) {
                //holder.calendar_date_id .setBackgroundColor(Color.RED);
                holder.calendar_date_id.setSolidColor("#0000ff");
                holder.calendar_date_id.setStrokeColor("#cccccc");
                holder.calendar_date_id.setTextColor(Color.parseColor("#ffffff"));
                holder.eventIndicator.setText(dayValue + "-" + displayMonth + "-" + displayYear);
            } /*else {
                    holder.eventIndicator.setText("");
                }*/
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return monthlyDates.size();
    }

    @Override
    public Object getItem(int position) {
        return monthlyDates.get(position);
    }

    @Override
    public int getPosition(Object item) {
        return monthlyDates.indexOf(item);
    }

    private Date convertStringToDate(String dateInString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
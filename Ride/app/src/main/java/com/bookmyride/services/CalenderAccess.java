package com.bookmyride.services;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import com.bookmyride.models.EventObjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by nisha on 6/1/2017.
 */

public class CalenderAccess {
    public static void deleteEvent(long eventID, Context mcon) {
        Uri eventUri = ContentUris
                .withAppendedId(getCalendarUriBase(), eventID);
        mcon.getContentResolver().delete(eventUri, null, null);
        Log.v("delete", "Row deleted:" + mcon.getContentResolver().delete(eventUri, null, null));
    }

    //** Adds Events and Reminders in Calendar. *//*
    public static void addReminderInCalendar(String pudate, String putitle, String pudesc, Context mcon) {

        String datetime[] = pudate.split(" ");
        String date[] = datetime[0].split("-");
        String time[] = datetime[1].split(":");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
        cal.set(Calendar.YEAR, Integer.parseInt(date[0]));
        cal.set(Calendar.DATE, Integer.parseInt(date[2]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(time[1]));
        //cal.set(Calendar.SECOND, Integer.parseInt(time[2]));
        TimeZone timeZone = TimeZone.getDefault();
        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        //** Inserting an event in calendar. *//*
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, putitle);
        values.put(CalendarContract.Events.DESCRIPTION, pudesc);
        values.put(CalendarContract.Events.ALL_DAY, 0);
        // event starts at 11 minutes from now
        values.put(CalendarContract.Events.DTSTART, cal.getTimeInMillis());
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, cal.getTimeInMillis() + 2 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        values.put(CalendarContract.Events.HAS_ALARM, 1);

        Uri event = mcon.getContentResolver().insert(EVENTS_URI, values);
        //** Adding reminder for event added. *//*
        Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
        values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));

        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, 10);
        mcon.getContentResolver().insert(REMINDERS_URI, values);
    }
    public static List<EventObjects> addEventsOnCalendar(Context context) {
        List<EventObjects> events = new ArrayList<>();
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "calendar_id"}, null,
                        null, null);
        cursor.moveToFirst();
        String CNames[] = new String[cursor.getCount()];
        for (int i = 0; i < CNames.length; i++) {
            try {
                if (cursor.getString(2).startsWith("Ride : ")) {
                    events.add(new EventObjects(Integer.parseInt(cursor.getString(0)), cursor.getString(1),getDate(Long.parseLong(cursor.getString(3)))));
                }
            } catch (Exception ex) {
            }
            cursor.moveToNext();
        }
        return events;
    }
    public static Date getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String datestring = formatter.format(calendar.getTime());
        SimpleDateFormat outformat= new SimpleDateFormat(
                "yyyy-MM-dd hh:mm");
        Date date = null;
        try {
            date = outformat.parse(datestring);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    public static void readCalendarEvent(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "calendar_id"}, null,
                        null, null);
        cursor.moveToFirst();
        String CNames[] = new String[cursor.getCount()];
        for (int i = 0; i < CNames.length; i++) {
            Log.d("description:.", "description:." + cursor.getString(2));
            try {
                if (cursor.getString(2).startsWith("Ride : ")) {
                    deleteEvent(Long.parseLong(cursor.getString(0)), context);
                }
            } catch (Exception ex) {
            }
            cursor.moveToNext();
        }
    }

    public static Cursor deletePastCalendarEvent(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"_id", "title", "description",
                                "dtstart", "dtend", "eventLocation", "calendar_id"}, null,
                        null, null);
        cursor.moveToFirst();
        String CNames[] = new String[cursor.getCount()];
        for (int i = 0; i < CNames.length; i++) {
            try {
                if (cursor.getString(2).startsWith("Ride : ")) {
                    if(Long.parseLong(cursor.getString(3))<getcurrentDateTime()) {
                        Log.d("Match", "Match");
                        deleteEvent(Long.parseLong(cursor.getString(0)), context);
                    }
                }
            } catch (Exception ex) {
            }
            cursor.moveToNext();
        }
        return cursor;
    }

    public static long getcurrentDateTime() {
        Calendar c = Calendar.getInstance();
        return c.getTimeInMillis();
    }

    public static Uri getCalendarUriBase() {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = Uri.parse("content://calendar/events");
            } else {
                calendarURI = Uri.parse("content://com.android.calendar/events");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI;
    }

    public static String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return calendarURI.toString();
    }
}

package com.bookmyride.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.bookmyride.models.Ride;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by vinod on 6/6/2017.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "events.db";
    private static final int DATABASE_VERSION = 1;
    // Contacts table name
    private static final String TABLE_BOOKING = "booking";

    // Contacts Table Columns names
    private static final String ID = "id";
    private static final String BOOKING_ID = "ride_id";
    private static final String NAME = "passenger";
    private static final String PHONE = "passenger_phone";
    private static final String MESSAGE = "message";
    private static final String PICKUP = "pickup";
    private static final String DROP_OFF = "dropOff";
    private static final String PICKUP_DATE = "pickup_date";
    private static final String DROPOFF_DATE = "drop_off_date";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_BOOKING + "("
                + ID + " INTEGER PRIMARY KEY, "
                + BOOKING_ID + " TEXT, "
                + NAME + " TEXT, "
                + PHONE + " TEXT, "
                + MESSAGE + " TEXT, "
                + PICKUP_DATE + " TEXT, "
                + DROP_OFF + " TEXT, "
                + DROPOFF_DATE + " TEXT, "
                + PICKUP + " TEXT " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKING);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    public void addEvent(Ride ride) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BOOKING_ID, ride.getId());
        values.put(NAME, ride.getpName());
        values.put(PHONE, ride.getpPhone());
        values.put(MESSAGE, ride.getMessage());
        values.put(PICKUP_DATE, ride.getPuDate());
        values.put(DROPOFF_DATE, ride.getDoDate());
        values.put(DROP_OFF, ride.getDoInfo());
        values.put(PICKUP, ride.getPuInfo());

        // Inserting Row
        db.insert(TABLE_BOOKING, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Ride
    public Ride getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKING, new String[]{ID,
                        NAME, PHONE}, ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Ride contact = new Ride();
        // return contact
        return contact;
    }

    // Getting All Rides
    public List<Ride> getAllRides(Date mDate) {
        Calendar calDate = Calendar.getInstance();
        Calendar dDate = Calendar.getInstance();
        calDate.setTime(mDate);
        int calDay = calDate.get(Calendar.DAY_OF_MONTH);
        int calMonth = calDate.get(Calendar.MONTH) + 1;
        int calYear = calDate.get(Calendar.YEAR);

        List<Ride> rideList = new ArrayList<Ride>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BOOKING;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Ride ride = new Ride();
                ride.setId(cursor.getString(cursor.getColumnIndex(BOOKING_ID)));
                ride.setpName(cursor.getString(cursor.getColumnIndex(NAME)));
                ride.setpPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                ride.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                ride.setPuInfo(cursor.getString(cursor.getColumnIndex(PICKUP)));
                ride.setDoInfo(cursor.getString(cursor.getColumnIndex(DROP_OFF)));
                ride.setPuDate(cursor.getString(cursor.getColumnIndex(PICKUP_DATE)));
                ride.setDoDate(cursor.getString(cursor.getColumnIndex(DROPOFF_DATE)));
                // Adding contact to list
                Date reminderDate = convertStringToDate(cursor.getString(cursor.getColumnIndex(PICKUP_DATE)));
                //Date end = convertStringToDate(endDate);
                dDate.setTime(reminderDate);
                int dDay = dDate.get(Calendar.DAY_OF_MONTH);
                int dMonth = dDate.get(Calendar.MONTH) + 1;
                int dYear = dDate.get(Calendar.YEAR);
                if (calDay == dDay && calMonth == dMonth && calYear == dYear) {
                    rideList.add(ride);
                }
            } while (cursor.moveToNext());
        }

        // return contact list
        return rideList;
    }

    // Getting All Rides
    public List<Ride> getAllRides() {
        List<Ride> rideList = new ArrayList<Ride>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BOOKING;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Ride ride = new Ride();
                ride.setId(cursor.getString(cursor.getColumnIndex(BOOKING_ID)));
                ride.setpName(cursor.getString(cursor.getColumnIndex(NAME)));
                ride.setpPhone(cursor.getString(cursor.getColumnIndex(PHONE)));
                ride.setMessage(cursor.getString(cursor.getColumnIndex(MESSAGE)));
                ride.setPuInfo(cursor.getString(cursor.getColumnIndex(PICKUP)));
                ride.setDoInfo(cursor.getString(cursor.getColumnIndex(DROP_OFF)));
                ride.setPuDate(cursor.getString(cursor.getColumnIndex(PICKUP_DATE)));
                ride.setDoDate(cursor.getString(cursor.getColumnIndex(DROPOFF_DATE)));
                // Adding contact to list
                rideList.add(ride);
            } while (cursor.moveToNext());
        }
        // return contact list
        return rideList;
    }

    // Deleting single contact
    public void deleteAllRides() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKING, null, null);
        db.close();
    }

    private Date convertStringToDate(String dateInString) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = null;
        try {
            date = format.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}

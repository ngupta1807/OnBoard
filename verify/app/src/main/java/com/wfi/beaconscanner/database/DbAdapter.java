package com.wfi.beaconscanner.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created : Nisha Developed by : GraycellTechnologies
 * Used :Simple database access helper class. Defines the basic CRUD operations
 * (Create, Read, Update, Delete).
 */
public class DbAdapter {
    // Databsae Related Constants
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE_LOGS = "logs";
    private static final String DATABASE_TABLE = "becons";
    private static final int DATABASE_VERSION = 3;

    public static final String KEY_Battery = "batteryMilliVolts";
    public static final String KEY_Distance = "Distance";
    public static final String KEY_Status = "status";
    public static final String KEY_SERVER_Status = "server_status";
    public static final String KEY_DateTime = "lastSeen";
    public static final String KEY_MinDateTime = "lastminSeen";
    public static final String KEY_InstanceId = "instanceId";
    public static final String KEY_SavedTime = "savedTime";
    public static final String KEY_MODE = "mode";
    public static final String KEY_Instance = "instance";
    public static final String KEY_Beacon = "beacon_name";

    public static final String KEY_ROWID = "_id";
    private static final String TAG = "LogsApp";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    int limit = 100;

    /*
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE_LOGS = "create table "
            + DATABASE_TABLE_LOGS + " (" + KEY_ROWID
            + " integer primary key autoincrement, "
            + KEY_Battery + " text , "
            + KEY_Distance + " text not null,"
            + KEY_Status + " text ,"
            + KEY_MODE + " text ,"
            + KEY_DateTime + " text ,"
            + KEY_MinDateTime + " text ,"
            + KEY_InstanceId + " text ,"
            + KEY_SavedTime + " text ,"
            + KEY_SERVER_Status + " text )";


    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + " ("
            + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_Beacon + " text ,"
            + KEY_Instance + " text );";

    private final Context mCtx;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            /* super(context, Environment.getExternalStorageDirectory()
                    + File.separator+ DATABASE_NAME, null, DATABASE_VERSION);*/
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE_LOGS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String db1 = "DROP TABLE IF EXISTS " + DATABASE_CREATE;
            String db2 = "DROP TABLE IF EXISTS " + DATABASE_TABLE_LOGS;
            db.execSQL(db1);
            db.execSQL(db2);
            onCreate(db);
        }
    }

    /*
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx
     *            the Context within which to work
     */
    public DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /*
     * Open the database. If it cannot be opened, try to create a new instance
     * of the database. If it cannot be created, throw an exception to signal
     * the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened or created
     */
    public DbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    /*
     * Close the database.
     */
    public void close() {
        mDbHelper.close();
    }

    /*
     * Insert the log data in database.
     */
    public long createLogs(String dis, String status, String server_status, String mode, String datetime, String instanceid, String battery, String lstminseen,String savedTime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_Battery, battery);
        initialValues.put(KEY_Distance, dis);
        initialValues.put(KEY_MinDateTime, lstminseen);
        initialValues.put(KEY_SavedTime, savedTime);
        initialValues.put(KEY_InstanceId, instanceid);
        initialValues.put(KEY_MODE, mode);
        initialValues.put(KEY_DateTime, datetime);
        initialValues.put(KEY_Status, status);
        initialValues.put(KEY_SERVER_Status, "offline");


        return mDb.insert(DATABASE_TABLE_LOGS, null, initialValues);
    }

    /*
     * Insert the beacon data in database.
     */
    public long createBeacons(String beacon_name,
                              String instance) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_Instance, instance);
        initialValues.put(KEY_Beacon, beacon_name.replace("\"", ""));
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

        /*
         * fetch the log  in database.
         */
    public void updateBeaconName(String instance_id,String beacon_name) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_Beacon,beacon_name.replace("\"", ""));
        mDb.update(DATABASE_TABLE, cv, KEY_Instance+"=\""+instance_id+ "\"", null);
    }
    /*
     * fetch the log  in database.
     */
    public Cursor fetchBeaconName(String instance_id) {
        String query = "SELECT * FROM " + DATABASE_TABLE
                + " where " + KEY_Instance + "='" + instance_id + "'";
        Cursor mCursor = mDb.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /*
     * fetch the log  in database.
     */
    public Cursor fetchInstanceId(String beaconname) {
        String query = "SELECT * FROM " + DATABASE_TABLE
                + " where " + KEY_Beacon + "='" + beaconname + "'";
        Cursor mCursor = mDb.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /*
     * fetch the log data in database.
     */
    public Cursor fetchLimitedOfflineLogs() {
        String query = "SELECT * FROM " + DATABASE_TABLE_LOGS + " order by _id ASC limit " + limit;
        Cursor mCursor = mDb.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }


    /*
     * delete the log data in database.
     */
    public Cursor deleteLogsByLimit(String rowid) {
        String query = "delete from " + DATABASE_TABLE_LOGS
                + " WHERE _id IN ( SELECT " + KEY_ROWID + " FROM " + DATABASE_TABLE_LOGS + " where rowid <= '" + rowid + "');";
        Cursor mCursor = mDb.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /*
   * delete all log data in database.
   */
    public Cursor deleteAllLogsByLimit() {
        String query = "delete from " + DATABASE_TABLE_LOGS+";";
        Cursor mCursor = mDb.rawQuery(query, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}

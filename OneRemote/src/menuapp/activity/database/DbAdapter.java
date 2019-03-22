package menuapp.activity.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Simple reminder database access helper class. Defines the basic CRUD
 * operations (Create, Read, Update, Delete) for the example, and gives the
 * ability to list all reminders as well as retrieve or modify a specific
 * reminder.
 * 
 */
public class DbAdapter {

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 4;

	private static final String DATABASE_CURRENT_VER_TABLE = "current_ver";

	private static final String DATABASE_TABLE = "category";
	private static final String DATABASE_ACTION_TABLE = "action";
	private static final String DATABASE_SUB_CAT_ITEM_TABLE = "sub_category_item";
	private static final String DATABASE_LINK_TABLE = "link";
	private static final String DATABASE_SWITCH_TABLE = "switch";

	public static final String KEY_CURRENT_VER = "current_version";
	public static final String KEY_CURRENT_VER_ROWID = "version_rowid";
	
	public static final String KEY_ROWID = "cat_id";
	public static final String KEY_BODY = "cat_name";
	public static final String CAT_DESC = "cat_desc";
	public static final String CAT_IMAGE_PATH = "image_path";
	public static final String NEST_ID = "nest_id";
	public static final String CAT_PATH = "path";
	public static final String MAX_LEN = "max_len";
	public static final String KEY_CAT_POS = "pos";

	public static final String KEY_SUB_CAT_ITEM_ROWID = "sub_cat_item_id";
	public static final String KEY_CAT_ID = "cat_id";
	public static final String KEY_PATH = "device_path";
	public static final String KEY_SUB_CAT_ITEM_BODY = "sub_cat_item_name";
	public static final String KEY_PIC = "pic";
	public static final String KEY_LINK = "link";
	public static final String KEY_SUB_CAT_POS = "pos";

	public static final String KEY_ACTION_ROWID = "action_id";
	public static final String KEY_ACTION_CAT_ID = "cat_id";
	public static final String KEY_ACTION_PATH = "action_path";
	public static final String KEY_ACTION_BODY = "action_body";
	public static final String KEY_ACTION_PIC = "pic";
	public static final String KEY_ACTION_POS = "pos";

	public static final String KEY_LINK_ROWID = "link_id";
	// public static final String KEY_CAT_ID = "cat_id";
	public static final String KEY_LINK_PATH = "device_path";
	public static final String KEY_LINK_BODY = "link_name";
	public static final String KEY_LINK_PIC = "pic";
	public static final String KEY_LINK_LINK = "link";
	public static final String KEY_LINK_DATA = "data";
	public static final String KEY_LINK_STATUS = "status";
	public static final String KEY_LINK_POS = "pos";

	public static final String KEY_Switch_ROWID = "switch_id";
	public static final String KEY_Switch_PATH = "device_path";
	public static final String KEY_Switch_BODY = "switch_name";
	public static final String KEY_Switch_PIC = "pic";
	public static final String KEY_Switch_LINK = "link";
	public static final String KEY_Switch_DATA = "data";
	public static final String KEY_Switch_STATUS = "status";
	public static final String KEY_Switch_POS = "pos";

	private static final String TAG = "CategoryDbAdapter";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation SQL statement
	 */
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + KEY_BODY
			+ " text not null , " + CAT_DESC + " text , " + CAT_IMAGE_PATH
			+ " text ," + NEST_ID + " integer," + MAX_LEN + " integer,"
			+ CAT_PATH + " integer," + KEY_CAT_POS + " text);";

	private static final String ACTION_DATABASE_CREATE = "create table "
			+ DATABASE_ACTION_TABLE + " (" + KEY_ACTION_ROWID
			+ " integer primary key autoincrement, " + KEY_ACTION_CAT_ID
			+ " integer, " + KEY_ACTION_PATH + " text, " + KEY_ACTION_BODY
			+ " text, " + KEY_ACTION_PIC + " text," + KEY_ACTION_POS
			+ " text);";

	private static final String DATABASE_SUB_CAT_ITEM_CREATE = "create table "
			+ DATABASE_SUB_CAT_ITEM_TABLE + " (" + KEY_SUB_CAT_ITEM_ROWID
			+ " integer primary key autoincrement, " + KEY_CAT_ID
			+ " integer, " + KEY_PATH + " text, " + KEY_SUB_CAT_ITEM_BODY
			+ " text, " + KEY_PIC + " text," + KEY_LINK + " text ,"
			+ KEY_SUB_CAT_POS + " text);";

	private static final String DATABASE_LINK_CREATE = "create table "
			+ DATABASE_LINK_TABLE + " (" + KEY_LINK_ROWID
			+ " integer primary key autoincrement, " + KEY_CAT_ID
			+ " integer, " + KEY_PATH + " text, " + KEY_LINK_BODY + " text, "
			+ KEY_PIC + " text," + KEY_LINK + " text," + "" + KEY_LINK_DATA
			+ " text ," + KEY_LINK_STATUS + " text ," + KEY_LINK_POS
			+ " text);";

	private static final String DATABASE_SWITCH_CREATE = "create table "
			+ DATABASE_SWITCH_TABLE + " (" + KEY_Switch_ROWID
			+ " integer primary key autoincrement, " + KEY_CAT_ID
			+ " integer, " + KEY_Switch_PATH + " text, " + KEY_Switch_BODY
			+ " text, " + KEY_Switch_PIC + " text," + KEY_Switch_LINK
			+ " text," + "" + KEY_Switch_DATA + " text ," + KEY_Switch_STATUS
			+ " text ," + KEY_Switch_POS + " text);";
	
	private static final String DATABASE_CURRENT_VER_CREATE = "create table "
			+ DATABASE_CURRENT_VER_TABLE + " (" + KEY_CURRENT_VER_ROWID
			+ " integer primary key autoincrement, " + KEY_CURRENT_VER + " integer);";
	
	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
			db.execSQL(ACTION_DATABASE_CREATE);
			db.execSQL(DATABASE_SUB_CAT_ITEM_CREATE);
			db.execSQL(DATABASE_LINK_CREATE);
			db.execSQL(DATABASE_SWITCH_CREATE);
			db.execSQL(DATABASE_CURRENT_VER_CREATE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_ACTION_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SUB_CAT_ITEM_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LINK_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SWITCH_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CURRENT_VER_TABLE);
			
			onCreate(db);
		}
	}

	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public DbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void dropDB() {
		// mDb.execSQL("DROP Database " + DATABASE_NAME);
		Log.w(TAG, "Droping tables:.. ");
		mDb.execSQL("DROP TABLE IF EXISTS category");
		mDb.execSQL("DROP TABLE IF EXISTS sub_category_item");
		mDb.execSQL("DROP TABLE IF EXISTS action");
		mDb.execSQL("DROP TABLE IF EXISTS link");
		mDb.execSQL("DROP TABLE IF EXISTS switch");
		mDb.execSQL("DROP TABLE IF EXISTS switch");
		mDb.execSQL("DROP TABLE IF EXISTS current_ver");
	}

	public void createDB() {
		// mDb.execSQL("DROP Database " + DATABASE_NAME);
		Log.w(TAG, "Creating tables:.. ");
		mDb.execSQL(DATABASE_CREATE);
		mDb.execSQL(ACTION_DATABASE_CREATE);
		mDb.execSQL(DATABASE_SUB_CAT_ITEM_CREATE);
		mDb.execSQL(DATABASE_LINK_CREATE);
		mDb.execSQL(DATABASE_SWITCH_CREATE);
		mDb.execSQL(DATABASE_CURRENT_VER_CREATE);

	}

	/*
	 * Category table
	 */	
	
	public long add_current_ver(int ver) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CURRENT_VER, ver);
		return mDb.insert(DATABASE_CURRENT_VER_TABLE, null, initialValues);
		}
	
	public Cursor fetch_current_ver() {
		Cursor mCursor = mDb.rawQuery("Select * from " + DATABASE_CURRENT_VER_TABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	public Cursor count_current_ver() {
		Cursor mCursor = mDb.rawQuery("Select count(*) from " + DATABASE_CURRENT_VER_TABLE, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	
	public boolean update_current_ver(int new_ver)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CURRENT_VER, new_ver);
		return mDb.update(DATABASE_CURRENT_VER_TABLE, initialValues,  KEY_CURRENT_VER_ROWID + "=1" ,null) >0;
	}
	

	public long createCategory(String body, String desc, String image_path,
			int nest_id, String path, int max_len) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_BODY, body);
		initialValues.put(CAT_DESC, desc);
		initialValues.put(CAT_IMAGE_PATH, image_path);
		initialValues.put(NEST_ID, nest_id);
		initialValues.put(CAT_PATH, path);
		initialValues.put(MAX_LEN, max_len);
		if(nest_id==0){
		initialValues.put(KEY_CAT_POS, ""+getcatsizeBycat(nest_id));
		}
		else{
			long d_size = getdevicesizeBycat(nest_id);
			long a_size = getactionsizeBycat(nest_id);
			long c_size = getcategorysizeBycat(nest_id);
			long l_size = getlinksizeBycat(nest_id);
			long s_size = getswitchsizeBycat(nest_id);
			long result = d_size + a_size + c_size + l_size + s_size;
			Log.d("", "result size:.." + result);
			initialValues.put(KEY_CAT_POS, result);
		}
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public long createCategory(String body, String desc, String image_path,
			int nest_id, String path, int max_len,String pos) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_BODY, body);
		initialValues.put(CAT_DESC, desc);
		initialValues.put(CAT_IMAGE_PATH, image_path);
		initialValues.put(NEST_ID, nest_id);
		initialValues.put(CAT_PATH, path);
		initialValues.put(MAX_LEN, max_len);
		initialValues.put(KEY_CAT_POS,pos);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean deleteCategory(long rowId) {
		return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor fetchAllCategory() {

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_BODY,
				CAT_DESC, CAT_IMAGE_PATH, NEST_ID, CAT_PATH, MAX_LEN,KEY_CAT_POS }, null,
				null, null, null, null);

	}

	public Cursor fetchAllCategoryOrderByNestId() {
		Cursor mCursor = mDb.rawQuery("Select * from " + DATABASE_TABLE
				+ " order by " + NEST_ID, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor getnestpath(long nest_id) {
		Cursor mCursor = mDb.rawQuery("Select * from " + DATABASE_TABLE
				+ " where cat_id=" + nest_id, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public Cursor fetchCategory(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_BODY, CAT_DESC,
				CAT_IMAGE_PATH, KEY_ROWID, NEST_ID, CAT_PATH, MAX_LEN },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchCategoryIdByNestId(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_BODY, CAT_DESC,
				CAT_IMAGE_PATH, KEY_ROWID, NEST_ID, CAT_PATH, MAX_LEN },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchCategoryIdByNestingId(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_BODY, CAT_DESC,
				CAT_IMAGE_PATH, KEY_ROWID, NEST_ID, CAT_PATH, MAX_LEN },
				NEST_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchPathByCat_id(long id) {
		Cursor mCursor =

		mDb.rawQuery("SELECT path FROM " + DATABASE_TABLE + " WHERE cat_id ="
				+ id, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchCategoryIdByName(String name) throws SQLException {
		Cursor mCursor =

		mDb.rawQuery("SELECT cat_id FROM  category Where cat_name='" + name
				+ "' order by cat_id desc LIMIT 1 ;", null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;

	}

	public boolean updatecategoryPos(long rowId, String pos) {
		ContentValues args = new ContentValues();
		args.put(KEY_CAT_POS, pos);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateCategory(long rowId, String body, String desc,
			String image_path, int nest_id, String path, int max_len) {
		ContentValues args = new ContentValues();
		args.put(KEY_BODY, body);
		args.put(CAT_DESC, desc);
		args.put(CAT_IMAGE_PATH, image_path);
		args.put(NEST_ID, nest_id);
		args.put(CAT_PATH, path);
		args.put(MAX_LEN, max_len);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateCategory(long rowId, String body, String path) {
		ContentValues args = new ContentValues();
		args.put(KEY_BODY, body);
		args.put(CAT_IMAGE_PATH, path);
		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateCategory(long rowId, int max_len) {
		ContentValues args = new ContentValues();
		args.put(MAX_LEN, max_len);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateCategoryPath(long rowId, String path) {
		System.out.println("ID of cat:.." + rowId);
		System.out.println("Path of cat:.." + path);

		ContentValues args = new ContentValues();
		args.put(CAT_PATH, path);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateCategoryNest(long rowId, String id) {
		System.out.println("ID of cat:.." + rowId);
		System.out.println("Path of cat:.." + id);

		ContentValues args = new ContentValues();
		args.put(NEST_ID, id);

		return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/*
	 * Action table
	 */

	public long createaction(int catid, String path, String body, String pic) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ACTION_CAT_ID, catid);
		initialValues.put(KEY_ACTION_PATH, path);
		initialValues.put(KEY_ACTION_BODY, body);
		initialValues.put(KEY_ACTION_PIC, pic);
		// initialValues.put(KEY_ACTION_OPEN_PATH, action_open_path);
		long d_size = getdevicesizeBycat(catid);
		long a_size = getactionsizeBycat(catid);
		long c_size = getcategorysizeBycat(catid);
		long l_size = getlinksizeBycat(catid);
		long s_size = getswitchsizeBycat(catid);
		long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);
		initialValues.put(KEY_ACTION_POS, result);
		return mDb.insert(DATABASE_ACTION_TABLE, null, initialValues);

	}
	
	public long createaction(int catid, String path, String body, String pic,String pos) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ACTION_CAT_ID, catid);
		initialValues.put(KEY_ACTION_PATH, path);
		initialValues.put(KEY_ACTION_BODY, body);
		initialValues.put(KEY_ACTION_PIC, pic);
		// initialValues.put(KEY_ACTION_OPEN_PATH, action_open_path);
		long d_size = getdevicesizeBycat(catid);
		long a_size = getactionsizeBycat(catid);
		long c_size = getcategorysizeBycat(catid);
		long l_size = getlinksizeBycat(catid);
		long s_size = getswitchsizeBycat(catid);
	/*	long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);*/
		initialValues.put(KEY_ACTION_POS, pos);
		return mDb.insert(DATABASE_ACTION_TABLE, null, initialValues);

	}

	public boolean deleteaction(long rowId) {
		return mDb.delete(DATABASE_ACTION_TABLE,
				KEY_ACTION_ROWID + "=" + rowId, null) > 0;
	}

	public boolean deleteactionByCat_id(long rowId) {
		return mDb.delete(DATABASE_ACTION_TABLE, KEY_ACTION_CAT_ID + "="
				+ rowId, null) > 0;
	}

	public Cursor fetchAllaction() {
	
		Cursor mCursor = mDb.rawQuery("Select "+ KEY_ACTION_ROWID +","+
						KEY_ACTION_CAT_ID+","+ KEY_ACTION_PATH+","+ KEY_ACTION_BODY+","+
						KEY_ACTION_PIC +","+KEY_ACTION_POS+" from " + DATABASE_ACTION_TABLE
				+ " order by " + KEY_ACTION_POS , null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		
		return mCursor;
	}

	public Cursor fetchActionByCatId(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_ACTION_TABLE, new String[] { KEY_ACTION_BODY,
				KEY_ACTION_CAT_ID, KEY_ACTION_ROWID, KEY_ACTION_PIC,
				KEY_ACTION_PATH }, KEY_ACTION_CAT_ID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchActionByActionId(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_ACTION_TABLE, new String[] { KEY_ACTION_BODY,
				KEY_ACTION_CAT_ID, KEY_ACTION_ROWID, KEY_ACTION_PIC,
				KEY_ACTION_PATH }, KEY_ACTION_ROWID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchaction(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_ACTION_TABLE, new String[] { KEY_ACTION_BODY,
				KEY_ACTION_CAT_ID, KEY_ACTION_PATH, KEY_ACTION_PIC,
				KEY_ACTION_ROWID }, KEY_ACTION_ROWID + "=" + rowId, null, null,
				null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchactionid(String name) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_ACTION_TABLE, new String[] { KEY_ACTION_ROWID,
				KEY_ACTION_CAT_ID, KEY_ACTION_PATH, KEY_ACTION_PIC },
				KEY_ACTION_BODY + "=" + "'" + name + "'", null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchactionOrderByCatid() throws SQLException {
		Cursor mCursor =

		mDb.query(DATABASE_ACTION_TABLE, new String[] { KEY_ACTION_BODY,
				KEY_ACTION_CAT_ID, KEY_ACTION_ROWID, KEY_ACTION_PATH,
				KEY_ACTION_PIC }, null, null, null, null, " "
				+ KEY_ACTION_CAT_ID + " ASC");

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public boolean updateaction(long rowId, long cat_id, String path,
			String body, String pic) {
		ContentValues args = new ContentValues();
		args.put(KEY_ACTION_PATH, path);
		args.put(KEY_ACTION_CAT_ID, cat_id);
		args.put(KEY_ACTION_PIC, pic);
		args.put(KEY_ACTION_BODY, body);
		// args.put(KEY_ACTION_OPEN_PATH, action_open_path);

		return mDb.update(DATABASE_ACTION_TABLE, args, KEY_ACTION_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean updateactionPos(long rowId, String pos) {
		ContentValues args = new ContentValues();
		args.put(KEY_ACTION_POS, pos);
		return mDb.update(DATABASE_ACTION_TABLE, args, KEY_ACTION_ROWID + "="
				+ rowId, null) > 0;
	}

	/*
	 * Sub Category Item
	 */

	public long createSubCategoryItem(int cat_id, String path, String body,
			String pic, String link) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CAT_ID, cat_id);
		initialValues.put(KEY_PATH, path);
		initialValues.put(KEY_SUB_CAT_ITEM_BODY, body);
		initialValues.put(KEY_PIC, pic);
		initialValues.put(KEY_LINK, link);

		long d_size = getdevicesizeBycat(cat_id);
		long a_size = getactionsizeBycat(cat_id);

		long c_size = getcategorysizeBycat(cat_id);
		long l_size = getlinksizeBycat(cat_id);
		long s_size = getswitchsizeBycat(cat_id);
		long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);
		initialValues.put(KEY_SUB_CAT_POS, result);

		return mDb.insert(DATABASE_SUB_CAT_ITEM_TABLE, null, initialValues);
	}
	public long createSubCategoryItem(int cat_id, String path, String body,
			String pic, String link,String pos) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CAT_ID, cat_id);
		initialValues.put(KEY_PATH, path);
		initialValues.put(KEY_SUB_CAT_ITEM_BODY, body);
		initialValues.put(KEY_PIC, pic);
		initialValues.put(KEY_LINK, link);

		long d_size = getdevicesizeBycat(cat_id);
		long a_size = getactionsizeBycat(cat_id);

		long c_size = getcategorysizeBycat(cat_id);
		long l_size = getlinksizeBycat(cat_id);
		long s_size = getswitchsizeBycat(cat_id);
		/*long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);*/
		initialValues.put(KEY_SUB_CAT_POS, pos);

		return mDb.insert(DATABASE_SUB_CAT_ITEM_TABLE, null, initialValues);
	}
	

	public boolean deleteSubCategoryItem(long rowId) {
		return mDb.delete(DATABASE_SUB_CAT_ITEM_TABLE, KEY_SUB_CAT_ITEM_ROWID
				+ "=" + rowId, null) > 0;
	}

	public boolean deleteSubCategoryItemByCat_id(long rowId) {
		return mDb.delete(DATABASE_SUB_CAT_ITEM_TABLE,
				KEY_CAT_ID + "=" + rowId, null) > 0;
	}

	public Cursor fetchAllSubCategoryItem() {
		return mDb.query(DATABASE_SUB_CAT_ITEM_TABLE, new String[] {
				KEY_SUB_CAT_ITEM_ROWID, KEY_CAT_ID, KEY_PATH,
				KEY_SUB_CAT_ITEM_BODY, KEY_PIC, KEY_LINK }, null, null, null,
				null, null);
	}

	public Cursor fetchSubCategoryItem() throws SQLException {

		Cursor mCursor = mDb.rawQuery("Select * from "
				+ DATABASE_SUB_CAT_ITEM_TABLE + " order by " + KEY_SUB_CAT_POS,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;

	}

	public Cursor fetchSubCategoryItemOrderBy() throws SQLException {
		Cursor mCursor = mDb.rawQuery("Select * from "
				+ DATABASE_SUB_CAT_ITEM_TABLE + " order by " + KEY_CAT_ID + ","
				+ KEY_PATH, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchSubCategoryItemByCatAndSubCatId(long cat_id,
			long sub_cat_id) throws SQLException {
		String selection = KEY_CAT_ID + " = '" + cat_id + "'" + " AND "
				+ KEY_PATH + " = '" + sub_cat_id + "'";

		Cursor mCursor =

		mDb.query(true, DATABASE_SUB_CAT_ITEM_TABLE, new String[] {
				KEY_SUB_CAT_ITEM_ROWID, KEY_CAT_ID, KEY_PATH,
				KEY_SUB_CAT_ITEM_BODY, KEY_PIC, KEY_LINK }, selection, null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchSubCategoryItem(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_SUB_CAT_ITEM_TABLE, new String[] {
				KEY_SUB_CAT_ITEM_ROWID, KEY_CAT_ID, KEY_PATH,
				KEY_SUB_CAT_ITEM_BODY, KEY_PIC, KEY_LINK },
				KEY_SUB_CAT_ITEM_ROWID + "=" + rowId, null, null, null, null,
				null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchSubCategoryItemByCatId(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_SUB_CAT_ITEM_TABLE, new String[] {
				KEY_SUB_CAT_ITEM_ROWID, KEY_CAT_ID, KEY_PATH,
				KEY_SUB_CAT_ITEM_BODY, KEY_PIC, KEY_LINK }, KEY_CAT_ID + "="
				+ rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean updateSubCategoryItem(long rowId, int cat_id, String path,
			String body, String pic, String link) {
		ContentValues args = new ContentValues();
		args.put(KEY_SUB_CAT_ITEM_BODY, body);
		args.put(KEY_CAT_ID, cat_id);
		args.put(KEY_PATH, path);
		args.put(KEY_PIC, pic);
		args.put(KEY_LINK, link);

		return mDb.update(DATABASE_SUB_CAT_ITEM_TABLE, args,
				KEY_SUB_CAT_ITEM_ROWID + "=" + rowId, null) > 0;
	}

	public boolean updateSubCategoryItemPos(long rowId, String pos) {
		ContentValues args = new ContentValues();
		args.put(KEY_SUB_CAT_POS, pos);

		return mDb.update(DATABASE_SUB_CAT_ITEM_TABLE, args,
				KEY_SUB_CAT_ITEM_ROWID + "=" + rowId, null) > 0;
	}

	/*
	 * 
	 * Link
	 */

	public long createLinkItem(int cat_id, String path, String body,
			String pic, String link, String data, String status) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CAT_ID, cat_id);
		initialValues.put(KEY_LINK_PATH, path);
		initialValues.put(KEY_LINK_BODY, body);
		initialValues.put(KEY_LINK_PIC, pic);
		initialValues.put(KEY_LINK_LINK, link);
		initialValues.put(KEY_LINK_DATA, data);
		initialValues.put(KEY_LINK_STATUS, status);

		long d_size = getdevicesizeBycat(cat_id);
		long a_size = getactionsizeBycat(cat_id);
		long c_size = getcategorysizeBycat(cat_id);
		long l_size = getlinksizeBycat(cat_id);
		long s_size = getswitchsizeBycat(cat_id);
		long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);
		initialValues.put(KEY_LINK_POS, result);

		return mDb.insert(DATABASE_LINK_TABLE, null, initialValues);
	}
	public long createLinkItem(int cat_id, String path, String body,
			String pic, String link, String data, String status,String pos) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CAT_ID, cat_id);
		initialValues.put(KEY_LINK_PATH, path);
		initialValues.put(KEY_LINK_BODY, body);
		initialValues.put(KEY_LINK_PIC, pic);
		initialValues.put(KEY_LINK_LINK, link);
		initialValues.put(KEY_LINK_DATA, data);
		initialValues.put(KEY_LINK_STATUS, status);

		long d_size = getdevicesizeBycat(cat_id);
		long a_size = getactionsizeBycat(cat_id);
		long c_size = getcategorysizeBycat(cat_id);
		long l_size = getlinksizeBycat(cat_id);
		long s_size = getswitchsizeBycat(cat_id);
		/*long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);*/
		initialValues.put(KEY_LINK_POS, pos);

		return mDb.insert(DATABASE_LINK_TABLE, null, initialValues);
	}


	public boolean deleteLinkItem(long rowId) {
		return mDb.delete(DATABASE_LINK_TABLE, KEY_LINK_ROWID + "=" + rowId,
				null) > 0;
	}

	public boolean deleteLinkByCat_id(long rowId) {
		return mDb.delete(DATABASE_LINK_TABLE, KEY_CAT_ID + "=" + rowId, null) > 0;
	}

	public Cursor fetchLink() throws SQLException {
		
		Cursor mCursor = mDb.rawQuery("Select * from "
				+ DATABASE_LINK_TABLE + " order by " + KEY_LINK_POS,
				null);
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchLinkItemOrderBy() throws SQLException {
		Cursor mCursor = mDb.rawQuery("Select * from " + DATABASE_LINK_TABLE
				+ " order by " + KEY_CAT_ID + "," + KEY_LINK_PATH, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchLinkByCatAndSubCatId(long cat_id, long sub_cat_id)
			throws SQLException {
		String selection = KEY_CAT_ID + " = '" + cat_id + "'" + " AND "
				+ KEY_LINK_PATH + " = '" + sub_cat_id + "'";

		Cursor mCursor =

		mDb.query(true, DATABASE_LINK_TABLE, new String[] { KEY_LINK_ROWID,
				KEY_CAT_ID, KEY_LINK_PATH, KEY_LINK_BODY, KEY_LINK_PIC,
				KEY_LINK_LINK, KEY_LINK_DATA }, selection, null, null, null,
				null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchLinkItem(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_LINK_TABLE, new String[] { KEY_LINK_ROWID,
				KEY_CAT_ID, KEY_LINK_PATH, KEY_LINK_BODY, KEY_LINK_PIC,
				KEY_LINK_LINK, KEY_LINK_DATA, KEY_LINK_STATUS }, KEY_LINK_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchLinkItemByCatid(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_LINK_TABLE, new String[] { KEY_LINK_ROWID,
				KEY_CAT_ID, KEY_LINK_PATH, KEY_LINK_BODY, KEY_LINK_PIC,
				KEY_LINK_LINK, KEY_LINK_DATA, KEY_LINK_STATUS }, KEY_CAT_ID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean updateLinkItem(long rowId, int cat_id, String path,
			String body, String pic, String link, String data, String status) {
		ContentValues args = new ContentValues();
		args.put(KEY_LINK_BODY, body);
		args.put(KEY_CAT_ID, cat_id);
		args.put(KEY_LINK_PATH, path);
		args.put(KEY_LINK_PIC, pic);
		args.put(KEY_LINK_LINK, link);
		args.put(KEY_LINK_DATA, data);
		args.put(KEY_LINK_STATUS, status);
		return mDb.update(DATABASE_LINK_TABLE, args, KEY_LINK_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean updateLinkItemPos(long rowId, String pos) {
		ContentValues args = new ContentValues();
		args.put(KEY_LINK_POS, pos);

		return mDb.update(DATABASE_LINK_TABLE, args, KEY_LINK_ROWID + "="
				+ rowId, null) > 0;
	}

	/*
	 * 
	 * Switch
	 */

	public long createSwitchItem(int cat_id, String path, String body,
			String pic, String link, String data, String status) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CAT_ID, cat_id);
		initialValues.put(KEY_Switch_PATH, path);
		initialValues.put(KEY_Switch_BODY, body);
		initialValues.put(KEY_Switch_PIC, pic);
		initialValues.put(KEY_Switch_LINK, link);
		initialValues.put(KEY_Switch_DATA, data);
		initialValues.put(KEY_Switch_STATUS, status);

		long d_size = getdevicesizeBycat(cat_id);
		long a_size = getactionsizeBycat(cat_id);
		long c_size = getcategorysizeBycat(cat_id);
		long l_size = getlinksizeBycat(cat_id);
		long s_size = getswitchsizeBycat(cat_id);
		long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);
		initialValues.put(KEY_Switch_POS, result);

		return mDb.insert(DATABASE_SWITCH_TABLE, null, initialValues);
	}
	
	public long createSwitchItem(int cat_id, String path, String body,
			String pic, String link, String data, String status,String pos) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_CAT_ID, cat_id);
		initialValues.put(KEY_Switch_PATH, path);
		initialValues.put(KEY_Switch_BODY, body);
		initialValues.put(KEY_Switch_PIC, pic);
		initialValues.put(KEY_Switch_LINK, link);
		initialValues.put(KEY_Switch_DATA, data);
		initialValues.put(KEY_Switch_STATUS, status);

		/*long d_size = getdevicesizeBycat(cat_id);
		long a_size = getactionsizeBycat(cat_id);
		long c_size = getcategorysizeBycat(cat_id);
		long l_size = getlinksizeBycat(cat_id);
		long s_size = getswitchsizeBycat(cat_id);
		long result = d_size + a_size + c_size + l_size + s_size;
		Log.d("", "result size:.." + result);*/
		initialValues.put(KEY_Switch_POS, pos);

		return mDb.insert(DATABASE_SWITCH_TABLE, null, initialValues);
	}

	public boolean deleteSwitchItem(long rowId) {
		return mDb.delete(DATABASE_SWITCH_TABLE,
				KEY_Switch_ROWID + "=" + rowId, null) > 0;
	}

	public boolean deleteSwitchByCat_id(long rowId) {
		return mDb
				.delete(DATABASE_SWITCH_TABLE, KEY_CAT_ID + "=" + rowId, null) > 0;
	}

	public Cursor fetchSwitch() throws SQLException {
		
		Cursor mCursor = mDb.rawQuery("Select * from switch  order by " + KEY_Switch_POS,
				null);
		
		
		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;

	}

	public Cursor fetchSwitchItemOrderBy() throws SQLException {
		Cursor mCursor = mDb.rawQuery("Select * from " + DATABASE_SWITCH_TABLE
				+ " order by " + KEY_CAT_ID + "," + KEY_Switch_PATH, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchSwitchByCatAndSubCatId(long cat_id, long sub_cat_id)
			throws SQLException {
		String selection = KEY_CAT_ID + " = '" + cat_id + "'" + " AND "
				+ KEY_Switch_PATH + " = '" + sub_cat_id + "'";

		Cursor mCursor =

		mDb.query(true, DATABASE_SWITCH_TABLE, new String[] { KEY_Switch_ROWID,
				KEY_CAT_ID, KEY_Switch_PATH, KEY_LINK_BODY, KEY_LINK_PIC,
				KEY_LINK_LINK, KEY_LINK_DATA, KEY_Switch_STATUS }, selection,
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchSwitchItem(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_SWITCH_TABLE, new String[] { KEY_Switch_ROWID,
				KEY_CAT_ID, KEY_Switch_PATH, KEY_Switch_BODY, KEY_Switch_PIC,
				KEY_Switch_LINK, KEY_Switch_DATA, KEY_Switch_STATUS },
				KEY_Switch_ROWID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public Cursor fetchSwitchItemByCatid(long rowId) throws SQLException {
		Cursor mCursor =

		mDb.query(true, DATABASE_SWITCH_TABLE, new String[] { KEY_Switch_ROWID,
				KEY_CAT_ID, KEY_Switch_PATH, KEY_Switch_BODY, KEY_Switch_PIC,
				KEY_Switch_LINK, KEY_Switch_DATA, KEY_Switch_STATUS },
				KEY_CAT_ID + "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	public boolean updateSwitchItem(long rowId, int cat_id, String path,
			String body, String pic, String link, String data, String status) {
		ContentValues args = new ContentValues();
		args.put(KEY_Switch_BODY, body);
		args.put(KEY_CAT_ID, cat_id);
		args.put(KEY_Switch_PATH, path);
		args.put(KEY_Switch_PIC, pic);
		args.put(KEY_Switch_LINK, link);
		args.put(KEY_Switch_DATA, data);
		args.put(KEY_Switch_STATUS, status);
		return mDb.update(DATABASE_SWITCH_TABLE, args, KEY_Switch_ROWID + "="
				+ rowId, null) > 0;
	}

	public boolean updateSwitchItemPos(long rowId, String pos) {
		ContentValues args = new ContentValues();
		args.put(KEY_Switch_POS, pos);
		return mDb.update(DATABASE_SWITCH_TABLE, args, KEY_Switch_ROWID + "="
				+ rowId, null) > 0;
	}

	/*
	 * 
	 * get count of tables data
	 */

	public long getcatagorysize() {
		long numRows = DatabaseUtils.queryNumEntries(mDb, DATABASE_TABLE);
		System.out.println("numRows:.." + numRows);
		return numRows;
	}
	public long getcatsizeBycat(long id) {

		  long numRows = DatabaseUtils.longForQuery(mDb,
		    "SELECT COUNT(*) FROM category " + " WHERE nest_id ='"
		      + id + "'", null);

		  System.out.println("numRows:.." + numRows);
		  return numRows;

		 }
	public long getcategorysizeBycat(int id) {

		String cat_id = getCatIDMatchesWithPath(id);

		String cat_ids[] = cat_id.split(",");
		long numRows = cat_ids.length;

		System.out.println("numRows:.." + (numRows - 1));
		return (numRows - 1);

	}

	public long getdevicesizeBycat(long id) {

		long numRows = DatabaseUtils.longForQuery(mDb,
				"SELECT COUNT(*) FROM sub_category_item " + " WHERE cat_id ='"
						+ id + "'", null);

		System.out.println("numRows:.." + numRows);
		return numRows;

	}

	public long getlinksizeBycat(long id) {

		long numRows = DatabaseUtils.longForQuery(mDb,
				"SELECT COUNT(*) FROM link " + " WHERE cat_id ='" + id + "'",
				null);

		System.out.println("numRows:.." + numRows);
		return numRows;

	}

	public long getactionsizeBycat(long id) {

		long numRows = DatabaseUtils.longForQuery(mDb,
				"SELECT COUNT(*) FROM action " + " WHERE cat_id ='" + id + "'",
				null);

		System.out.println("numRows:.." + numRows);
		return numRows;

	}

	public long getswitchsizeBycat(long id) {

		long numRows = DatabaseUtils.longForQuery(mDb,
				"SELECT COUNT(*) FROM switch " + " WHERE cat_id ='" + id + "'",
				null);

		System.out.println("numRows:.." + numRows);
		return numRows;

	}

	public String getCatIDMatchesWithPath(int id) {
		Cursor catcussor = null;
		String path = "";
		String cat_ids = "";
		try {
			catcussor = fetchAllCategory();

			if (catcussor.moveToFirst()) {
				do {
					path = catcussor
							.getString(catcussor.getColumnIndex("path"));
					String ar[] = path.split(",");
					for (int i = 0; i < ar.length; i++) {
						System.out.println("ar[i].." + ar[i]);
						System.out.println("id.." + id);
						if (ar[i].equals("" + id)) {
							System.out.println("Match");
							if (cat_ids.equals("")) {
								cat_ids = catcussor.getString(catcussor
										.getColumnIndex("cat_id"));
							} else {
								cat_ids = cat_ids
										+ ","
										+ catcussor.getString(catcussor
												.getColumnIndex("cat_id"));
							}
							break;
						} else {
							System.out.println("Not Match");
						}

					}

					System.out.println("cat_ids:..." + cat_ids);
				} while (catcussor.moveToNext());

			}
		} catch (Exception ex) {
			System.out.println("Error:" + ex.getMessage());
		}
		return cat_ids;
	}

}
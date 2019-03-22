package menuapp.activity;

import java.util.ArrayList;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;
import menuapp.activity.util.txtdata.TXTParser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;

public class MainActivity extends Activity {
	private ArrayList<AppModel> catList;
	private ArrayList<ActionModel> actionlist;
	private ArrayList<SubCatItemModel> subCatItemList;
	private ArrayList<LinkModel> linktemList;
	private ArrayList<SwitchModel> switchtemList;
	DbAdapter mDbHelper;
	AppModel tempValues = null;
	ActionModel actionvalues = null;
	SubCatItemModel subCatItemtempValues = null;
	LinkModel LinkItemtempValues = null;
	SwitchModel SwicthItemtempValues = null;
	SharedPreferencesManager spm;
	Context mcon;
	String error = "false";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		System.out.println("splash..");
		mcon = this;
		spm = new SharedPreferencesManager(mcon);
		String basefolder = spm.getStringValues("basefolder");
		System.out.println("" + basefolder);
		if (basefolder.equals("0")) {
			String path = Environment.getExternalStorageDirectory()
					+ "/OneRemote";
			spm.saveStringValues("basefolder", path);
		} else {
			String path = Environment.getExternalStorageDirectory()
					+ "/OneRemote";
			spm.saveStringValues("basefolder", path);
			System.out.println("else:");
		}

		dbSetup();
		
		Runnable rn = new Runnable() {

			@Override
			public void run() {
				try {
					syncDb();
					error = "false";
				} catch (Exception ex) {
					error = "true";
					Log.e("Main Activity", "" + ex.getMessage());
					/*
					 * spm.saveStringValues("session", "add");
					 * spm.saveStringValues("sub_session", "add");
					 * spm.saveStringValues(getString(R.string.Cat_id), "" + 0);
					 * spm.saveStringValues(getString(R.string.Cat_name), "" +
					 * 0); spm.saveStringValues(getString(R.string.Sub_Cat_id),
					 * "" + 0);
					 * spm.saveStringValues(getString(R.string.Sub_Cat_name), ""
					 * + 0);
					 * spm.saveStringValues(getString(R.string.Sub_Cat_item_id),
					 * "" + 0);
					 * spm.saveStringValues(getString(R.string.Sub_Cat_item_name
					 * ), "" + 0);
					 */
					Intent intent = new Intent(MainActivity.this,
							ErrorScreen.class);
					startActivity(intent);
					MainActivity.this.finish();

				}
				System.out.println("error:..." + error);
				if (error.equals("false")) {
					spm.saveStringValues("session", "add");
					spm.saveStringValues("sub_session", "add");
					spm.saveStringValues(getString(R.string.Cat_id), "" + 0);
					spm.saveStringValues(getString(R.string.Cat_name), "" + 0);
					spm.saveStringValues(getString(R.string.Sub_Cat_id), "" + 0);
					spm.saveStringValues(getString(R.string.Sub_Cat_name),
							"" + 0);
					spm.saveStringValues(getString(R.string.Sub_Cat_item_id),
							"" + 0);
					spm.saveStringValues(getString(R.string.Sub_Cat_item_name),
							"" + 0);
					Intent intent = new Intent(MainActivity.this,
							ListCategory.class);
					startActivity(intent);
					finish();
				} else {

				}

			}
		};

		Thread mythread = new Thread(rn);
		mythread.start();

	}

	
	private void syncDb() {
		
		TXTParser txtparserdevice = new TXTParser();
		  String version_no = txtparserdevice.getAllVersionResult(MainActivity.this);
		  
		  Log.v("Final Ver :", version_no);
		   
		  try
		  {
		      mDbHelper.add_current_ver(Integer.parseInt(version_no));
		  }
		  catch(Exception e)
		  {
		      e.printStackTrace();
		  }
		
		
		TXTParser txtparser = new TXTParser();
		catList = txtparser.getAllCategoryResult(MainActivity.this, mDbHelper);

		if (catList.size() > 0) {
			for (int i = 0; i < catList.size(); i++) {

				tempValues = (AppModel) catList.get(i);
				String pic = "";

				if (!tempValues.getPic().equals("&")) {
					pic = tempValues.getPic();
				}

				mDbHelper.createCategory(tempValues.getName(), "", pic,
						tempValues.getNest_id(), "0", tempValues.getMax_len(),""+tempValues.getPos());
				System.out.println("length" + tempValues.getPath());
				System.out.println("value of i" + i);
				String cat_path = "";
				String ar[] = tempValues.getPath().split(",");
				for (int j = 0; j < ar.length; j++) {
					if (j == 0) {
						cat_path = new GetDataFrmDB().getCatIDByName(ar[j],
								mDbHelper);
					} else {
						cat_path = cat_path
								+ ","
								+ new GetDataFrmDB().getCatIDByName(ar[j],
										mDbHelper);
					}
				}

				mDbHelper.updateCategoryPath((i + 1), cat_path);
			}
		}

		TXTParser devicetxtparser = new TXTParser();
		subCatItemList = devicetxtparser.getAllDevicesResults(
				MainActivity.this, mDbHelper);

		if (subCatItemList.size() > 0) {
			for (int i = 0; i < subCatItemList.size(); i++) {

				subCatItemtempValues = (SubCatItemModel) subCatItemList.get(i);
				String pic = "", link = "";
				if (!subCatItemtempValues.getKEY_PIC().equals("&")) {
					pic = subCatItemtempValues.getKEY_PIC();
				}
				if (!subCatItemtempValues.getKEY_LINK().equals("&")) {
					link = subCatItemtempValues.getKEY_LINK();
				}
				mDbHelper.createSubCategoryItem(
						Integer.parseInt(subCatItemtempValues.getKEY_CAT_ID()),
						subCatItemtempValues.getKey_DEVICE_PATH(),
						subCatItemtempValues.getKEY_SUB_CAT_ITEM_BODY(), pic,
						link,subCatItemtempValues.getKEY_SUB_CAT_ITEM_Pos());

			}
		}
		TXTParser actiontxtparser = new TXTParser();
		actionlist = actiontxtparser.getAllActionResults(MainActivity.this,
				mDbHelper);

		if (actionlist.size() > 0) {
			for (int i = 0; i < actionlist.size(); i++) {

				actionvalues = (ActionModel) actionlist.get(i);
				String pic = "";
				if (!actionvalues.getKEY_ACTION_PIC().equals("&")) {
					pic = actionvalues.getKEY_ACTION_PIC();
				}
				mDbHelper.createaction(
						Integer.parseInt(actionvalues.getKEY_CAT_ID()),
						actionvalues.getKey_ACTION_PATH(),
						actionvalues.getKEY_ACTION_BODY(), pic,""+actionvalues.getKEY_ACTION_POS());

			}
		}
		TXTParser linktxtparser = new TXTParser();
		linktemList = linktxtparser.getAllLinkResults(MainActivity.this,
				mDbHelper);

		if (linktemList.size() > 0) {
			for (int i = 0; i < linktemList.size(); i++) {

				LinkItemtempValues = (LinkModel) linktemList.get(i);
				String pic = "", link = "", data = "",status="";
				if (!LinkItemtempValues.getKEY_LINK_PIC().equals("&")) {
					pic = LinkItemtempValues.getKEY_LINK_PIC();
				}
				if (!LinkItemtempValues.getKEY_LINK_LINK().equals("&")) {
					link = LinkItemtempValues.getKEY_LINK_LINK();
				}
				if (!LinkItemtempValues.getKEY_LINK_DATA().equals("&")) {
					data = LinkItemtempValues.getKEY_LINK_DATA();
				}
				if (!LinkItemtempValues.getkEY_LINK_STATUS().equals("&")) {
					status = LinkItemtempValues.getkEY_LINK_STATUS();
				}
				mDbHelper.createLinkItem(
						Integer.parseInt(LinkItemtempValues.getKEY_CAT_ID()),
						LinkItemtempValues.getKey_LINK_DEVICE_PATH(),
						LinkItemtempValues.getKEY_LINK_BODY(), pic, link, data,status,LinkItemtempValues.getKEY_LINK_POS());

			}
		}
		TXTParser switchtxtparser = new TXTParser();
		switchtemList = switchtxtparser.getAllSwitchResults(MainActivity.this,
				mDbHelper);

		if (switchtemList.size() > 0) {
			for (int i = 0; i < switchtemList.size(); i++) {

				SwicthItemtempValues = (SwitchModel) switchtemList.get(i);
				String pic = "", link = "", data = "",status="";
				if (!SwicthItemtempValues.getKEY_SWITCH_PIC().equals("&")) {
					pic = SwicthItemtempValues.getKEY_SWITCH_PIC();
				}
				if (!SwicthItemtempValues.getKEY_SWITCH_LINK().equals("&")) {
					link = SwicthItemtempValues.getKEY_SWITCH_LINK();
				}
				if (!SwicthItemtempValues.getKEY_SWITCH_DATA().equals("&")) {
					data = SwicthItemtempValues.getKEY_SWITCH_DATA();
				}
				if (!SwicthItemtempValues.getkEY_SWITCH_STATUS().equals("&")) {
					status = SwicthItemtempValues.getkEY_SWITCH_STATUS();
				}
				mDbHelper.createSwitchItem(
						Integer.parseInt(SwicthItemtempValues.getKEY_CAT_ID()),
						SwicthItemtempValues.getKey_SWITCH_DEVICE_PATH(),
						SwicthItemtempValues.getKEY_SWITCH_BODY(), pic, link, data,status,SwicthItemtempValues.getKEY_SWITCH_POS());

			}
		}
		
		
		
		/*
		 * subCatList =
		 * txtparser.getAllSubCategoryCatIdResults(MainActivity.this,
		 * mDbHelper);
		 * 
		 * if (subCatList.size() > 0) { for (int i = 0; i < subCatList.size();
		 * i++) { System.out.println("Filling sub cat:.."); subCattempValues =
		 * (SubCatAppModel) subCatList.get(i);
		 * 
		 * System.out.println("Filling cat id:.." +
		 * subCattempValues.getCAT_Id()); mDbHelper.createSubCategory(
		 * Integer.parseInt(subCattempValues.getCAT_Id()),
		 * subCattempValues.getName()); } }
		 */

		/*
		 * subCatItemList = txtparser.getAllSubCategoryItemCatIdResults(
		 * MainActivity.this, mDbHelper); if (subCatItemList.size() > 0) {
		 * System.out.println("subCatItemList.size():.." +
		 * subCatItemList.size()); for (int i = 0; i < subCatItemList.size();
		 * i++) {
		 * 
		 * System.out.println("Filling sub cat item:.."); subCatItemtempValues =
		 * (SubCatItemModel) subCatItemList.get(i);
		 * 
		 * System.out.println("cat_id:.." +
		 * subCatItemtempValues.getKEY_CAT_ID());
		 * 
		 * 
		 * int cat_id = Integer.parseInt(subCatItemtempValues .getKEY_CAT_ID());
		 * 
		 * String body = "",tempup="",pic=""; if
		 * (!subCatItemtempValues.getKEY_SUB_CAT_ITEM_BODY() .equals("&")) {
		 * body = subCatItemtempValues.getKEY_SUB_CAT_ITEM_BODY(); }
		 * 
		 * mDbHelper.createSubCategoryItem(cat_id,
		 * subCatItemtempValues.getKey_DEVICE_PATH(), body ,pic); } }
		 */

	}

	private void dbSetup() {
		// TODO Auto-generated method stub
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		mDbHelper.dropDB();
		mDbHelper.createDB();

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();

	}
}

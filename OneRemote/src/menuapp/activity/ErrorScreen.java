package menuapp.activity;

import java.util.ArrayList;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.SubCatAppModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.txtdata.TXTGenerator;
import menuapp.activity.util.txtdata.TXTHandler;
import menuapp.activity.util.xmldata.XMLParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class ErrorScreen extends Activity {
	String Restore = "false";
	DbAdapter mDbHelper;
	Context mcon;
	Button btn_clk;
	SharedPreferencesManager spm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.error_screen);
		mcon = this;
		btn_clk = (Button) findViewById(R.id.btn_clk);
		spm = new SharedPreferencesManager(mcon);
		dbSetup();

		btn_clk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialog(
						"Some thing went wrong while loading data.Please restore the previous data other wise you will not be able to use the app.",
						false);
			}
		});

	}

	private String LoadDumpFile() {
		String value = "underprogress";

		new TXTHandler().deleteTxtFiles(ErrorScreen.this);
		
		mDbHelper.dropDB();
		mDbHelper.createDB();
		
		XMLParser parser = new XMLParser();
		AppModel tempValues = null;
		SubCatAppModel subCattempValues = null;
		SubCatItemModel subCatItemtempValues = null;

		ArrayList<AppModel> cat_data = parser.getAllCategoryResult(mcon,0);
		for (int i = 0; i < cat_data.size(); i++) {
			tempValues = (AppModel) cat_data.get(i);
			mDbHelper.createCategory(tempValues.getName(),tempValues.getDesc(),tempValues.getPic(),tempValues.getNest_id(),tempValues.getPath(),0);
		}

		/*ArrayList<SubCatAppModel> sub_cat_data = parser
				.getAllSubCategoryCatIdResults(mcon);
		for (int i = 0; i < sub_cat_data.size(); i++) {
			subCattempValues = (SubCatAppModel) sub_cat_data.get(i);
			mDbHelper.createaction(
					Integer.parseInt(subCattempValues.getCAT_Id()),
					subCattempValues.getName());
		}*/

		ArrayList<SubCatItemModel> sub_cat_item_data = parser
				.getAllSubCategoryItemCatIdResults(mcon);
		for (int i = 0; i < sub_cat_item_data.size(); i++) {
			subCatItemtempValues = (SubCatItemModel) sub_cat_item_data.get(i);

			int cat_id = Integer.parseInt(subCatItemtempValues.getKEY_CAT_ID());
			
			mDbHelper.createSubCategoryItem(cat_id, subCatItemtempValues.getKey_DEVICE_PATH(),
					subCatItemtempValues.getKEY_SUB_CAT_ITEM_BODY(),"",subCatItemtempValues.getKEY_LINK());
		}

		ArrayList<AppModel> array_cat_data = new GetDataFrmDB()
				.getAllCategoryResult(mDbHelper);
		new TXTGenerator().generateCatTXTFile(array_cat_data,mDbHelper,ErrorScreen.this);

		/*ArrayList<SubCatAppModel> array_sub_cat_data = new GetDataFrmDB()
				.getAllSubCategoryResults(mDbHelper);
		new TXTGenerator().generateSubCatTXTFile(array_sub_cat_data, mDbHelper);*/

		ArrayList<SubCatItemModel> array_sub_cat_item_data = new GetDataFrmDB()
				.getAllSubCategoryItemResult2(mDbHelper);
		new TXTGenerator().generateSubCatItemTXTFile(array_sub_cat_item_data,
				mDbHelper,ErrorScreen.this);
		value = "done";

		return value;
	}

	private void dbSetup() {
		// TODO Auto-generated method stub
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();

	}

	public void showDialog(final String message, boolean isCancelable) {

		AlertDialog.Builder builder = new AlertDialog.Builder((Activity) mcon);

		builder.setCancelable(isCancelable);

		builder.setTitle(mcon.getResources().getString(R.string.app_name));

		builder.setMessage(message);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Restore = "true";
				dialog.dismiss();
				String output = LoadDumpFile();
				if (output.equals("done")) {
					spm.saveStringValues("session", "add");
					spm.saveStringValues("sub_session", "add");
					Intent intent = new Intent(ErrorScreen.this,
							ListCategory.class);
					startActivity(intent);
					ErrorScreen.this.finish();
				}

			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
						finish();
					}
				});

		builder.show();

	}

}

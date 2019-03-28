package menuapp.activity;

import java.util.ArrayList;

import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.adapter.GetAllActionSpinnerDataListAdapter;
import menuapp.activity.util.adapter.GetAllCategorySpinnerDataListAdapter;
import menuapp.activity.util.adapter.GetAllDeviceSpinnerDataListAdapter;
import menuapp.activity.util.adapter.GetAllLinkSpinnerDataListAdapter;
import menuapp.activity.util.adapter.GetAllSwitchSpinnerDataListAdapter;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SelectLevel extends Activity implements OnClickListener {

	Button submit;
	DbAdapter mDbHelper;
	Spinner sp_name;
	Context mcon;
	SharedPreferencesManager spm;
	String id = "";
	int ViewClicked;
	ImageView back;
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_level);

		mcon = SelectLevel.this;

		spm = new SharedPreferencesManager(mcon);

		submit = (Button) findViewById(R.id.submit);

		sp_name = (Spinner) findViewById(R.id.sp_name);

		back = (ImageView) findViewById(R.id.back);

		dbSetup();
		screensetup();
		spinnersetup();
		submit.setOnClickListener(this);

		back.setOnClickListener(this);

	}

	private void dbSetup() {
		mDbHelper = new DbAdapter(this);

		mDbHelper.open();

	}

	public void screensetup() {

		System.out.println("level:..." + spm.getStringValues("level"));

		if (getIntent().getStringExtra("selectedvalue").toString()
				.equals("Category")) {
			ArrayList<AppModel> CatData = new GetDataFrmDB()
					.getAllCategoryResult(mDbHelper);
			GetAllCategorySpinnerDataListAdapter cm = new GetAllCategorySpinnerDataListAdapter(
					mcon, CatData);
			sp_name.setAdapter(cm);
		}
		if (getIntent().getStringExtra("selectedvalue").toString()
				.equals("Device")) {
			ArrayList<SubCatItemModel> deviceData = new GetDataFrmDB()
					.getAllSubCategoryItemOrderByResult2(mDbHelper);
			GetAllDeviceSpinnerDataListAdapter cm = new GetAllDeviceSpinnerDataListAdapter(
					mcon, deviceData);
			sp_name.setAdapter(cm);
		}
		if (getIntent().getStringExtra("selectedvalue").toString()
				.equals("Action")) {
			ArrayList<ActionModel> deviceData = new GetDataFrmDB()
					.getAllAction(mDbHelper);
			GetAllActionSpinnerDataListAdapter cm = new GetAllActionSpinnerDataListAdapter(
					mcon, deviceData);
			sp_name.setAdapter(cm);
		}
		
		if (getIntent().getStringExtra("selectedvalue").toString()
				.equals("Link")) {
			ArrayList<LinkModel> linkData = new GetDataFrmDB()
					.getAllLinkResult(mDbHelper);
			GetAllLinkSpinnerDataListAdapter cm = new GetAllLinkSpinnerDataListAdapter(
					mcon, linkData);
			sp_name.setAdapter(cm);
		}
		if (getIntent().getStringExtra("selectedvalue").toString()
				.equals("Switch")) {
			ArrayList<SwitchModel> linkData = new GetDataFrmDB()
					.getAllSwitchResult(mDbHelper);
			GetAllSwitchSpinnerDataListAdapter cm = new GetAllSwitchSpinnerDataListAdapter(
					mcon, linkData);
			sp_name.setAdapter(cm);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.submit:
			if (getIntent().getStringExtra("selectedvalue").toString()
					.equals("Category")) {
				if (id.equals("")) {
					Toast.makeText(SelectLevel.this, "Please select category.",
							2000).show();
				} else {
					Intent intent = new Intent(SelectLevel.this,
							AddCategory.class);
					intent.putExtra("itemid", "" + id);
					intent.putExtra("title", "Edit");
					intent.putExtra("selectedvalue",
							getIntent().getStringExtra("selectedvalue"));
					startActivity(intent);
					finish();
				}
			}
			if (getIntent().getStringExtra("selectedvalue").toString()
					.equals("Device")) {
				if (id.equals("")) {
					Toast.makeText(SelectLevel.this, "Please select device.",
							2000).show();
				} else {
					Intent intent = new Intent(SelectLevel.this,
							AddDevice.class);
					intent.putExtra("itemid", "" + id);
					intent.putExtra("title", "Edit");
					intent.putExtra("selectedvalue",
							getIntent().getStringExtra("selectedvalue"));
					startActivity(intent);
					finish();
				}
			}
			
			if (getIntent().getStringExtra("selectedvalue").toString()
					.equals("Action")) {
				if (id.equals("")) {
					Toast.makeText(SelectLevel.this, "Please select action.",
							2000).show();
				} else {
					Intent intent = new Intent(SelectLevel.this,
							AddAction.class);
					intent.putExtra("itemid", "" + id);
					intent.putExtra("title", "Edit");
					intent.putExtra("selectedvalue",
							getIntent().getStringExtra("selectedvalue"));
					startActivity(intent);
					finish();
				}
			}if (getIntent().getStringExtra("selectedvalue").toString()
					.equals("Link")) {
				if (id.equals("")) {
					Toast.makeText(SelectLevel.this, "Please select Link.",
							2000).show();
				} else {
					Intent intent = new Intent(SelectLevel.this,
							AddLink.class);
					intent.putExtra("itemid", "" + id);
					intent.putExtra("title", "Edit");
					intent.putExtra("selectedvalue",
							getIntent().getStringExtra("selectedvalue"));
					startActivity(intent);
					finish();
				}
			}	
			if (getIntent().getStringExtra("selectedvalue").toString()
					.equals("Switch")) {
				if (id.equals("")) {
					Toast.makeText(SelectLevel.this, "Please select Switch.",
							2000).show();
				} else {
					Intent intent = new Intent(SelectLevel.this,
							AddSwitch.class);
					intent.putExtra("itemid", "" + id);
					intent.putExtra("title", "Edit");
					intent.putExtra("selectedvalue",
							getIntent().getStringExtra("selectedvalue"));
					startActivity(intent);
					finish();
				}
			}
			
			break;

		case R.id.back:
			Intent intent = new Intent(SelectLevel.this, AddEdit.class);
			intent.putExtra("title", "Edit");
			intent.putExtra("selectedvalue",
					getIntent().getStringExtra("selectedvalue"));
			startActivity(intent);
			finish();

			break;

		}
	}

	public void spinnersetup() {

		sp_name.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				id = ((TextView) v.findViewById(R.id.tv_id)).getText()
						.toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(SelectLevel.this, AddEdit.class);
		intent.putExtra("title", "Edit");
		intent.putExtra("selectedvalue",
				getIntent().getStringExtra("selectedvalue"));
		startActivity(intent);
		finish();

	}

}

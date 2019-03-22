package menuapp.activity;

import java.util.ArrayList;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.adapter.GetAllCategoryDataListAdapter;
import menuapp.activity.util.model.AppModel;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectCategory extends Activity implements OnClickListener {
	DbAdapter mDbHelper;
	Context mcon;
	ListView lv;
	SharedPreferencesManager spm;
	TextView txt;
	ImageView back;
	// Button next;
	String cat_id = "";
	GetAllCategoryDataListAdapter cm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.only_view_cat);
		mcon = this;
		mDbHelper = new DbAdapter(this);
		spm = new SharedPreferencesManager(mcon);

		mDbHelper.open();

		lv = (ListView) findViewById(R.id.cat_list);
		txt = (TextView) findViewById(R.id.txt);
		back = (ImageView) findViewById(R.id.back);
		// next = (Button) findViewById(R.id.next);

		back.setOnClickListener(this);
		// next.setOnClickListener(this);

		viewsetup();
	}

	private void viewsetup() {
		ArrayList<AppModel> CatData = new GetDataFrmDB()
				.getAllCategoryResult(mDbHelper);

		if (getIntent().getStringExtra("selectedvalue").equals("Category")) {
			AppModel amadd = new AppModel();
			amadd.setId("0");
			amadd.setDesc("");
			amadd.setName("root");
			amadd.setNest_id(0);
			CatData.add(amadd);
		}

		if (CatData.size() > 0) {
			txt.setVisibility(View.GONE);
			lv.setVisibility(View.VISIBLE);
			cm = new GetAllCategoryDataListAdapter(mcon, CatData);
			lv.setAdapter(cm);
			listViewClick();
		} else {
			txt.setVisibility(View.VISIBLE);
			lv.setVisibility(View.GONE);
		}

	}

	private void listViewClick() {
		// TODO Auto-generated method stub
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				view.setSelected(true);
				TextView tv1 = (TextView) view.findViewById(R.id.tv_id);
				cat_id = tv1.getText().toString();
				System.out.println("cat_id:..." + cat_id);
				spm.saveStringValues("add_cat_id", cat_id);
				if (getIntent().getStringExtra("selectedvalue").equals(
						"Category")) {
					Intent intent = new Intent(SelectCategory.this,
							AddCategory.class);
					intent.putExtra("itemid", "0");
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", "Add");
					startActivity(intent);
					finish();
				}
				if (getIntent().getStringExtra("selectedvalue")
						.equals("Device")) {
					Intent intent = new Intent(SelectCategory.this,
							AddDevice.class);
					intent.putExtra("itemid", "0");
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", "Add");
					startActivity(intent);
					finish();
				}
				if (getIntent().getStringExtra("selectedvalue")
						.equals("Action")) {
					Intent intent = new Intent(SelectCategory.this,
							AddAction.class);
					intent.putExtra("itemid", "0");
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", "Add");
					startActivity(intent);
					finish();
				}

				if (getIntent().getStringExtra("selectedvalue").equals("Link")) {
					Intent intent = new Intent(SelectCategory.this,
							AddLink.class);
					intent.putExtra("itemid", "0");
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));

					intent.putExtra("title", "Add");
					startActivity(intent);
					finish();
				}
				if (getIntent().getStringExtra("selectedvalue").equals("Switch")) {
					Intent intent = new Intent(SelectCategory.this,
							AddSwitch.class);
					intent.putExtra("itemid", "0");
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));

					intent.putExtra("title", "Add");
					startActivity(intent);
					finish();
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.back) {
			if (spm.getStringValues(getString(R.string.P_Cat_id)).equals("")) {
				Intent intent = new Intent(SelectCategory.this, ListCategory.class);				
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(SelectCategory.this, AddEdit.class);
				intent.putExtra("title", "Add");
				intent.putExtra("selectedvalue",
						getIntent().getStringExtra("selectedvalue"));
				startActivity(intent);
				finish();
			}
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(SelectCategory.this, AddEdit.class);
		intent.putExtra("title", "Add");
		intent.putExtra("selectedvalue",
				getIntent().getStringExtra("selectedvalue"));
		startActivity(intent);
		finish();

	}
}

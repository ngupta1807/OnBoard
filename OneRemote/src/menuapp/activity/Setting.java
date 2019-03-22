package menuapp.activity;

import menuapp.activity.database.DbAdapter;
import menuapp.activity.intrface.AlertInterface;
import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.Constants;
import menuapp.activity.service.DeleteLogout;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.util.InternetReachability;
import menuapp.activity.util.SharedPreferencesManager;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Setting extends Activity implements OnClickListener,
		AlertInterface, AsyncTaskCompleteListener {

	Button cat_add, cat_edit, sub_cat_item_add, sub_cat_item_edit,set_photo;
	Button add, edit, registor, login, logout;
	// sub_cat_edit,sub_cat_add
	SharedPreferencesManager spm;
	Button imprt, export;
	DbAdapter mDbHelper;
	InternetReachability ir;
	CustomAlertDialog am;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		dbSetup(this);

		spm = new SharedPreferencesManager(this);
		ir = new InternetReachability(this);
		am = new CustomAlertDialog(this);
		add = (Button) findViewById(R.id.add);
		edit = (Button) findViewById(R.id.edit);

		imprt = (Button) findViewById(R.id.imprt);
		export = (Button) findViewById(R.id.export);

		registor = (Button) findViewById(R.id.registor);
		login = (Button) findViewById(R.id.login);
		logout = (Button) findViewById(R.id.logout);
		set_photo = (Button) findViewById(R.id.set_photo);
		
		System.out.println("token:.." + spm.getStringValues(Constants.Token));
		if (spm.getStringValues(Constants.Token).equals("0")) {
			registor.setVisibility(View.VISIBLE);
			login.setVisibility(View.VISIBLE);
			logout.setVisibility(View.GONE);
		} else {
			registor.setVisibility(View.GONE);
			login.setVisibility(View.GONE);
			logout.setVisibility(View.VISIBLE);
		}

		add.setOnClickListener(this);
		edit.setOnClickListener(this);
		imprt.setOnClickListener(this);
		export.setOnClickListener(this);
		registor.setOnClickListener(this);
		login.setOnClickListener(this);
		logout.setOnClickListener(this);
		set_photo.setOnClickListener(this);
	}

	private void dbSetup(Context con) {
		// TODO Auto-generated method stub
		mDbHelper = new DbAdapter(con);
		mDbHelper.open();
	}

	@Override
	public void onClick(View v) {
		spm.saveStringValues("menu", "setting");
		if (v.getId() == R.id.add) {
			Intent intent = new Intent(Setting.this, AddEdit.class);
			intent.putExtra("title", "Add");
			startActivity(intent);
			finish();
		}
		if (v.getId() == R.id.edit) {
			Intent intent = new Intent(Setting.this, AddEdit.class);
			intent.putExtra("title", "Edit");
			startActivity(intent);
			finish();
		}
		if (v.getId() == R.id.imprt) {
			CustomAlertDialog cd = new CustomAlertDialog(Setting.this, this);
			cd.CustomzippedDialog();
		}
		if (v.getId() == R.id.export) {
			CustomAlertDialog cd = new CustomAlertDialog(Setting.this, this);
			cd.CustomFolderDialog(mDbHelper);
		}
		if (v.getId() == R.id.registor) {
			Intent intent = new Intent(Setting.this, Registor.class);
			startActivity(intent);
			finish();
		}
		if (v.getId() == R.id.login) {
			Intent intent = new Intent(Setting.this, Login.class);
			startActivity(intent);
			finish();
		}
		if (v.getId() == R.id.logout) {
			if (ir.isConnected()) {
				DeleteLogout Pdr = new DeleteLogout(Setting.this, Setting.this,
						Constants.logout);

				Pdr.execute();
			} else {
				am.showValidate(getString(R.string.internet_error), true);
			}
		}
		if (v.getId() == R.id.set_photo) {
			Intent intent = new Intent(Setting.this, ChooseOption.class);
			intent.putExtra("title", "Add");
			startActivity(intent);
			//finish();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (spm.getStringValues("level").equals("level1")) {
			Intent intent = new Intent(Setting.this, ListCategory.class);
			startActivity(intent);
			finish();
		} else {
			Intent intent = new Intent(Setting.this, ListSubCategory.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public void selected(String action) {
		if (action.equals("ok")) {
			Intent intent = new Intent(Setting.this, ListCategory.class);
			startActivity(intent);
			finish();
		}
		else if(action.equals("finish")){
			Intent intent = new Intent(Setting.this, SelectCategory.class);
			intent.putExtra("selectedvalue", "Link");
			startActivity(intent);
			finish();
		}
		
	}

	@Override
	public void onTaskComplete(String result, int code) {
		if (result.equals("error")) {
			am.showValidate(getString(R.string.server_error), true);
		}
		else if(code == 404){
			registor.setVisibility(View.VISIBLE);
			login.setVisibility(View.VISIBLE);
			logout.setVisibility(View.GONE);
		}
		else if (code == 200) {
			registor.setVisibility(View.VISIBLE);
			login.setVisibility(View.VISIBLE);
			logout.setVisibility(View.GONE);
		} else {
			try {
				JSONObject obj = new JSONObject(result);
				JSONArray ar = new JSONArray(obj.getString("errors"));
				am.showValidate(ar.get(0) + "", true);
			} catch (Exception ex) {
				System.out.println("Error:.." + ex.getMessage());
			}
		}

	}

}

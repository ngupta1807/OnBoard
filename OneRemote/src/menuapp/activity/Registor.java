package menuapp.activity;

import org.json.JSONArray;

import org.json.JSONObject;

import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.Constants;
import menuapp.activity.service.PostLogin;
import menuapp.activity.service.PostRegistor;
import menuapp.activity.setdata.SetData;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.util.InternetReachability;
import menuapp.activity.validation.Validate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Registor extends Activity implements OnClickListener,
		AsyncTaskCompleteListener {
	EditText email, pwd, c_pwd;
	Validate vd;
	CustomAlertDialog am;
	InternetReachability ir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registor);
		vd = new Validate(this);
		ImageView back = (ImageView) findViewById(R.id.back);
		am = new CustomAlertDialog(this);
		ir = new InternetReachability(this);
		Button registor = (Button) findViewById(R.id.registor);
		email = (EditText) findViewById(R.id.email);
		pwd = (EditText) findViewById(R.id.pwd);
		c_pwd = (EditText) findViewById(R.id.c_pwd);

		back.setOnClickListener(this);
		registor.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.registor) {
			Boolean val = vd.reg(email.getText().toString().trim(), pwd.getText()
					.toString(), c_pwd.getText().toString());
			if (val.equals(false)) {
				if (ir.isConnected()) {
					String data = SetData.reg(Registor.this, email.getText()
							.toString().trim(), pwd.getText().toString(), c_pwd
							.getText().toString());
					PostRegistor Pdr = new PostRegistor(Registor.this,
							Registor.this, data, Constants.reg);

					Pdr.execute();
				} else {
					am.showValidate(getString(R.string.internet_error), true);
				}
			}

		}
		if (v.getId() == R.id.back) {
			Intent intent = new Intent(Registor.this, Setting.class);
			startActivity(intent);
			finish();
		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Registor.this, Setting.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onTaskComplete(String result, int code) {
		if (result.equals("error")) {
			am.showValidate(getString(R.string.server_error), true);
		} else if (code == 200) {
			Intent intent = new Intent(Registor.this, Setting.class);
			startActivity(intent);
			finish();
		} else {
			try {
				JSONObject obj = new JSONObject(result);
				JSONObject newobj = new JSONObject(obj.getString("errors"));
				JSONArray ar = new JSONArray(newobj.getString("full_messages"));
				am.showValidate(ar.get(0) + "", true);
			} catch (Exception ex) {
				System.out.println("Error:.." + ex.getMessage());
			}
		}
	}

}

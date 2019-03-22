package menuapp.activity;

import org.json.JSONArray;

import org.json.JSONObject;

import menuapp.activity.intrface.AsyncTaskCompleteListener;
import menuapp.activity.intrface.Constants;
import menuapp.activity.service.PostLogin;
import menuapp.activity.setdata.SetData;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.util.InternetReachability;
import menuapp.activity.validation.Validate;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class Login extends Activity implements OnClickListener,
		AsyncTaskCompleteListener {
	EditText email, pwd;
	Validate vd;
	CustomAlertDialog am;
	InternetReachability ir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		vd = new Validate(this);
		am = new CustomAlertDialog(this);
		
		Button login = (Button) findViewById(R.id.login);
		ir = new InternetReachability(this);
		ImageView back = (ImageView) findViewById(R.id.back);
		email = (EditText) findViewById(R.id.email);
		pwd = (EditText) findViewById(R.id.pwd);

		login.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.login) {
			Boolean val = vd.login(email.getText().toString().trim(), pwd.getText()
					.toString());
			if (val.equals(false)) {
				if (ir.isConnected()) {
					
					String query = SetData.login(Login.this, email.getText()
							.toString().trim(), pwd.getText().toString());
					PostLogin Pdr = new PostLogin(Login.this, Login.this, query,
							Constants.login);

					Pdr.execute();
				} else {
					am.showValidate(getString(R.string.internet_error), true);
				}
			}
		}
		if (v.getId() == R.id.back) {
			Intent intent = new Intent(Login.this, Setting.class);
			startActivity(intent);
			finish();

		}

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Login.this, Setting.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onTaskComplete(String result, int code) {
		if (result.equals("error")) {
			am.showValidate(getString(R.string.server_error), true);
		} else if (code == 200) {
			Intent intent = new Intent(Login.this, Setting.class);
			startActivity(intent);
			finish();
		} else {
			try {
				JSONObject obj = new JSONObject(result);
				JSONArray ar = new JSONArray(obj.getString("errors"));
				am.showValidate(ar.get(0) + "", true);
			} catch (Exception ex) {
				System.out.println("Error:.." + ex.getMessage());
				am.showValidate(getString(R.string.api_error), true);
			}
		}

	}
}

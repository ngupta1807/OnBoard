package menuapp.activity;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.util.InternetReachability;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.validation.Validate;

public class ChooseOption extends Activity implements OnClickListener,
		OnCheckedChangeListener {
	EditText email, pwd;
	Validate vd;
	CustomAlertDialog am;
	InternetReachability ir;
	RadioGroup radioGroup;
	RadioButton maxsize, enlarge;
	Button add;
	SharedPreferencesManager spm;
	EditText width, height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose);

		vd = new Validate(this);
		spm = new SharedPreferencesManager(this);
		am = new CustomAlertDialog(this);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);

		maxsize = (RadioButton) findViewById(R.id.maxsize);
		enlarge = (RadioButton) findViewById(R.id.enlarge);

		add = (Button) findViewById(R.id.add);
		width = (EditText) findViewById(R.id.width);
		height = (EditText) findViewById(R.id.height);
		viewSet();
		radioGroup.setOnCheckedChangeListener(this);
		add.setOnClickListener(this);
	}

	public void viewSet() {

		if (spm.getStringValues(getString(R.string.Scalling)).equals("0")) {
			enlarge.setChecked(false);
			maxsize.setChecked(false);
		} else {
			if (spm.getStringValues(getString(R.string.Scalling))
					.equals("size")) {
				maxsize.setChecked(true);
				width.setVisibility(View.VISIBLE);
				height.setVisibility(View.VISIBLE);
				width.setText(spm.getStringValues(getString(R.string.Width)));
				height.setText(spm.getStringValues(getString(R.string.Height)));
			} else {
				enlarge.setChecked(true);
				width.setVisibility(View.GONE);
				height.setVisibility(View.GONE);
			}

		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.add) {
			if (enlarge.isChecked() == false && maxsize.isChecked() == false) {
				am.showValidate("Please choose option.", true);
			} else {
				if (enlarge.isChecked() == true) {
					spm.saveStringValues(getString(R.string.Scalling),
							"enlarge");
					/*Intent intent = new Intent(ChooseOption.this, Setting.class);
					startActivity(intent);*/
					finish();
				} else {
					if (width.getText().toString().equals("")
							|| height.getText().toString().equals("")) {
						am.showValidate("Please add dimensions.", true);
					} else {
						spm.saveStringValues(getString(R.string.Scalling),
								"size");
						spm.saveStringValues(getString(R.string.Width), width
								.getText().toString());
						spm.saveStringValues(getString(R.string.Height), height
								.getText().toString());
						/*Intent intent = new Intent(ChooseOption.this,
							 	Setting.class);
						startActivity(intent);*/
						finish();
					}
				}

			}
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.enlarge:
			width.setVisibility(View.GONE);
			height.setVisibility(View.GONE);
			break;
		case R.id.maxsize:
			width.setVisibility(View.VISIBLE);
			height.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

	}

}
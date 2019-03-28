package menuapp.activity;

import menuapp.activity.util.SharedPreferencesManager;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddEdit extends Activity {
	// Spinner select;
	Button category, device, action, link,swtch;
	String selectedvalue = "";
	SharedPreferencesManager spm;
	TextView title;
	String selected = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_edit);
		// select = (Spinner) findViewById(R.id.select);
		// btnselect = (Button) findViewById(R.id.btnselect);
		category = (Button) findViewById(R.id.category);
		device = (Button) findViewById(R.id.device);
		action = (Button) findViewById(R.id.action);
		link = (Button) findViewById(R.id.link);
		swtch= (Button) findViewById(R.id.swtch);
		title = (TextView) findViewById(R.id.title);

		String tit = getIntent().getStringExtra("title");

		try {
			selected = getIntent().getStringExtra("selectedvalue");
		} catch (Exception ex) {
			selected = "";
		}
		if (selected == null) {
			selected = "";
		}
		title.setText(tit);

		System.out.println("selected:.." + selected);

		if (!selected.equals("")) {
			if (selected.equals("Category")) {
				selectedvalue = "Category";
				category.setBackgroundResource(R.drawable.list_background_active);
				category.setTextColor(Color.BLACK);
			} else if (selected.equals("Device")) {
				selectedvalue = "Device";
				device.setBackgroundResource(R.drawable.list_background_active);
				device.setTextColor(Color.BLACK);
			} else if (selected.equals("Action")) {
				selectedvalue = "Action";
				action.setBackgroundResource(R.drawable.list_background_active);
				action.setTextColor(Color.BLACK);
			} else if (selected.equals("Link")) {
				selectedvalue = "Link";
				link.setBackgroundResource(R.drawable.list_background_active);
				link.setTextColor(Color.BLACK);
			}
			else if (selected.equals("Switch")) {
				selectedvalue = "Switch";
				swtch.setBackgroundResource(R.drawable.list_background_active);
				swtch.setTextColor(Color.BLACK);
			}

		}

		spm = new SharedPreferencesManager(this);

		spm.saveStringValues("menu", "add");

		category.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				category.setBackgroundResource(R.drawable.list_background_active);
				category.setTextColor(Color.BLACK);
				action.setBackgroundResource(R.drawable.list_background);
				action.setTextColor(Color.WHITE);
				device.setBackgroundResource(R.drawable.list_background);
				device.setTextColor(Color.WHITE);
				link.setBackgroundResource(R.drawable.list_background);
				link.setTextColor(Color.WHITE);
				swtch.setBackgroundResource(R.drawable.list_background);
				swtch.setTextColor(Color.WHITE);
				selectedvalue = "Category";
				selectActivity(selectedvalue);
			}
		});

		device.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				device.setBackgroundResource(R.drawable.list_background_active);
				device.setTextColor(Color.BLACK);
				action.setBackgroundResource(R.drawable.list_background);
				action.setTextColor(Color.WHITE);
				category.setBackgroundResource(R.drawable.list_background);
				category.setTextColor(Color.WHITE);
				link.setBackgroundResource(R.drawable.list_background);
				link.setTextColor(Color.WHITE);
				swtch.setBackgroundResource(R.drawable.list_background);
				swtch.setTextColor(Color.WHITE);
				selectedvalue = "Device";
				selectActivity(selectedvalue);
			}
		});

		action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				action.setBackgroundResource(R.drawable.list_background_active);
				action.setTextColor(Color.BLACK);
				device.setBackgroundResource(R.drawable.list_background);
				device.setTextColor(Color.WHITE);
				category.setBackgroundResource(R.drawable.list_background);
				category.setTextColor(Color.WHITE);
				link.setBackgroundResource(R.drawable.list_background);
				link.setTextColor(Color.WHITE);
				swtch.setBackgroundResource(R.drawable.list_background);
				swtch.setTextColor(Color.WHITE);
				selectedvalue = "Action";
				selectActivity(selectedvalue);
			}
		});

		link.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				link.setBackgroundResource(R.drawable.list_background_active);
				link.setTextColor(Color.BLACK);
				action.setBackgroundResource(R.drawable.list_background);
				action.setTextColor(Color.WHITE);
				device.setBackgroundResource(R.drawable.list_background);
				device.setTextColor(Color.WHITE);
				category.setBackgroundResource(R.drawable.list_background);
				category.setTextColor(Color.WHITE);
				swtch.setBackgroundResource(R.drawable.list_background);
				swtch.setTextColor(Color.WHITE);
				selectedvalue = "Link";				
				selectActivity(selectedvalue);
			}
		});

		swtch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				swtch.setBackgroundResource(R.drawable.list_background_active);
				swtch.setTextColor(Color.BLACK);
				action.setBackgroundResource(R.drawable.list_background);
				action.setTextColor(Color.WHITE);
				device.setBackgroundResource(R.drawable.list_background);
				device.setTextColor(Color.WHITE);
				category.setBackgroundResource(R.drawable.list_background);
				category.setTextColor(Color.WHITE);
				link.setBackgroundResource(R.drawable.list_background);
				link.setTextColor(Color.WHITE);
				selectedvalue = "Switch";
				selectActivity(selectedvalue);
			}
		});

		/*
		 * btnselect.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub System.out.println("selectedvalue:.." + selectedvalue);
		 * 
		 * 
		 * } });
		 */

	}

	public void selectActivity(String selectedvalue) {

		if (title.getText().equals("Add")) {
			if (selectedvalue.equals("Category")) {
				Intent intent = new Intent(AddEdit.this, SelectCategory.class);
				intent.putExtra("itemid", "0");
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			} else if (selectedvalue.equals("Device")) {
				spm.saveStringValues("level3", "setting");
				/*
				 * Intent intent = new
				 * Intent(AddEdit.this,AddSubCategoryItem.class);
				 */
				Intent intent = new Intent(AddEdit.this, SelectCategory.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			} else if (selectedvalue.equals("Action")) {
				spm.saveStringValues("level3", "setting");
				/*
				 * Intent intent = new Intent(AddEdit.this, AddAction.class);
				 */
				Intent intent = new Intent(AddEdit.this, SelectCategory.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			} else if (selectedvalue.equals("Link")) {
				spm.saveStringValues("level3", "setting");

				Intent intent = new Intent(AddEdit.this, SelectCategory.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			}
			 else if (selectedvalue.equals("Switch")) {
					spm.saveStringValues("level3", "setting");
					Intent intent = new Intent(AddEdit.this, SelectCategory.class);
					intent.putExtra("selectedvalue", selectedvalue);
					startActivity(intent);
					finish();
				}
			else {
				Toast.makeText(AddEdit.this, "Please Choose Type.", 2000)
						.show();
			}
		}
		if (title.getText().equals("Edit")) {
			if (selectedvalue.equals("Category")) {
				Intent intent = new Intent(AddEdit.this, SelectLevel.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			}

			else if (selectedvalue.equals("Device")) {
				Intent intent = new Intent(AddEdit.this, SelectLevel.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			}

			else if (selectedvalue.equals("Action")) {
				Intent intent = new Intent(AddEdit.this, SelectLevel.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			} else if (selectedvalue.equals("Link")) {
				Intent intent = new Intent(AddEdit.this, SelectLevel.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			}
			else if (selectedvalue.equals("Switch")) {
				Intent intent = new Intent(AddEdit.this, SelectLevel.class);
				intent.putExtra("selectedvalue", selectedvalue);
				startActivity(intent);
				finish();
			}
			else {
				Toast.makeText(AddEdit.this, "Please Choose Type.", 2000)
						.show();
			}
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent(AddEdit.this, Setting.class);
		startActivity(intent);
		finish();

	}
}

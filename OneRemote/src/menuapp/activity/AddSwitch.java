package menuapp.activity;

import java.io.File;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import menuapp.activity.intrface.AlertInterface;
import menuapp.activity.intrface.ImageInterface;
import menuapp.activity.setdata.ToggleRadioButton;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.GetPosition;
import menuapp.activity.util.ImageCompressing;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.adapter.GetAllCategorySpinnerDataListAdapter;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.SubCatItemModel;
import menuapp.activity.util.model.SwitchModel;
import menuapp.activity.util.txtdata.TXTGenerator;
import menuapp.activity.util.xmldata.XMLGenerator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddSwitch extends Activity implements OnClickListener,
		AlertInterface, ImageInterface {

	Button submit, btn_browse, delete, next;
	DbAdapter mDbHelper;
	EditText ed_sub_cat_item_name, link, link2, data;
	Spinner sp_cat_name;
	Context mcon;
	SharedPreferencesManager spm;
	String cat_id = "";
	TextView pic_path;
	private ArrayList<String> subCatImgsPath = new ArrayList<String>();
	int ViewClicked;
	ImageView pic, back;
	TextView title;
	SubCatItemModel model;
	String cameratype = "";
	int editclick = 0;
	String path = "";
	CheckBox showhide;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_switch);

		mcon = AddSwitch.this;

		spm = new SharedPreferencesManager(mcon);

		if (savedInstanceState != null) {
			ViewClicked = savedInstanceState.getInt("viewCicked");

			file_name = savedInstanceState.getString("file_name");

			uriSavedImage = savedInstanceState.getParcelable("img_uri");
		}

		pic_path = (TextView) findViewById(R.id.pic_path);

		submit = (Button) findViewById(R.id.submit);

		btn_browse = (Button) findViewById(R.id.btn_browse);
		delete = (Button) findViewById(R.id.delete);
		next = (Button) findViewById(R.id.next);

		showhide = (CheckBox) findViewById(R.id.showhide);

		ed_sub_cat_item_name = (EditText) findViewById(R.id.ed_sub_cat_item_name);

		sp_cat_name = (Spinner) findViewById(R.id.sp_cat_name);

		title = (TextView) findViewById(R.id.title);

		pic = (ImageView) findViewById(R.id.pic);

		back = (ImageView) findViewById(R.id.back);

		link = (EditText) findViewById(R.id.link);

		link2 = (EditText) findViewById(R.id.link2);

		data = (EditText) findViewById(R.id.data);

		System.out.println("In append...");

	
		dbSetup();
		screensetup();
		spinnersetup();
		appendEditableItems();

		submit.setOnClickListener(this);
		// btn_upload_icon.setOnClickListener(this);
		btn_browse.setOnClickListener(this);

		back.setOnClickListener(this);
		delete.setOnClickListener(this);
		next.setOnClickListener(this);
		showhide.setOnClickListener(this);
		
		if (!spm.getStringValues("tv_id").equals("")) {
			ed_sub_cat_item_name.setText(spm.getStringValues("tv_id"));
		}
		if (!spm.getStringValues("tv_url").equals("")) {
			link.setText(spm.getStringValues("tv_url"));
		}

		link.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					if (link.getText().length() > 0) {
						link2.setVisibility(View.VISIBLE);
					}
				}

			}
		});

	}

	private void dbSetup() {
		mDbHelper = new DbAdapter(this);

		mDbHelper.open();

	}

	public void screensetup() {
		sp_cat_name.setVisibility(View.VISIBLE);
		ArrayList<AppModel> CatData = new GetDataFrmDB()
				.getAllCategoryResult(mDbHelper);

		GetAllCategorySpinnerDataListAdapter cm = new GetAllCategorySpinnerDataListAdapter(
				mcon, CatData);
		sp_cat_name.setAdapter(cm);

		if (getIntent().getExtras().getString("itemid").equals("0")) {
			title.setText("Switch");
			delete.setVisibility(View.GONE);
			sp_cat_name.setVisibility(View.GONE);
			cat_id = spm.getStringValues("add_cat_id");
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		} else {
			title.setText("Switch");
			delete.setVisibility(View.VISIBLE);
		}
		if (spm.getStringValues("menu").contains("click")) {
			next.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.next: {
			Intent intent = new Intent(AddSwitch.this, ListCategory.class);
			startActivity(intent);
			finish();
		}
			break;
		case R.id.delete: {
			CustomAlertDialog cam = new CustomAlertDialog(AddSwitch.this,
					AddSwitch.this);
			cam.showDialog("Do you want to delete!", false);
		}
			break;
		case R.id.submit:
			spm.saveStringValues("sub_session", "add");
			saveSwitchData();

			break;

		case R.id.btn_browse:

			ViewClicked = 0;

			selectImage();
			break;
		case R.id.showhide:
			System.out.println("On Click"+showhide.isChecked());
			break;
		case R.id.back:
			if (spm.getStringValues("menu").equals("click")) {
				if (spm.getStringValues("level").equals("level1")) {
					// spm.saveStringValues(getString(R.string.P_Cat_id), "");
					Intent intent = new Intent(AddSwitch.this, ListCategory.class);
					startActivity(intent);
					finish();

				} else {
					Intent intent = new Intent(AddSwitch.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
					break;
				}
			} else {
				if (getIntent().getStringExtra("title").equals("Add")) {
					Intent intent = new Intent(AddSwitch.this,
							SelectCategory.class);
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", getIntent()
							.getStringExtra("title"));
					startActivity(intent);
					finish();
				} else if (getIntent().getStringExtra("title").equals("Edit")) {
					Intent intent = new Intent(AddSwitch.this, SelectLevel.class);
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", getIntent()
							.getStringExtra("title"));
					startActivity(intent);
					finish();
				}
			}
		}
	}

	private void selectImage() {
		/*
		 * final CharSequence[] items = { "Take Photo", "Choose from Library",
		 * "Cancel" }; AlertDialog.Builder builder = new
		 * AlertDialog.Builder(mcon); builder.setTitle("Add Photo!");
		 * builder.setItems(items, new DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int item) { if
		 * (items[item].equals("Take Photo")) { cameratype = "take";
		 * openNativeCamera(); } else if
		 * (items[item].equals("Choose from Library")) { cameratype = "gallery";
		 * openGallery(); } else if (items[item].equals("Cancel")) {
		 * dialog.dismiss(); } } }); builder.show();
		 */
		CustomAlertDialog cad = new CustomAlertDialog(mcon, AddSwitch.this,
				"image");
		cad.CustomImageDialog();

	}

	public void spinnersetup() {

		sp_cat_name.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				cat_id = ((TextView) v.findViewById(R.id.tv_id)).getText()
						.toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	static final int REQUEST_TAKE_PHOTO = 1;

	static final int REQUEST_CHOOSE_PHOTO = 2;

	String file_name;

	File sdIconStorageDir;

	public Uri uriSavedImage;

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);

		bundle.putParcelable("img_uri", uriSavedImage);

		bundle.putString("file_name", file_name);

		bundle.putInt("viewCicked", ViewClicked);
	}

	private void openGallery() {

		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		i.setType("image/*");

		startActivityForResult(i, REQUEST_CHOOSE_PHOTO);

	}

	private void openNativeCamera() {
		Intent cameraIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

		String iconsStoragePath = new GetPathForImage()
				.getBasePath(AddSwitch.this);

		sdIconStorageDir = new File(iconsStoragePath);

		if (!sdIconStorageDir.exists()) {
			sdIconStorageDir.mkdirs();
		}
		file_name = "Oneremote_" + System.currentTimeMillis() + ".png";

		File file = new File(sdIconStorageDir, file_name);

		file.setWritable(true);

		if (file.exists())
			file.delete();

		uriSavedImage = Uri.fromFile(file);

		cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

		startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_TAKE_PHOTO) {
				if (ViewClicked == 0) {
					pic_path.setText(uriSavedImage.toString());

					Bitmap bit = appendImages(pic_path.getText().toString(),
							pic, "add");

					new ImageCompressing().saveImage(pic_path.getText()
							.toString(), bit, AddSwitch.this, file_name);

					pic.setVisibility(View.VISIBLE);

				}
			}
			if (requestCode == REQUEST_CHOOSE_PHOTO) {
				if (ViewClicked == 0) {
					Uri selectedImage = data.getData();

					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();
					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String filePath = cursor.getString(columnIndex);
					cursor.close();
					try {

						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inPreferredConfig = Bitmap.Config.ARGB_8888;

						path = createDirectoryAndSaveFile(
								BitmapFactory.decodeFile(filePath), filePath);
						// copyFile(filePath);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("Get filePath:.." + path);

					pic_path.setText(path);

					appendImages(path, pic, "add");

					pic.setVisibility(View.VISIBLE);
				}
			}
		} else if (resultCode == RESULT_CANCELED) {
		}
	}

	private String createDirectoryAndSaveFile(Bitmap imageToSave,
			String fileName) {
		String file_path = "";
		String newpath = fileName.substring(fileName.lastIndexOf("/") + 1,
				fileName.length());

		File direct = new File(spm.getStringValues("basefolder") + "/Photos");

		System.out.println("direct:.." + direct);

		if (!direct.exists()) {
			File Directory = new File(
					new GetPathForImage().getBasePathForCopy(AddSwitch.this));

			Directory.mkdirs();
		}

		File file;
		file = new File(direct, newpath);

		System.out.println("file:.." + file);
		file_path = "" + file;
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			imageToSave.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return file_path;
	}

	public Bitmap appendImages(String path, ImageView img, String value) {
		Bitmap bitmap = null;
		path = path.substring(path.lastIndexOf("/") + 1, path.length());

		String iconsStoragePath = new GetPathForImage()
				.getBasePath(AddSwitch.this);

		File imgFile = new File(iconsStoragePath, path);

		System.out.println("Image path.." + imgFile);

		if (imgFile.exists() && imgFile.isFile()) {
			try {
				BitmapFactory.Options bm = new BitmapFactory.Options();
				bitmap = BitmapFactory
						.decodeFile(imgFile.getAbsolutePath(), bm);
				System.out.println("add Width 1:" + bitmap.getWidth());
				System.out.println("add height 1:" + bitmap.getHeight());
				if (!value.equals("update")) {
					try {
						bitmap = new ImageCompressing().decodeFile(imgFile,
								mcon);
					} catch (Exception ex) {
						bitmap = BitmapFactory.decodeFile(
								imgFile.getAbsolutePath(), bm);
					}
				}
				System.out.println("add Width:" + bitmap.getWidth());
				System.out.println("add height:" + bitmap.getHeight());

				Matrix matrix = new Matrix();

				ExifInterface exif = new ExifInterface(
						imgFile.getAbsolutePath());

				int orientation = exif.getAttributeInt(
						ExifInterface.TAG_ORIENTATION,
						ExifInterface.ORIENTATION_UNDEFINED);

				Log.v("TAG", "" + orientation);
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					matrix.preRotate(90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					matrix.preRotate(180);
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					matrix.preRotate(270);
					break;
				default:
					break;
				}
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);

				img.setImageBitmap(bitmap);
				editclick = editclick + 1;
				if (ed_sub_cat_item_name.getText().length() > 0
						&& editclick >= 2) {

					saveSwitchData();
				}

			} catch (Exception ex) {
				System.out.println("Error:" + ex.getMessage());
				Toast.makeText(
						AddSwitch.this,
						"Some thing went wrong while loading image from gallery.",
						2000).show();
			}
		} else
			System.out.println("Not Found");
		return bitmap;
	}

	private void appendEditableItems() {
		if (!getIntent().getExtras().getString("itemid").equals("0")) {
			SwitchModel scm;
			
			Log.d("","Switch item id inside:"+getIntent().getExtras().getString("itemid"));
			ArrayList<SwitchModel> devicelist = new GetDataFrmDB()
					.getAllSwitchItemResult(
							mDbHelper,
							Integer.parseInt(getIntent().getStringExtra(
									"itemid")));
			for (int i = 0; i < devicelist.size(); i++) {
				scm = (SwitchModel) devicelist.get(i);

				ed_sub_cat_item_name.append(scm.getKEY_SWITCH_BODY());

				pic_path.setText(scm.getKEY_SWITCH_PIC());

				if (scm.getKEY_SWITCH_LINK().contains("?:")) {
					System.out.println("Switch split:..."
							+ scm.getKEY_SWITCH_LINK());
					String lk2[] = scm.getKEY_SWITCH_LINK().toString()
							.split(Pattern.quote("?:"));
					link.setText(lk2[0]);
					link2.setVisibility(View.VISIBLE);
					link2.setText(lk2[1]);
				} else {
					link.setText(scm.getKEY_SWITCH_LINK());
				}
				data.setText(scm.getKEY_SWITCH_DATA());
				Boolean status=Boolean.valueOf(scm.getkEY_SWITCH_STATUS());
				showhide.setChecked(status);
				System.out.println("pic path:..."
						+ pic_path.getText().toString());
				if (!pic_path.getText().toString().equals("")) {
					appendImages(pic_path.getText().toString(), pic, "update");

					pic.setVisibility(View.VISIBLE);
				} else {
					pic.setVisibility(View.GONE);
				}

				String cat_name = new GetDataFrmDB().getCatNameByID(
						Integer.parseInt(scm.getKEY_CAT_ID()), mDbHelper);
				int pos = new GetPosition().getCategoryPos(cat_name, mDbHelper);
				sp_cat_name.setSelection(pos);
			}

		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (spm.getStringValues("menu").equals("click")) {
			Intent intent = new Intent(AddSwitch.this, ListCategory.class);
			startActivity(intent);
			finish();
		} else {
			if (getIntent().getStringExtra("title").equals("Add")) {
				Intent intent = new Intent(AddSwitch.this, SelectCategory.class);
				intent.putExtra("selectedvalue",
						getIntent().getStringExtra("selectedvalue"));
				intent.putExtra("title", getIntent().getStringExtra("title"));
				startActivity(intent);
				finish();
			} else if (getIntent().getStringExtra("title").equals("Edit")) {
				Intent intent = new Intent(AddSwitch.this, SelectLevel.class);
				intent.putExtra("selectedvalue",
						getIntent().getStringExtra("selectedvalue"));
				intent.putExtra("title", getIntent().getStringExtra("title"));
				startActivity(intent);
				finish();
			}
		}
	}

	@Override
	public void selected(String action) {
		if (action.equals("delete")) {
			int id = Integer.parseInt(getIntent().getExtras().getString(
					"itemid"));
			mDbHelper.deleteSwitchItem(id);

			new XMLGenerator().generateSwitchXMLFile(
					new GetDataFrmDB().getAllSwitchResult(mDbHelper),
					AddSwitch.this);

			new TXTGenerator().generateSwitchTXTFile(
					new GetDataFrmDB().getAllSwitchResult(mDbHelper), mDbHelper,
					AddSwitch.this);

			if (spm.getStringValues("level").equals("level1")) {
				// spm.saveStringValues(getString(R.string.P_Cat_id), "");
				Intent intent = new Intent(AddSwitch.this, ListCategory.class);
				startActivity(intent);
				finish();

			} else {
				Intent intent = new Intent(AddSwitch.this, ListSubCategory.class);
				startActivity(intent);
				finish();
			}
		} else {

		}

	}

	private void saveSwitchData() {
		Boolean st = false;
		if (cat_id.equals("0") || cat_id.equals("")) {
			Toast.makeText(getApplicationContext(), "Please add Category.",
					2000).show();
		} else if (ed_sub_cat_item_name.getText().toString().equals("")) {
			Toast.makeText(getApplicationContext(), "Please add Device.", 2000)
					.show();
		} else {
			if (!path.equals("")) {
				pic_path.setText(path);
			}
			if (pic_path.getText().toString().equals("")) {
				String name = ed_sub_cat_item_name.getText().toString()
						.replace(" ", "");
				String imagename = new GetPathForImage().getAllimages(name
						+ ".png", AddSwitch.this);

				if (!imagename.equals("")) {
					File direct = new File(spm.getStringValues("basefolder")
							+ "/Photos");

					System.out.println("direct:.." + direct);
					File file = new File(direct, imagename);
					pic_path.setText("" + file);
				}
			}
			if (!getIntent().getExtras().getString("itemid").equals("0")) {

				int id = Integer.parseInt(getIntent().getExtras().getString(
						"itemid"));

				int catid = Integer.parseInt(cat_id);

				String path = new GetDataFrmDB().getPathByCat_id(catid,
						mDbHelper);

				System.out.println("path:.." + path);
				System.out.println("catid:.." + catid);
				System.out.println("row_id:.." + id);
				System.out.println("pic_path:.."
						+ pic_path.getText().toString());
				if (link2.getText().toString().equals("")) {
					mDbHelper.updateSwitchItem(id, catid, path,
							ed_sub_cat_item_name.getText().toString(), pic_path
									.getText().toString(), link.getText()
									.toString(), data.getText().toString(),""+showhide.isChecked());
				} else {
					mDbHelper.updateSwitchItem(id, catid, path,
							ed_sub_cat_item_name.getText().toString(), pic_path
									.getText().toString(), link.getText()
									.toString()
									+ "?:"
									+ link2.getText().toString(), data
									.getText().toString(),""+showhide.isChecked());
				}

				new XMLGenerator().generateSwitchXMLFile(
						new GetDataFrmDB().getAllSwitchResult(mDbHelper),
						AddSwitch.this);

				new TXTGenerator().generateSwitchTXTFile(
						new GetDataFrmDB().getAllSwitchResult(mDbHelper),
						mDbHelper, AddSwitch.this);

				if (spm.getStringValues("level").equals("level1")) {
					// spm.saveStringValues(getString(R.string.P_Cat_id), "");
					Log.v("", "Level 1 if case");
					Intent intent = new Intent(AddSwitch.this, ListCategory.class);
					startActivity(intent);
					finish();
					/*Intent intent=new Intent();
                    setResult(RESULT_OK, intent);
                    finish();*/
				} else {
					System.out.println("switch calling");
					Log.v("", "Level 1 else case");
					
					Intent intent = new Intent(AddSwitch.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
					/*Intent intent=new Intent();
		            setResult(RESULT_OK, intent);
		            finish();*/
				}

			} else {
				int catid = Integer.parseInt(cat_id);
				String path = new GetDataFrmDB().getPathByCat_id(catid,
						mDbHelper);

				if (link2.getText().toString().equals("")) {
					mDbHelper.createSwitchItem(catid, path, ed_sub_cat_item_name
							.getText().toString(), pic_path.getText()
							.toString(), link.getText().toString(), data
							.getText().toString(),""+showhide.isChecked());
				} else {
					mDbHelper.createSwitchItem(catid, path, ed_sub_cat_item_name
							.getText().toString(), pic_path.getText()
							.toString(), link.getText().toString() + "?:"
							+ link2.getText().toString(), data.getText()
							.toString(),""+showhide.isChecked());
				}
				System.out.println("Saved data...");
				new XMLGenerator().generateSwitchXMLFile(
						new GetDataFrmDB().getAllSwitchResult(mDbHelper),
						AddSwitch.this);
				System.out.println("Saved XMLGenerator...");
				new TXTGenerator().generateSwitchTXTFile(
						new GetDataFrmDB().getAllSwitchResult(mDbHelper),
						mDbHelper, AddSwitch.this);
				System.out.println("Saved TXTGenerator...");
				if (spm.getStringValues("level").equals("level1")) {
					
					Log.v("", "Level 2 if case");
					Intent intent = new Intent(AddSwitch.this, ListCategory.class);
					startActivity(intent);
					finish();/*
					Intent intent=new Intent();
		            setResult(RESULT_OK, intent);
		            finish();*/
				} else {
					Log.v("", "Level 2 else case");
					Intent intent = new Intent(AddSwitch.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
					/*Intent intent=new Intent();
		             setResult(RESULT_OK, intent);
		             finish();*/
				}
			}
		}

	}

	@Override
	public void Pvalue(String action) {
		if (action.equals("Photo")) {
			cameratype = "take";
			openNativeCamera();
		} else if (action.equals("Gallery")) {
			cameratype = "gallery";
			openGallery();
		} else {
		}

	}


}

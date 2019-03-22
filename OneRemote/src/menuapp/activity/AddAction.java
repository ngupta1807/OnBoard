package menuapp.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.intrface.AlertInterface;
import menuapp.activity.intrface.AsyncTaskCompletePackageListener;
import menuapp.activity.intrface.ImageInterface;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.util.GetListOfInstalledApplication;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.GetPosition;
import menuapp.activity.util.ImageCompressing;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.adapter.GetAllCategorySpinnerDataListAdapter;
import menuapp.activity.util.adapter.GetAllInstalledAppSpinnerDataListAdapter;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.PackageItem;
import menuapp.activity.util.model.SubCatItemModel;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddAction extends Activity implements OnClickListener,
		AlertInterface, AsyncTaskCompletePackageListener,ImageInterface {

	Button submit, btn_browse, delete, next;
	DbAdapter mDbHelper;
	Spinner sp_cat_name, sp_action_name;
	Context mcon;
	SharedPreferencesManager spm;
	String cat_id = "";
	TextView pic_path;
	// menuapp.activity.FlowLayout subCatImgs;

	int ViewClicked;
	ImageView pic, back;
	TextView title;
	SubCatItemModel model;
	String cameratype = "";
	String action_name = "";

	int editclick = 0;
	String path = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_action);

		mcon = AddAction.this;

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

		sp_cat_name = (Spinner) findViewById(R.id.sp_cat_name);

		sp_action_name = (Spinner) findViewById(R.id.sp_action_name);

		title = (TextView) findViewById(R.id.title);

		pic = (ImageView) findViewById(R.id.pic);

		back = (ImageView) findViewById(R.id.back);

		// appendEditableItems();

		dbSetup();
		screensetup();
		spinnersetup();
		submit.setOnClickListener(this);

		btn_browse.setOnClickListener(this);

		back.setOnClickListener(this);
		delete.setOnClickListener(this);
		next.setOnClickListener(this);
	}

	private void dbSetup() {
		mDbHelper = new DbAdapter(this);

		mDbHelper.open();

	}

	public void screensetup() {

		sp_cat_name.setVisibility(View.VISIBLE);
		if (getIntent().getExtras().getString("itemid").equals("0")) {
			title.setText("App");
			delete.setVisibility(View.GONE);
			sp_cat_name.setVisibility(View.GONE);
			cat_id = spm.getStringValues("add_cat_id");
		} else {
			title.setText("App");
			delete.setVisibility(View.VISIBLE);
		}
		if (spm.getStringValues("menu").contains("click")) {
			next.setVisibility(View.GONE);
		}

		ArrayList<AppModel> CatData = new GetDataFrmDB()
				.getAllCategoryResult(mDbHelper);

		GetAllCategorySpinnerDataListAdapter cm = new GetAllCategorySpinnerDataListAdapter(
				mcon, CatData);
		sp_cat_name.setAdapter(cm);

		GetListOfInstalledApplication gla = new GetListOfInstalledApplication(
				AddAction.this, AddAction.this);
		gla.execute();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.next: {
			Intent intent = new Intent(AddAction.this, ListCategory.class);
			startActivity(intent);
			finish();
		}
			break;
		case R.id.delete: {
			CustomAlertDialog cam = new CustomAlertDialog(AddAction.this,
					AddAction.this);
			cam.showDialog("Do you want to delete!", false);
		}
			break;
		case R.id.submit:
			spm.saveStringValues("sub_session", "add");

			saveActionData();

			break;

		case R.id.btn_browse:

			ViewClicked = 0;

			selectImage();
			break;

		case R.id.back:
			if (spm.getStringValues("menu").equals("click")) {
				if (spm.getStringValues("level").equals("level1")) {
					// spm.saveStringValues(getString(R.string.P_Cat_id), "");
					Intent intent = new Intent(AddAction.this,
							ListCategory.class);
					startActivity(intent);
					finish();

				} else {
					//finish();
				    Intent intent = new Intent(AddAction.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
				}
			} else {
				if (getIntent().getStringExtra("title").equals("Add")) {
					Intent intent = new Intent(AddAction.this,
							SelectCategory.class);
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", getIntent()
							.getStringExtra("title"));
					startActivity(intent);
					finish();
				} else if (getIntent().getStringExtra("title").equals("Edit")) {
					Intent intent = new Intent(AddAction.this,
							SelectLevel.class);
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", getIntent()
							.getStringExtra("title"));
					startActivity(intent);
					finish();
				}
			}

			/*
			 * if (spm.getStringValues("menu").equals("click")) {
			 * 
			 * spm.saveStringValues(getString(R.string.P_Cat_id), ""); Intent
			 * intent = new Intent(AddAction.this, ListSubCategory.class);
			 * startActivity(intent); finish(); } else { Intent intent = new
			 * Intent(AddAction.this, Setting.class); startActivity(intent);
			 * finish(); }
			 */
			/*
			 * if (spm.getStringValues("menu").equals("click")) { if
			 * (spm.getStringValues("P_Cat_id").equals("")) { Intent intent =
			 * new Intent(AddAction.this, ListCategory.class);
			 * startActivity(intent); finish(); } else {
			 * spm.saveStringValues(getString(R.string.P_Cat_id), ""); Intent
			 * intent = new Intent(AddAction.this, ListSubCategory.class);
			 * startActivity(intent); finish(); }
			 * 
			 * } else { Intent intent = new Intent(AddAction.this,
			 * Setting.class); startActivity(intent); finish(); }
			 */

			break;

		}
	}

	private void selectImage() {
		/*final CharSequence[] items = { "Take Photo", "Choose from Library",
				"Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(mcon);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals("Take Photo")) {
					cameratype = "take";
					openNativeCamera();
				} else if (items[item].equals("Choose from Library")) {
					cameratype = "gallery";
					openGallery();
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();*/
		
		CustomAlertDialog cad = new CustomAlertDialog(mcon, AddAction.this,
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

		sp_action_name.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				action_name = ((TextView) v.findViewById(R.id.tv_name))
						.getText().toString();

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
				.getBasePath(AddAction.this);

		sdIconStorageDir = new File(iconsStoragePath);

		if (!sdIconStorageDir.exists()) {
			sdIconStorageDir.mkdirs();
		}
		file_name = "Oneremote_" + System.currentTimeMillis() + ".png";
		/*
		 * if (action_name.equals("")) {
		 * 
		 * } else { String name = ""; if (action_name.contains(" ")) name =
		 * action_name.replace(" ", ""); else name = action_name; file_name =
		 * name + ".jpg";
		 * 
		 * }
		 */
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
							pic,"add");

					new ImageCompressing().saveImage(pic_path.getText()
							.toString(), bit, AddAction.this, file_name);

					pic.setVisibility(View.VISIBLE);

				}
			}
			if (requestCode == REQUEST_CHOOSE_PHOTO) {
				if (ViewClicked == 0) {
					try {
						Uri selectedImage = data.getData();

						String[] filePathColumn = { MediaStore.Images.Media.DATA };

						Cursor cursor = getContentResolver()
								.query(selectedImage, filePathColumn, null,
										null, null);
						cursor.moveToFirst();

						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);
						String filePath = cursor.getString(columnIndex);
						
						cursor.close();

						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inPreferredConfig = Bitmap.Config.ARGB_8888;

						path = createDirectoryAndSaveFile(
								BitmapFactory.decodeFile(filePath), filePath);

						
						Bitmap bit = appendImages(path, pic,"add");

						String newpath = filePath.substring(
								filePath.lastIndexOf("/") + 1,
								filePath.length());

						file_name = newpath;

						new ImageCompressing().saveImage(path, bit,
								AddAction.this, file_name);

						pic_path.setText(path);

						pic.setVisibility(View.VISIBLE);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						Toast.makeText(
								AddAction.this,
								"Some thing went wrong while loading image from gallery.",
								2000).show();
					}
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


		if (!direct.exists()) {
			File Directory = new File(
					new GetPathForImage().getBasePathForCopy(AddAction.this));

			Directory.mkdirs();
		}

		File file;
		file = new File(direct, newpath);
		/*
		 * if (action_name.equals("")) { file = new File(direct, newpath);
		 * 
		 * } else { String name = ""; if (action_name.contains(" ")) name =
		 * action_name.replace(" ", ""); else name = action_name; file = new
		 * File(direct, name + ".jpg"); }
		 */

	
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
				.getBasePath(AddAction.this);

		File imgFile = new File(iconsStoragePath, path);

		

		if (imgFile.exists() && imgFile.isFile()) {
			try {
				BitmapFactory.Options bm = new BitmapFactory.Options();
				bitmap = BitmapFactory.decodeFile(
						imgFile.getAbsolutePath(), bm);
				
				if (!value.equals("update")) {
					try {
						bitmap = new ImageCompressing().decodeFile(imgFile,mcon);
					} catch (Exception ex) {
						bitmap = BitmapFactory.decodeFile(
								imgFile.getAbsolutePath(), bm);
					}
				}
				
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
				if (action_name.length() > 0 && editclick >= 2) {
					// saveActionData();
				}
			} catch (Exception e) {
				
				Toast.makeText(
						AddAction.this,
						"Some thing went wrong while loading image from gallery.",
						2000).show();
			}

		} else
			System.out.println("Not Found");
		return bitmap;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		if (spm.getStringValues("menu").equals("click")) {
			Intent intent = new Intent(AddAction.this, ListCategory.class);
			startActivity(intent);
			finish();
		} else {
			if (getIntent().getStringExtra("title").equals("Add")) {
				Intent intent = new Intent(AddAction.this, SelectCategory.class);
				intent.putExtra("selectedvalue",
						getIntent().getStringExtra("selectedvalue"));
				intent.putExtra("title", getIntent().getStringExtra("title"));
				startActivity(intent);
				finish();
			} else if (getIntent().getStringExtra("title").equals("Edit")) {
				Intent intent = new Intent(AddAction.this, SelectLevel.class);
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

			mDbHelper.deleteaction(id);

			new XMLGenerator().generateActionXMLFile(
					new GetDataFrmDB().getAllAction(mDbHelper), AddAction.this);

			new TXTGenerator().generateActionTXTFile(
					new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper,
					AddAction.this);

			// spm.saveStringValues(getString(R.string.P_Cat_id), "");
			if (spm.getStringValues("level").equals("level1")) {
				// spm.saveStringValues(getString(R.string.P_Cat_id), "");
				Intent intent = new Intent(AddAction.this, ListCategory.class);
				startActivity(intent);
				finish();

			} else {
				Intent intent = new Intent(AddAction.this,
						ListSubCategory.class);
				startActivity(intent);
				finish();
			}
			/*
			 * if (spm.getStringValues("menu").equals("click")) {
			 * 
			 * spm.saveStringValues(getString(R.string.P_Cat_id), ""); Intent
			 * intent = new Intent(AddAction.this, ListSubCategory.class);
			 * startActivity(intent); finish(); } else { Intent intent = new
			 * Intent(AddAction.this, Setting.class); startActivity(intent);
			 * finish(); }
			 */
		} else {

		}

	}

	private void saveActionData() {
		if (cat_id.equals("0") || cat_id.equals("")) {
			Toast.makeText(getApplicationContext(), "Please add Category.",
					2000).show();
		} else if (action_name.equals("")) {
			Toast.makeText(getApplicationContext(), "Please add action.", 2000)
					.show();
		} else {
			if (!path.equals("")) {
				pic_path.setText(path);
			}
			if (pic_path.getText().toString().equals("")) {
				String name = action_name.replace(" ", "");
				String imagename = new GetPathForImage().getAllimages(name
						+ ".png", AddAction.this);

				if (!imagename.equals("")) {
					File direct = new File(spm.getStringValues("basefolder")
							+ "/Photos");

					
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

				

				mDbHelper.updateaction(id, catid, path, action_name, pic_path
						.getText().toString());

				new XMLGenerator().generateActionXMLFile(
						new GetDataFrmDB().getAllAction(mDbHelper),
						AddAction.this);

				new TXTGenerator().generateActionTXTFile(
						new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper,
						AddAction.this);

				/*
				 * if (spm.getStringValues("menu").equals("click")) {
				 * 
				 * spm.saveStringValues(getString(R.string.P_Cat_id), "");
				 * Intent intent = new Intent(AddAction.this,
				 * ListSubCategory.class); startActivity(intent); finish(); }
				 * else { Intent intent = new Intent(AddAction.this,
				 * Setting.class); startActivity(intent); finish(); }
				 */
				if (spm.getStringValues("level").equals("level1")) {
					// spm.saveStringValues(getString(R.string.P_Cat_id), "");
					Log.v("", "Level 1 if case");
					Intent intent = new Intent(AddAction.this,
							ListCategory.class);
					startActivity(intent);
					finish();
					/*Intent intent=new Intent();
		             setResult(RESULT_OK, intent);
		             finish();*/
				} else {
					//finish();
					Log.v("", "Level 1 else case");
					Intent intent = new Intent(AddAction.this,
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

						mDbHelper.createaction(catid, path, action_name, pic_path
						.getText().toString());

				new XMLGenerator().generateActionXMLFile(
						new GetDataFrmDB().getAllAction(mDbHelper),
						AddAction.this);

				new TXTGenerator().generateActionTXTFile(
						new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper,
						AddAction.this);

				/*
				 * if (spm.getStringValues("menu").equals("click")) {
				 * 
				 * spm.saveStringValues(getString(R.string.P_Cat_id), "");
				 * Intent intent = new Intent(AddAction.this,
				 * ListSubCategory.class); startActivity(intent); finish(); }
				 * else { Intent intent = new Intent(AddAction.this,
				 * Setting.class); startActivity(intent); finish(); }
				 */
				if (spm.getStringValues("level").equals("level1")) {
					Log.v("", "Level 2 if case");
					// spm.saveStringValues(getString(R.string.P_Cat_id), "");
					Intent intent = new Intent(AddAction.this,
							ListCategory.class);
					startActivity(intent);
					finish();
/*					Intent intent=new Intent();
		             setResult(RESULT_OK, intent);
		             finish();
*/
				} else {
					Log.v("", "Level 2 else case");
					Intent intent = new Intent(AddAction.this,
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
	public int onTaskComplete(ArrayList<PackageItem> result) {

		GetAllInstalledAppSpinnerDataListAdapter lcm = new GetAllInstalledAppSpinnerDataListAdapter(
				mcon, result);
		sp_action_name.setAdapter(lcm);

		if (getIntent().getExtras().getString("itemid").equals("0")) {

		} else {

			ActionModel scm;
			ArrayList<ActionModel> devicelist = new GetDataFrmDB()
					.getActionByActionId(
							mDbHelper,
							Integer.parseInt(getIntent().getStringExtra(
									"itemid")));
			for (int i = 0; i < devicelist.size(); i++) {
				scm = (ActionModel) devicelist.get(i);

				pic_path.setText(scm.getKEY_ACTION_PIC());
				
				if (!pic_path.getText().toString().equals("")) {
					appendImages(pic_path.getText().toString(), pic,"update");

					pic.setVisibility(View.VISIBLE);
				} else {
					pic.setVisibility(View.GONE);
				}
				int apos = new GetPosition().getActionPos(
						scm.getKEY_ACTION_BODY(), AddAction.this);

				sp_action_name.setSelection(apos);

				String cat_name = new GetDataFrmDB().getCatNameByID(
						Integer.parseInt(scm.getKEY_CAT_ID()), mDbHelper);
				int pos = new GetPosition().getCategoryPos(cat_name, mDbHelper);
				sp_cat_name.setSelection(pos);
			}

		}
		return 0;
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

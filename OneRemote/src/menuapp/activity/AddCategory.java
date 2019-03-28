package menuapp.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import menuapp.activity.intrface.AlertInterface;
import menuapp.activity.intrface.ImageInterface;
import menuapp.activity.util.CustomAlertDialog;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.ImageCompressing;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.adapter.GetAllCategorySpinnerDataListAdapter;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.txtdata.TXTGenerator;
import menuapp.activity.util.txtdata.TXTHandler;
import menuapp.activity.util.xmldata.XMLGenerator;
import menuapp.activity.util.xmldata.XMLHandler;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class AddCategory extends Activity implements OnClickListener,
		AlertInterface, ImageInterface {

	Button submit, btn_browse, delete, next;
	DbAdapter mDbHelper;
	EditText ed_cat_name;
	ImageView back, pic;
	SharedPreferencesManager spm;
	Spinner sp_cat_name;
	Context mcon;
	int ViewClicked;

	static final int REQUEST_TAKE_PHOTO = 1;

	static final int REQUEST_CHOOSE_PHOTO = 2;

	String file_name;

	File sdIconStorageDir;

	public Uri uriSavedImage;

	TextView pic_path, title;

	int checkupdation = 0;

	String cameratype = "";

	String cat_id = "0";
	TextView tv_subcat_name;
	int editclick = 0;
	String path = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categoryadd);
		spm = new SharedPreferencesManager(this);
		mcon = AddCategory.this;
		submit = (Button) findViewById(R.id.submit);
		ed_cat_name = (EditText) findViewById(R.id.ed_cat_name);
		sp_cat_name = (Spinner) findViewById(R.id.sp_cat_name);

		// ed_cat_desc = (EditText) findViewById(R.id.ed_cat_desc);
		pic = (ImageView) findViewById(R.id.pic);
		btn_browse = (Button) findViewById(R.id.btn_browse);
		delete = (Button) findViewById(R.id.delete);
		next = (Button) findViewById(R.id.next);

		pic_path = (TextView) findViewById(R.id.pic_path);
		title = (TextView) findViewById(R.id.title);
		back = (ImageView) findViewById(R.id.back);
		tv_subcat_name = (TextView) findViewById(R.id.tv_subcat_name);

		dbSetup();
		spinnersetup();
		submit.setOnClickListener(this);
		back.setOnClickListener(this);
		btn_browse.setOnClickListener(this);
		delete.setOnClickListener(this);
		next.setOnClickListener(this);
	}

	private void dbSetup() {
		// TODO Auto-generated method stub
		mDbHelper = new DbAdapter(this);

		mDbHelper.open();

		ArrayList<AppModel> CatData = new GetDataFrmDB()
				.getAllCategoryResult(mDbHelper);

		AppModel amadd = new AppModel();
		amadd.setId("0");
		amadd.setDesc("");
		amadd.setName("root");
		amadd.setNest_id(0);
		CatData.add(amadd);

		GetAllCategorySpinnerDataListAdapter cm = new GetAllCategorySpinnerDataListAdapter(
				mcon, CatData);
		sp_cat_name.setAdapter(cm);

		AppModel am;

		if (getIntent().getStringExtra("itemid").equals("0")) {
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			title.setText("Category");
			sp_cat_name.setVisibility(View.GONE);
			tv_subcat_name.setVisibility(View.GONE);
			delete.setVisibility(View.GONE);
			sp_cat_name.setSelection(CatData.size() - 1);
			cat_id = spm.getStringValues("add_cat_id");
		} else {
			sp_cat_name.setVisibility(View.GONE);
			tv_subcat_name.setVisibility(View.GONE);
			delete.setVisibility(View.VISIBLE);
			// submit.setText("Done");
			title.setText("Category");
			ArrayList<AppModel> catlist = new GetDataFrmDB()
					.getAllCategoryResultById(
							mDbHelper,
							Integer.parseInt(getIntent().getStringExtra(
									"itemid")));
			for (int i = 0; i < catlist.size(); i++) {
				am = (AppModel) catlist.get(i);

				ed_cat_name.append(am.getName());

				// ed_cat_desc.append(am.getDesc());

				pic_path.setText(am.getPic());
				if (!pic_path.getText().toString().equals("")) {
					appendImages(pic_path.getText().toString(), pic, "update");

					pic.setVisibility(View.VISIBLE);
				} else
					pic.setBackgroundResource(R.drawable.no_image);
				cat_id = getIntent().getStringExtra("itemid");

			}
		}

		if (spm.getStringValues("menu").contains("click")) {
			next.setVisibility(View.GONE);
		}

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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_browse) {
			// mDbHelper.createCategory(ed_cat_name.getText().toString());
			ViewClicked = 0;
			selectImage();

		}
		if (v.getId() == R.id.next) {
			Intent intent = new Intent(AddCategory.this, ListCategory.class);
			startActivity(intent);
			finish();
		}
		if (v.getId() == R.id.submit) {
			saveCatData();
		}
		if (v.getId() == R.id.delete) {
			CustomAlertDialog cam = new CustomAlertDialog(AddCategory.this,
					AddCategory.this);
			cam.showDialog("Do you want to delete!", false);

		}
		if (v.getId() == R.id.back) {
			if (spm.getStringValues("menu").equals("click")) {

				if (spm.getStringValues("level").equals("level1")) {
					spm.saveStringValues(getString(R.string.P_Cat_id), "");
					//finish();
					Intent intent = new Intent(AddCategory.this,
							ListCategory.class);
					startActivity(intent);
					finish();

				} else if (spm.getStringValues("level").equals("level2")) {
					spm.saveStringValues(getString(R.string.P_Cat_id), "");
					//finish();
					Intent intent = new Intent(AddCategory.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
				}

			} else {
				if (getIntent().getStringExtra("title").equals("Add")) {
					Intent intent = new Intent(AddCategory.this,
							SelectCategory.class);
					intent.putExtra("selectedvalue", getIntent()
							.getStringExtra("selectedvalue"));
					intent.putExtra("title", getIntent()
							.getStringExtra("title"));
					startActivity(intent);
					finish();
				} else if (getIntent().getStringExtra("title").equals("Edit")) {
					Intent intent = new Intent(AddCategory.this,
							SelectLevel.class);
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

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (spm.getStringValues("menu").equals("click")) {
			Intent intent = new Intent(AddCategory.this, ListCategory.class);
			startActivity(intent);
			finish();
		} else {
			if (getIntent().getStringExtra("title").equals("Add")) {
				Intent intent = new Intent(AddCategory.this,
						SelectCategory.class);
				intent.putExtra("selectedvalue",
						getIntent().getStringExtra("selectedvalue"));
				intent.putExtra("title", getIntent().getStringExtra("title"));
				startActivity(intent);
				finish();
			} else if (getIntent().getStringExtra("title").equals("Edit")) {
				Intent intent = new Intent(AddCategory.this, SelectLevel.class);
				intent.putExtra("selectedvalue",
						getIntent().getStringExtra("selectedvalue"));
				intent.putExtra("title", getIntent().getStringExtra("title"));
				startActivity(intent);
				finish();
			}
		}

	}

	private void selectImage() {
		CustomAlertDialog cad = new CustomAlertDialog(mcon, AddCategory.this,
				"image");
		cad.CustomImageDialog();

		/*
		 * final CharSequence[] items = { "Take Photo", "Choose from Library",
		 * "Cancel" }; AlertDialog.Builder builder = new
		 * AlertDialog.Builder(mcon); builder.setTitle("Add Photo");
		 * builder.setItems(items, new DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int item) { if
		 * (items[item].equals("Take Photo")) { cameratype = "take";
		 * openNativeCamera(); } else if
		 * (items[item].equals("Choose from Library")) { cameratype = "gallery";
		 * openGallery(); } else if (items[item].equals("Cancel")) {
		 * dialog.dismiss(); } } }); builder.show();
		 */
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
				.getBasePath(AddCategory.this);

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
							.toString(), bit, AddCategory.this, file_name);

					pic.setVisibility(View.VISIBLE);
				}
			}
			if (requestCode == REQUEST_CHOOSE_PHOTO) {
				if (ViewClicked == 0) {
					Uri selectedImage = data.getData();

					String[] filePathColumn = { MediaStore.Images.Media.DATA };
					String filePath = "";

					try {
						Cursor cursor = getContentResolver()
								.query(selectedImage, filePathColumn, null,
										null, null);
						cursor.moveToFirst();

						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);
						filePath = cursor.getString(columnIndex);
						cursor.close();

						BitmapFactory.Options option = new BitmapFactory.Options();
						option.inPreferredConfig = Bitmap.Config.ARGB_8888;

					} catch (Exception e) {
						e.printStackTrace();                                                                                                   
					}

					path = createDirectoryAndSaveFile(
							BitmapFactory.decodeFile(filePath), filePath);

					
					Bitmap bit = appendImages(path, pic, "add");

					String newpath = filePath.substring(
							filePath.lastIndexOf("/") + 1, filePath.length());

					file_name = newpath;

					new ImageCompressing().saveImage(path, bit,
							AddCategory.this, file_name);

					pic_path.setText(path);

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

		
		if (!direct.exists()) {
			File Directory = new File(
					new GetPathForImage().getBasePathForCopy(AddCategory.this));

			Directory.mkdirs();
		}

		File file;
		file = new File(direct, newpath);

		
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
				.getBasePath(AddCategory.this);

		File imgFile = new File(iconsStoragePath, path);

		
		if (imgFile.exists() && imgFile.isFile()) {
			try {
				BitmapFactory.Options bm = new BitmapFactory.Options();
				bitmap = BitmapFactory
						.decodeFile(imgFile.getAbsolutePath(), bm);
				
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

				img.setBackgroundDrawable(null);

				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);

				img.setImageBitmap(bitmap);

				editclick = editclick + 1;
				
				if (ed_cat_name.getText().length() > 0 && editclick >= 2) {
					saveCatData();
				}

			} catch (Exception ex) {
				System.out.println("Error:" + ex.getMessage());
				Toast.makeText(
						AddCategory.this,
						"Some thing went wrong while loading image from gallery.",
						2000).show();
			}
		} else
			System.out.println("Not Found");
		return bitmap;
	}

	@Override
	public void selected(String action) {

		if (action.equals("delete")) {
			int id = Integer.parseInt(getIntent().getStringExtra("itemid"));

			String cat_id = new GetDataFrmDB().getCatIDMatchesWithPath(id,
					mDbHelper);

			

			String cat_ids[] = cat_id.split(",");

			try{
			for (int i = 0; i < cat_ids.length; i++) {
				mDbHelper.deleteCategory(Integer.parseInt(cat_ids[i]));
			}
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			mDbHelper.deleteSubCategoryItemByCat_id(id);

			mDbHelper.deleteactionByCat_id(id);

			new XMLGenerator().generateCatXMLFile(
					new GetDataFrmDB().getAllCategoryResult(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateCatTXTFile(
					new GetDataFrmDB().getAllCategoryResult(mDbHelper),
					mDbHelper, AddCategory.this);

			new XMLGenerator().generateSubCatItemXMLFile(
					new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateSubCatItemTXTFile(
					new GetDataFrmDB().getAllSubCategoryItemResult2(mDbHelper),
					mDbHelper, AddCategory.this);

			new XMLGenerator().generateActionXMLFile(
					new GetDataFrmDB().getAllAction(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateActionTXTFile(
					new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper,
					AddCategory.this);

			new XMLGenerator().generateLinkXMLFile(
					new GetDataFrmDB().getAllLinkResult(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateLinkTXTFile(
					new GetDataFrmDB().getAllLinkResult(mDbHelper),
					mDbHelper, AddCategory.this);
			new XMLGenerator().generateSwitchXMLFile(
					new GetDataFrmDB().getAllSwitchResult(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateSwitchTXTFile(
					new GetDataFrmDB().getAllSwitchResult(mDbHelper),
					mDbHelper, AddCategory.this);

			
			if (spm.getStringValues("menu").equals("click")) {

				if (spm.getStringValues("level").equals("level1")) {
					spm.saveStringValues(getString(R.string.P_Cat_id), "");
					Intent intent = new Intent(AddCategory.this,
							ListCategory.class);
					startActivity(intent);
					finish();

				} else if (spm.getStringValues("level").equals("level2")) {
					spm.saveStringValues(getString(R.string.P_Cat_id), "");
					Intent intent = new Intent(AddCategory.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
				}

			} else {
				/*
				 * Intent intent = new Intent(AddCategory.this, Setting.class);
				 * startActivity(intent); finish();
				 */
				if (spm.getStringValues("level").equals("level1")) {
					Intent intent = new Intent(AddCategory.this,
							ListCategory.class);
					startActivity(intent);
					finish();
				} else {
					Intent intent = new Intent(AddCategory.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
				}
			}
		} else {

		}
	}
	
	 public void deleteDir(File file) { 
         if (file.isDirectory())
             for (String child : file.list())
                 deleteDir(new File(file, child));
         file.delete();  // delete child file or empty directory
     }
	
	
	Cursor mcursor;
	private void saveCatData() {
		int catid = Integer.parseInt(cat_id);
		 String nestid = "0";
		
		  mcursor = mDbHelper.fetch_current_ver();

		  if(mcursor.getCount()==0)
		  {
		   mDbHelper.add_current_ver(1);
		  }
		  else if (mcursor.getInt(1)!=1) 
		  {
		   mDbHelper.dropDB();
		   mDbHelper.createDB();
		   Log.v("", "DB Clean");
		   deleteDir(new File("/storage/sdcard0/OneRemote/Menu"));
		   // deleteFiles("/storage/sdcard0/OneRemote/Menu/");
		   Log.v("", "Files Deleted");
		   mDbHelper.add_current_ver(1);
		  }
		 new TXTGenerator().generateVersionTXTFile(mDbHelper, AddCategory.this);
		
		
		if (!path.equals("")) {
			pic_path.setText(path);
		}
		if (pic_path.getText().toString().equals("")) {
			String name = ed_cat_name.getText().toString().replace(" ", "");
			String imagename = new GetPathForImage().getAllimages(
					name + ".png", AddCategory.this);

			if (!imagename.equals("")) {
				File direct = new File(spm.getStringValues("basefolder")
						+ "/Photos");

				
				File file = new File(direct, imagename);
				pic_path.setText("" + file);
			}
		}
		if (ed_cat_name.getText().length() > 0) {
			if (catid == 0) {
				long size = mDbHelper.getcatagorysize() + 1;
				
				nestid = String.valueOf(size);
			} else {
				if (new GetDataFrmDB().getnestpath(mDbHelper, catid)
						.equals("0")) {
					nestid = String.valueOf(mDbHelper.getcatagorysize() + 1);
				} else {
					nestid = new GetDataFrmDB().getnestpath(mDbHelper, catid)
							+ "" + (mDbHelper.getcatagorysize() + 1);
				}
			}

			if (getIntent().getStringExtra("itemid").equals("0")) {
				mDbHelper.createCategory(ed_cat_name.getText().toString(), "",
						pic_path.getText().toString(), catid, nestid, 0);

				String[] path = nestid.split(",");
				for (int j = 0; j < path.length; j++) {
					new GetDataFrmDB().updateRootFolderMaxLen(mDbHelper,
							path[j], path.length);
				}
				
			} else {
				int id = Integer.parseInt(getIntent().getStringExtra("itemid"));

				mDbHelper.updateCategory(id, ed_cat_name.getText().toString(),
						pic_path.getText().toString());
		
			}
			new XMLGenerator().generateCatXMLFile(
					new GetDataFrmDB().getAllCategoryResult(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateCatTXTFile(
					new GetDataFrmDB().getAllCategoryResult(mDbHelper),
					mDbHelper, AddCategory.this);

			new XMLGenerator().generateSubCatItemXMLFile(new GetDataFrmDB()
					.getAllSubCategoryItemResult2(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateSubCatItemTXTFile(new GetDataFrmDB()
					.getAllSubCategoryItemResult2(mDbHelper), mDbHelper,
					AddCategory.this);

			new XMLGenerator().generateActionXMLFile(
					new GetDataFrmDB().getAllAction(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateActionTXTFile(
					new GetDataFrmDB().getAllAction(mDbHelper), mDbHelper,
					AddCategory.this);

			new XMLGenerator().generateLinkXMLFile(
					new GetDataFrmDB().getAllLinkResult(mDbHelper),
					AddCategory.this);

			new TXTGenerator().generateLinkTXTFile(
					new GetDataFrmDB().getAllLinkResult(mDbHelper),
					mDbHelper, AddCategory.this);

			if (spm.getStringValues("menu").equals("click")) {

				if (spm.getStringValues("level").equals("level1")) {
					spm.saveStringValues(getString(R.string.P_Cat_id), "");
					//new ListCategory().refreshAdapter();
					//finish();
					Intent intent = new Intent(AddCategory.this,
							ListCategory.class);
					startActivity(intent);
					finish();
					/* Intent intent=new Intent();
		             setResult(RESULT_OK, intent);
		             finish();*/
					

				} else if (spm.getStringValues("level").equals("level2")) {
					spm.saveStringValues(getString(R.string.P_Cat_id), "");
					//finish();
					Intent intent = new Intent(AddCategory.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
					 /*Intent intent=new Intent();
		             setResult(RESULT_OK, intent);
		             finish();*/
				}

			} else {
				if (spm.getStringValues("level").equals("level1")) {
					Intent intent = new Intent(AddCategory.this,
							ListCategory.class);
					startActivity(intent);
					finish();
					
					/* Intent intent=new Intent();
		             setResult(RESULT_OK, intent);
		             finish();*/
				} else {
					Intent intent = new Intent(AddCategory.this,
							ListSubCategory.class);
					startActivity(intent);
					finish();
					 /*Intent intent=new Intent();
		             setResult(RESULT_OK, intent);
		             finish();*/
					
				}
			}

		} else {
			Toast.makeText(getApplicationContext(), "Please add Category.",
					2000).show();
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

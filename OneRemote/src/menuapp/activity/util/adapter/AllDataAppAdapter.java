package menuapp.activity.util.adapter;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import menuapp.activity.AddAction;
import menuapp.activity.AddCategory;
import menuapp.activity.AddDevice;
import menuapp.activity.AddLink;
import menuapp.activity.AddSwitch;
import menuapp.activity.ListCategory;
import menuapp.activity.ListSubCategory;
import menuapp.activity.R;
import menuapp.activity.database.DbAdapter;
import menuapp.activity.database.GetDataFrmDB;
import menuapp.activity.util.GetListOfInstalledApplication;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.SharedPreferencesManager;
import menuapp.activity.util.DragDrop.SubCategoryItemOnDragListener;
import menuapp.activity.util.loadingimage.ImageLoader;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.AppendAllData;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//********* Adapter class extends with BaseAdapter and implements with OnClickListener ************//*
public class AllDataAppAdapter extends BaseAdapter {

	// *********** Declare Used Variables *********//*
	private Activity activity;
	private ArrayList<AppendAllData> dataload;
	private static LayoutInflater inflater = null;
	public Resources res;
	AppendAllData tempValues = null;
	int i = 0;
	private Typeface typeFaceFlamaBook;
	String list_update;
	int id;
	public ImageLoader imageLoader;
	DbAdapter mdbhelper;
	//List<String> ids;
	List<String>  posids;
	SharedPreferencesManager spm;
	ListView cat_list;
	// ************* CustomAdapter Constructor *****************//*
	public AllDataAppAdapter(Activity a, ArrayList<AppendAllData> d, Resources resLocal,
			DbAdapter mdb,ListView cat_list,List<String> posids) {

		// ********** Take passed values **********//*
		activity = a;
		dataload = d;
		res = resLocal;
		mdbhelper = mdb;
		//this.ids = ids;
		this.posids=posids;
		this.cat_list=cat_list;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		spm = new SharedPreferencesManager(activity);
		
	}

	// ******** What is the size of Passed Arraylist Size ************//*
	
	
	@Override
	public int getCount() {
		return dataload.size();
	}

	@Override
	public Object getItem(int position) {
		return dataload.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	

	// ********* Create a holder Class to contain inflated xml file elements
	// *********//*

	// ****** Depends upon data size called for each row , Create each ListView
	// row *****//*
	public View getView(final int position, View convertView, ViewGroup parent) {

		View vi = convertView;

		vi = inflater.inflate(R.layout.customlistviewwithimage, null);

		final TextView name = (TextView) vi.findViewById(R.id.tv_name);
		final TextView id = (TextView) vi.findViewById(R.id.tv_id);
		final ImageView pic = (ImageView) vi.findViewById(R.id.pic);
		final RelativeLayout rl = (RelativeLayout) vi.findViewById(R.id.rl);

		final TextView tv_type = (TextView) vi.findViewById(R.id.tv_type);
		final TextView tv_status = (TextView) vi.findViewById(R.id.tv_status);
		final TextView tv_pos = (TextView) vi.findViewById(R.id.tv_pos);
		final ImageView onoff = (ImageView) vi.findViewById(R.id.onoff);
		final ImageView edit = (ImageView) vi.findViewById(R.id.edit);
		edit.setVisibility(View.VISIBLE);

		
		edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setdata(tv_type.getText().toString(), id.getText().toString(), name.getText().toString(), tv_status.getText().toString(), onoff,position);
				
			}
		});
		if (dataload.size() <= 0) {

		} else {
			// ***** Get each Model object from Arraylist ********//*
			tempValues = null;

			tempValues = (AppendAllData) dataload.get(position);

			// ************ Set Model values in Holder elements ***********//*

			id.setText(tempValues.getId());
			name.setText(tempValues.getName());
			tv_type.setText(tempValues.getType());
			tv_status.setText(tempValues.getStatus());
			tv_pos.setText(tempValues.getPos());
			if (tv_type.getText().toString().equals("link")) {
				String url = new GetDataFrmDB().getLinkUrlfromDeviceID(mdbhelper,
						Integer.parseInt(id.getText().toString()));
				
			if (url.contains("?:")) {
					System.out.println("pos if Link:."+position);
					onoff.setVisibility(View.VISIBLE);
					System.out.println("ids:.." + posids);
					
					Log.v("Pos :", String.valueOf(posids));
					Log.v("Positon :", String.valueOf(position));
					if (posids.contains(String.valueOf(position))) {
						onoff.setBackgroundResource(android.R.drawable.presence_online);
						Log.v("Status Adpter :", "On");
					} else {
						onoff.setBackgroundResource(android.R.drawable.presence_invisible);
						Log.v("Status Adpter :", "Off");
					}
				} else {
					onoff.setVisibility(View.GONE);
				}
			} 
			else if (tv_type.getText().toString().equals("switch")) {
				String url = new GetDataFrmDB().getSwitchUrlfromDeviceID(mdbhelper,
						Integer.parseInt(id.getText().toString()));
				if (url.contains("?:")) {
					System.out.println("pos if Switch:."+position);
					onoff.setVisibility(View.VISIBLE);
					System.out.println("ids:.." + posids);
					if (posids.contains(String.valueOf(position))) {
						onoff.setBackgroundResource(android.R.drawable.presence_online);
					} else {		
						onoff.setBackgroundResource(android.R.drawable.presence_invisible);
					}
				} else {
					onoff.setVisibility(View.GONE);
				}
			}
		
			if (position % 2 == 0) {
				name.setBackgroundResource(R.anim.oddlist);
				name.setTextColor(Color.parseColor("#001824"));
			} else {

				name.setBackgroundResource(R.anim.evenlist);
				name.setTextColor(Color.parseColor("#ffffff"));

			}

			if (tempValues.getName().length() > 25) {
				String newtemp = tempValues.getName().substring(0, 25);
				name.setText(newtemp + "..");
			} else {
				name.setText(tempValues.getName());
			}
			if (!tempValues.getPic().toString().equals("")) {
				// appendImages(tempValues.getPic().toString(), pic);

				imageLoader.DisplayImage(tempValues.getPic().toString(), pic);
				pic.setVisibility(View.VISIBLE);
			} else {
				pic.setBackgroundResource(R.drawable.no_image);

			}
			  vi.setOnDragListener(new SubCategoryItemOnDragListener(tempValues,activity,dataload,mdbhelper));
			  
		}
		return vi;
	}
	
	public ArrayList<AppendAllData> getList() {
		return dataload;
	}

	
String url="";

	
public void setdata(String tv_type,String tv_id,String tv_name,String tv_status,ImageView onoff,int position){
	String cat_id = tv_id;



	if (tv_type.equals("cat")) {

		spm.saveStringValues(
				activity.getString(R.string.Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));
		spm.saveStringValues(
				activity.getString(R.string.Cat_name),
				""
						+ new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(spm
										.getStringValues(activity.getString(R.string.Cat_id))),
								mdbhelper));
		spm.saveStringValues(
				activity.getString(R.string.P_Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));

		/*Intent intent = new Intent(activity,
				AddCategory.class);
		intent.putExtra("itemid", "" + cat_id);
		activity.startActivityForResult(intent, position);*/
		Intent intent = new Intent(activity, AddCategory.class);
		intent.putExtra("itemid", "" + cat_id);
		activity.startActivity(intent);
		activity.finish();
	
		
	}
	if (tv_type.equals("device")) {
		String dev_id = tv_id;
			
		spm.saveStringValues(activity.getString(R.string.Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));
		spm.saveStringValues(
				activity.getString(R.string.Cat_name),
				""
						+ new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(spm
										.getStringValues(activity.getString(R.string.Cat_id))),
								mdbhelper));
		spm.saveStringValues(
				activity.getString(R.string.P_Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));

		/*Intent intent = new Intent(activity,
				AddDevice.class);
		intent.putExtra("itemid", "" + dev_id);
		activity.startActivityForResult(intent, position);*/
		
		Intent intent = new Intent(activity, AddDevice.class);
		intent.putExtra("itemid", "" + dev_id);
		activity.startActivity(intent);
		activity.finish();
	}

	if (tv_type.equals("link")) {
		String l_id = tv_id;

		System.out.println("ac_id in link:...." + l_id);

		spm.saveStringValues(
				activity.getString(R.string.Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));
		spm.saveStringValues(
				activity.getString(R.string.Cat_name),
				""
						+ new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(spm
										.getStringValues(activity.getString(R.string.Cat_id))),
								mdbhelper));
		spm.saveStringValues(
				activity.getString(R.string.P_Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));

		/*Intent intent = new Intent(activity,
				AddLink.class);
		intent.putExtra("itemid", "" + l_id);
		activity.startActivityForResult(intent, position);*/
		Intent intent = new Intent(activity, AddLink.class);
		intent.putExtra("itemid", "" + l_id);
		activity.startActivity(intent);
		activity.finish();
	}
	
	if (tv_type.equals("switch")) {
		String l_id = tv_id;

		System.out.println("ac_id in switch:...." + l_id);

		spm.saveStringValues(
				activity.getString(R.string.Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));
		spm.saveStringValues(
				activity.getString(R.string.Cat_name),
				""
						+ new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(spm
										.getStringValues(activity.getString(R.string.Cat_id))),
								mdbhelper));
		spm.saveStringValues(
				activity.getString(R.string.P_Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));

		/*Intent intent = new Intent(activity,
				AddSwitch.class);
		intent.putExtra("itemid", "" + l_id);
		activity.startActivityForResult(intent, position);*/
		
		Intent intent = new Intent(activity, AddSwitch.class);
		intent.putExtra("itemid", "" + l_id);
		activity.startActivity(intent);
		activity.finish();
	}
	
	if (tv_type.equals("action")) {
		String ac_id = tv_id;

		spm.saveStringValues(
				activity.getString(R.string.Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));
		spm.saveStringValues(
				activity.getString(R.string.Cat_name),
				""
						+ new GetDataFrmDB().getCatNameByID(
								Integer.parseInt(spm
										.getStringValues(activity.getString(R.string.Cat_id))),
								mdbhelper));
		spm.saveStringValues(
				activity.getString(R.string.P_Cat_id),
				""
						+ spm.getStringValues(activity.getString(R.string.Cat_id)));

		/*Intent intent = new Intent(activity,
				AddAction.class);
		intent.putExtra("itemid", "" + ac_id);
		activity.startActivityForResult(intent, position);*/
		Intent intent = new Intent(activity, AddAction.class);
		intent.putExtra("itemid", "" + ac_id);
		activity.startActivity(intent);
		activity.finish();
	}
	
}

	
	
	public void appendImages(String path, ImageView img) {
		path = path.substring(path.lastIndexOf("/") + 1, path.length());

		String iconsStoragePath = new GetPathForImage().getBasePath(activity
				.getApplicationContext());

		File imgFile = new File(iconsStoragePath, path);

		System.out.println("Image path.." + imgFile);

		if (imgFile.exists() && imgFile.isFile()) {
			try {

				BitmapFactory.Options bmOptions = new BitmapFactory.Options();

				bmOptions.inSampleSize = 4;

				Bitmap bitmap = BitmapFactory.decodeFile(
						imgFile.getAbsolutePath(), bmOptions);

				System.out.println("Widht in list :.." + bitmap.getWidth());

				System.out.println("height in list :.." + bitmap.getHeight());

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

				Matrix m = img.getImageMatrix();

				System.out.println("width :.." + bitmap.getWidth());

				RectF drawableRect = new RectF(0, 0, bitmap.getWidth(),
						bitmap.getHeight());
				RectF viewRect = new RectF(0, 0, bitmap.getWidth(),
						bitmap.getHeight());
				m.setRectToRect(drawableRect, viewRect,
						Matrix.ScaleToFit.CENTER);
				img.setImageMatrix(m);
				img.setImageBitmap(bitmap);
			} catch (Exception ex) {
				System.out.println("Error:" + ex.getMessage());
				Toast.makeText(
						activity,
						"Some thing went wrong while loading image from gallery.",
						2000).show();
			}
		} else
			System.out.println("Not Found");
	}

}
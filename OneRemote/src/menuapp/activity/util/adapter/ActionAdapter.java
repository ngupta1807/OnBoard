package menuapp.activity.util.adapter;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

import menuapp.activity.ListCategory;
import menuapp.activity.R;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.loadingimage.ImageLoader;
import menuapp.activity.util.model.ActionModel;
import menuapp.activity.util.model.AppModel;

import android.app.Activity;
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
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//********* Adapter class extends with BaseAdapter and implements with OnClickListener ************//*
public class ActionAdapter extends BaseAdapter {

	// *********** Declare Used Variables *********//*
	private Activity activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	public Resources res;
	ActionModel tempValues = null;
	int i = 0;
	private Typeface typeFaceFlamaBook;
	String list_update;
	int id;
	public ImageLoader imageLoader;

	// ************* CustomAdapter Constructor *****************//*
	public ActionAdapter(Activity a, ArrayList d, Resources resLocal) {

		// ********** Take passed values **********//*
		activity = a;
		data = d;
		res = resLocal;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}

	// ******** What is the size of Passed Arraylist Size ************//*
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

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

		TextView name = (TextView) vi.findViewById(R.id.tv_name);
		
		final TextView id = (TextView) vi.findViewById(R.id.tv_id);
		ImageView pic = (ImageView) vi.findViewById(R.id.pic);
		RelativeLayout rl = (RelativeLayout) vi.findViewById(R.id.rl);
		
		// Button update = (Button) vi.findViewById(R.id.update);
		if (data.size() <= 0) {

		} else {
			// ***** Get each Model object from Arraylist ********//*
			tempValues = null;

			tempValues = (ActionModel) data.get(position);

			// ************ Set Model values in Holder elements ***********//*

			System.out.println("Action size:.." + data.size());
			System.out.println("in Action app adapter:..");

			name.setText(tempValues.getKEY_ACTION_BODY());

			if (position % 2 == 0) {

				name.setBackgroundResource(R.anim.oddlist);
				name.setTextColor(Color.parseColor("#001824"));
			} else {

				name.setBackgroundResource(R.anim.evenlist);
				name.setTextColor(Color.parseColor("#ffffff"));

			}

			if (tempValues.getKEY_ACTION_BODY().length() > 25) {
				String newtemp = tempValues.getKEY_ACTION_BODY().substring(0,
						25);
				name.setText(newtemp + "..");
			} else {
				name.setText(tempValues.getKEY_ACTION_BODY());
			}
			System.out.println("pic path:..." + tempValues.getKEY_ACTION_PIC());
			if (!tempValues.getKEY_ACTION_PIC().toString().equals("")) {
				// appendImages(tempValues.getPic().toString(), pic);

				imageLoader.DisplayImage(tempValues.getKEY_ACTION_PIC()
						.toString(), pic);
				pic.setVisibility(View.VISIBLE);
			} else {
				pic.setBackgroundResource(R.drawable.no_image);
				
			}
			id.setText(tempValues.getKEY_ACTION_ID());
			
		}
		return vi;
	}

	public void appendImages(String path, ImageView img) {
		path = path.substring(path.lastIndexOf("/") + 1, path.length());

		String iconsStoragePath = new GetPathForImage().getBasePath(activity.getApplicationContext());

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
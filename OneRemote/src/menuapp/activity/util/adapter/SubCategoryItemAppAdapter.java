package menuapp.activity.util.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import menuapp.activity.R;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.loadingimage.ImageLoader;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.SubCatAppModel;
import menuapp.activity.util.model.SubCatItemModel;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//********* Adapter class extends with BaseAdapter and implements with OnClickListener ************//*
public class SubCategoryItemAppAdapter extends BaseAdapter implements
		OnClickListener {

	// *********** Declare Used Variables *********//*
	private Activity activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	public Resources res;
	SubCatItemModel tempValues = null;
	int i = 0;
	private Typeface typeFaceFlamaBook;
	String list_update;
	int id;
	public ImageLoader imageLoader;

	// ************* CustomAdapter Constructor *****************//*
	public SubCategoryItemAppAdapter(Activity a, ArrayList d, Resources resLocal) {

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
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;

		vi = inflater.inflate(R.layout.customlistviewwithimage, null);

		TextView name = (TextView) vi.findViewById(R.id.tv_name);
		TextView id = (TextView) vi.findViewById(R.id.tv_id);
		ImageView pic = (ImageView) vi.findViewById(R.id.pic);
		RelativeLayout rl = (RelativeLayout) vi.findViewById(R.id.rl);

		// RelativeLayout bdr = (RelativeLayout) vi.findViewById(R.id.bdr);

		if (data.size() <= 0) {

		} else {
			// ***** Get each Model object from Arraylist ********//*
			tempValues = null;

			tempValues = (SubCatItemModel) data.get(position);

			// ************ Set Model values in Holder elements ***********//*

			if (position % 2 == 0) {

				name.setBackgroundResource(R.anim.oddlist);
				name.setTextColor(Color.parseColor("#001824"));
			} else {

				name.setBackgroundResource(R.anim.evenlist);
				name.setTextColor(Color.parseColor("#ffffff"));

			}
			/*
			 * if (position == 0) {
			 * bdr.setBackgroundResource(R.drawable.top_list_bullet); } if
			 * (position == (data.size() - 1)) {
			 * bdr.setBackgroundResource(R.drawable.btm_list_bullet); } if
			 * (!(position == 0) && !(position == (data.size() - 1))) {
			 * bdr.setBackgroundResource(R.drawable.list_bullet); }
			 */
			name.setText(tempValues.getKEY_SUB_CAT_ITEM_BODY());

			if (tempValues.getKEY_SUB_CAT_ITEM_BODY().length() > 25) {
				String newtemp = tempValues.getKEY_SUB_CAT_ITEM_BODY()
						.substring(0, 25);
				name.setText(newtemp + "..");
			} else {
				name.setText(tempValues.getKEY_SUB_CAT_ITEM_BODY());
			}

			if (!tempValues.getKEY_PIC().equals("")) {
				// appendImages(tempValues.getKEY_PIC(), pic);
				imageLoader.DisplayImage(tempValues.getKEY_PIC().toString(),
						pic);
				pic.setVisibility(View.VISIBLE);
			} else {
				pic.setBackgroundResource(R.drawable.no_image);
				
			}

			id.setText(tempValues.getKEY_SUB_CAT_ITEM_ID());

		}
		return vi;
	}

	@Override
	public void onClick(View v) {

	}

	public void appendImages(String path, ImageView img) {
		path = path.substring(path.lastIndexOf("/") + 1, path.length());

		String iconsStoragePath = new GetPathForImage().getBasePath(activity.getApplicationContext());

		File imgFile = new File(iconsStoragePath, path);

		System.out.println("Image path.." + imgFile);

		if (imgFile.exists() && imgFile.isFile()) {
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();

			bmOptions.inSampleSize = 4;

			Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),
					bmOptions);

			Matrix matrix = new Matrix();

			try {
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
			} catch (IOException e) {
				Log.v("TAG", "" + e.getMessage());
				e.printStackTrace();
			}

			try {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);

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
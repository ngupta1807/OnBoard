package menuapp.activity.util.adapter;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

import menuapp.activity.ListCategory;
import menuapp.activity.R;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.loadingimage.ImageLoader;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LanModel;
import menuapp.activity.util.model.SdcardModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//********* Adapter class extends with BaseAdapter and implements with OnClickListener ************//*
public class SdcardDataAdapter extends BaseAdapter {

	// *********** Declare Used Variables *********//*
	private Context activity;
	private ArrayList<File> data;
	private static LayoutInflater inflater = null;
	public Resources res;
	File tempValues = null;
	LanModel lantempValues = null;
	int id;
	String type = "";
	private ArrayList<LanModel> landata;

	// ************* CustomAdapter Constructor *****************//*
	public SdcardDataAdapter(Context a, ArrayList<File> d, Resources resLocal) {

		// ********** Take passed values **********//*
		System.out.println("in file");
		activity = a;
		data = d;
		res = resLocal;
		type = "file";
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public SdcardDataAdapter(Context a, ArrayList<LanModel> d,
			Resources resLocal, String ty) {

		// ********** Take passed values **********//*
		System.out.println("in string");
		activity = a;
		landata = d;
		res = resLocal;
		type = ty;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// ******** What is the size of Passed Arraylist Size ************//*
	public int getCount() {
		if (type.equals("string")) {
			return landata.size();
			// return 1;
		} else {
			return data.size();
		}
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		View vi = convertView;

		vi = inflater.inflate(R.layout.custom_lan_listview, null);

		TextView name = (TextView) vi.findViewById(R.id.tv_name);
		TextView ip = (TextView) vi.findViewById(R.id.tv_ip);
		TextView url = (TextView) vi.findViewById(R.id.tv_url);
		final TextView id = (TextView) vi.findViewById(R.id.tv_id);
		final TextView host = (TextView) vi.findViewById(R.id.tv_host);
		RelativeLayout background = (RelativeLayout) vi
				.findViewById(R.id.background);

		tempValues = null;
		if (type.equals("string")) {
			background.setBackgroundDrawable(null);
			lantempValues = (LanModel) landata.get(position);
			url.setVisibility(View.VISIBLE);
			ip.setVisibility(View.VISIBLE);
			name.setText(lantempValues.getKEY_name());
			ip.setText(lantempValues.getKEY_ip());
			url.setText(lantempValues.getKEY_url());
			host.setText(lantempValues.getKEY_host());

			url.setPaintFlags(url.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
			
			id.setText("");

		} else {
			tempValues = (File) data.get(position);
			url.setVisibility(View.GONE);
			ip.setVisibility(View.GONE);
			
			name.setTextColor(activity.getResources().getColor(
					android.R.color.white));
			
			name.setText(tempValues.getName());

			id.setText(tempValues.getPath());
		}

		return vi;
	}

}
package menuapp.activity.util.adapter;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

import menuapp.activity.ListCategory;
import menuapp.activity.R;
import menuapp.activity.util.GetPathForImage;
import menuapp.activity.util.loadingimage.ImageLoader;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.GetDownloadModel;
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
public class ServerDataAdapter extends BaseAdapter {

	// *********** Declare Used Variables *********//*
	private Context activity;
	private ArrayList<GetDownloadModel> data;
	private static LayoutInflater inflater = null;
	public Resources res;
	GetDownloadModel tempValues = null;
	int id;

	// ************* CustomAdapter Constructor *****************//*
	public ServerDataAdapter(Context a, ArrayList<GetDownloadModel> d,
			Resources resLocal) {

		// ********** Take passed values **********//*
		activity = a;
		data = d;
		res = resLocal;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	// ******** What is the size of Passed Arraylist Size ************//*
	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		TextView name;
		TextView id;
		TextView tv_version;
		TextView tv_desc;
		TextView tv_org_file;
		TextView tv_chksum;

	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;

		LayoutInflater inflater = LayoutInflater.from(activity);

		convertView = inflater.inflate(R.layout.custom_server_listview, null);

		viewHolder = new ViewHolder();

		viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
		viewHolder.id = (TextView) convertView.findViewById(R.id.tv_id);
		viewHolder.tv_version = (TextView) convertView
				.findViewById(R.id.tv_version);

		viewHolder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
		viewHolder.tv_org_file = (TextView) convertView
				.findViewById(R.id.tv_org_file);
		viewHolder.tv_chksum = (TextView) convertView
				.findViewById(R.id.tv_chksum);

		if (data.size() <= 0) {

		} else {

			tempValues = (GetDownloadModel) data.get(position);

			System.out.println("name:.." + tempValues.getName());

			viewHolder.name.setText(tempValues.getName());

			viewHolder.id.setText(tempValues.getId());

			viewHolder.tv_version.setText(tempValues.getId());

			viewHolder.tv_desc.setText(tempValues.getDescription());

			viewHolder.tv_org_file.setText(tempValues.getOriginal_filename());

			viewHolder.tv_chksum.setText(tempValues.getChecksum());

		}
		return convertView;
	}

}
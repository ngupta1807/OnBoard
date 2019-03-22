package menuapp.activity.util.adapter;

import java.util.ArrayList;


import menuapp.activity.R;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.SubCatAppModel;
import menuapp.activity.util.model.SubCatItemModel;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GetAllDeviceSpinnerDataListAdapter extends BaseAdapter 
{
	private Context mContext;
	private ArrayList<SubCatItemModel> mItems;
	AlertDialog alertDialog;
	SubCatItemModel am;

	public GetAllDeviceSpinnerDataListAdapter(Context c,
			ArrayList<SubCatItemModel> items) 
	{
		mContext = c;
		mItems = items;
	}

	@Override
	public int getCount() 
	{
		return mItems.size();
	}

	@Override
	public Object getItem(int arg0) 
	{
		return null;
	}

	@Override
	public long getItemId(int position) 
	{
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) 
	{
		final TextView name;
		final TextView c_id;
		if (convertView == null) 
		{
			LayoutInflater importLayout = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = importLayout.inflate(
					R.layout.customspinnerview, null);

		}

		
		
		name = (TextView) convertView.findViewById(R.id.tv_name);
		c_id = (TextView) convertView.findViewById(R.id.tv_id);
		
		am = (SubCatItemModel) mItems.get(position);
		/************ Set Model values in Holder elements ***********/
		
		name.setText(am.getKEY_SUB_CAT_ITEM_BODY());
		c_id.setText(am.getKEY_SUB_CAT_ITEM_ID());
		
		return convertView;
	}
}

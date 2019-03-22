package menuapp.activity.util.adapter;

import java.util.ArrayList;


import menuapp.activity.R;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.SubCatAppModel;

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

public class GetAllCategoryDataListAdapter extends BaseAdapter 
{
	private Context mContext;
	private ArrayList<AppModel> mItems;
	AlertDialog alertDialog;
	AppModel am;

	public GetAllCategoryDataListAdapter(Context c,
			ArrayList<AppModel> items) 
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
					R.layout.customlistview, null);

		}
		
		name = (TextView) convertView.findViewById(R.id.tv_name);
		c_id = (TextView) convertView.findViewById(R.id.tv_id);				
		am = (AppModel) mItems.get(position);

		name.setText(am.getName());
		c_id.setText(am.getId());
		
		return convertView;
	}
}

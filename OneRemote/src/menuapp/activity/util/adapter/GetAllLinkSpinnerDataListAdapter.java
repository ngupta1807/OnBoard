package menuapp.activity.util.adapter;

import java.util.ArrayList;


import menuapp.activity.R;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatAppModel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GetAllLinkSpinnerDataListAdapter extends BaseAdapter 
{
	private Context mContext;
	private ArrayList<LinkModel> mItems;
	AlertDialog alertDialog;
	LinkModel am;

	public GetAllLinkSpinnerDataListAdapter(Context c,
			ArrayList<LinkModel> items) 
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
		am = (LinkModel) mItems.get(position);

		/************ Set Model values in Holder elements ***********/
		
		name.setText(am.getKEY_LINK_BODY());
		c_id.setText(am.getKEY_LINK_ITEM_ID());
		
		return convertView;
	}
}

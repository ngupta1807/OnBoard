package menuapp.activity.util.adapter;

import java.util.ArrayList;


import menuapp.activity.R;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.LinkModel;
import menuapp.activity.util.model.SubCatAppModel;
import menuapp.activity.util.model.SwitchModel;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GetAllSwitchSpinnerDataListAdapter extends BaseAdapter 
{
	private Context mContext;
	private ArrayList<SwitchModel> mItems;
	AlertDialog alertDialog;
	SwitchModel am;

	public GetAllSwitchSpinnerDataListAdapter(Context c,
			ArrayList<SwitchModel> items) 
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
		am = (SwitchModel) mItems.get(position);

		/************ Set Model values in Holder elements ***********/
		
		name.setText(am.getKEY_SWITCH_BODY());
		c_id.setText(am.getKEY_SWITCH_ITEM_ID());
		
		return convertView;
	}
}

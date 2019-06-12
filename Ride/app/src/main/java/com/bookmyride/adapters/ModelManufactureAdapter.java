package com.bookmyride.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.models.Country;

import java.util.ArrayList;
import java.util.Locale;


//********* Adapter class extends with BaseAdapter and implements with OnClickListener ************//*
public class ModelManufactureAdapter extends BaseAdapter {

	// *********** Declare Used Variables *********//*
	private Context activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	public Resources res;
	Country tempValues = null;
	ArrayList<Country> listData;
	String type;
	// ************* CustomAdapter Constructor *****************//*
	public ModelManufactureAdapter(Context a, ArrayList<Country> d, String type,
								   Resources resLocal) {

		// ********** Take passed values **********//*
		activity = a;
		data = d;
		this.type = type;
		res = resLocal;
		listData = new ArrayList<Country>();
		listData.addAll(data);

		// *********** Layout inflator to call external xml layout ()
		// ***********//*
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

	public static class ViewHolder {

		public TextView id;
		public TextView name;
		public TextView code;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.country_list, null);

			holder = new ViewHolder();
			holder.id = (TextView) vi.findViewById(R.id.id);
			holder.code = (TextView) vi.findViewById(R.id.code);
			holder.name = (TextView) vi.findViewById(R.id.name);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (data.size() <= 0) {

		} else {
			// ***** Get each Model object from Arraylist ********//*
			tempValues = null;
			tempValues = (Country) data.get(position);

			holder.id.setText(tempValues.getId().toString());
			//holder.code.setText(tempValues.getCode().toString());
			holder.name.setText(tempValues.getName().toString());

		}
		return vi;
	}
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		data.clear();
		if (charText.length() == 0) {
			data.addAll(listData);

		} else {
			for (Country postDetail : listData) {
				if (charText.length() != 0 && postDetail.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
					data.add(postDetail);
				}
			}
			Country cm = new Country();
			if(type.equals("1")) {
				cm.setId("Other Manufacturer");
				cm.setName("Other Manufacturer");
			} else {
				cm.setId("Other Model");
				cm.setName("Other Model");
			}
			if(!charText.contains("oth") && !charText.contains("man"))
				data.add(this.data.size(), cm);
		}
		notifyDataSetChanged();
	}

}
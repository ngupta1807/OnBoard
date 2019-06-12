package com.bookmyride.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.models.Fleets;

import java.util.ArrayList;
import java.util.Locale;


//********* Adapter class extends with BaseAdapter and implements with OnClickListener ************//*
public class FleetAdapter extends BaseAdapter {

	// *********** Declare Used Variables *********//*
	private Context activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	public Resources res;
	Fleets tempValues = null;
	ArrayList<Fleets> listData;
	// ************* CustomAdapter Constructor *****************//*
	public FleetAdapter(Context a, ArrayList<Fleets> d,
						Resources resLocal) {

		// ********** Take passed values **********//*
		activity = a;
		data = d;
		res = resLocal;
		listData = new ArrayList<Fleets>();
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

	public class ViewHolder {
		private ImageView image;
		private TextView brand, model, number;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final ViewHolder holder;

		if (convertView == null) {
			vi = inflater.inflate(R.layout.fleet_adapter, null);
			holder = new ViewHolder();
			holder.brand = (TextView) vi.findViewById(R.id.brand);
			holder.model = (TextView) vi.findViewById(R.id.model);
			holder.number = (TextView) vi.findViewById(R.id.number);
			holder.image = (ImageView) vi.findViewById(R.id.icon);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (data.size() <= 0) {

		} else {
			// ***** Get each Model object from Arraylist ********//*
			tempValues = null;
			tempValues = (Fleets) data.get(position);

			holder.brand.setText(tempValues.getBrand());
			holder.model.setText(tempValues.getModel());
			holder.number.setText(tempValues.getVehicleNumber());

			if(tempValues.getCategoryId().equals("1")) {
				holder.image.setImageResource(R.drawable.taxi);
			} else if(tempValues.getCategoryId().equals("2")) {
				holder.image.setImageResource(R.drawable.economy);
			} else if(tempValues.getCategoryId().equals("3")) {
				holder.image.setImageResource(R.drawable.premium);
			} else if(tempValues.getCategoryId().equals("4")) {
				holder.image.setImageResource(R.drawable.motor_bike);
			}

		}
		return vi;
	}
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		data.clear();
		if (charText.length() == 0) {
			data.addAll(listData);

		} else {
			for (Fleets postDetail : listData) {
				if (charText.length() != 0 && postDetail.getVehicleNumber().toLowerCase(Locale.getDefault()).contains(charText)) {
					data.add(postDetail);
				}
			}
		}
		notifyDataSetChanged();
	}
}
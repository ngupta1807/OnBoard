package menuapp.activity.util.adapter;

import java.util.ArrayList;

import menuapp.activity.R;
import menuapp.activity.util.model.AppModel;
import menuapp.activity.util.model.SubCatAppModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

//********* Adapter class extends with BaseAdapter and implements with OnClickListener ************//*
public class SubCategoryAppAdapter extends BaseAdapter implements OnClickListener {

	// *********** Declare Used Variables *********//*
	private Activity activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	public Resources res;
	SubCatAppModel tempValues = null;
	int i = 0;
	private Typeface typeFaceFlamaBook;
	String list_update;
	int id;

	// ************* CustomAdapter Constructor *****************//*
	public SubCategoryAppAdapter(Activity a, ArrayList d, Resources resLocal) {

		// ********** Take passed values **********//*
		activity = a;
		data = d;
		res = resLocal;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

		vi = inflater.inflate(R.layout.customlistview, null);

		TextView name = (TextView) vi.findViewById(R.id.tv_name);
		final TextView id = (TextView) vi.findViewById(R.id.tv_id);
		//RelativeLayout bdr = (RelativeLayout) vi.findViewById(R.id.bdr);
		//Button update = (Button) vi.findViewById(R.id.update);
		if (data.size() <= 0) {

		} else {
			// ***** Get each Model object from Arraylist ********//*
			tempValues = null;

			tempValues = (SubCatAppModel) data.get(position);

			// ************ Set Model values in Holder elements ***********//*

			System.out.println("size:.." + data.size());

			/*if (position == 0) {
				bdr.setBackgroundResource(R.drawable.top_list_bullet);
			}
			if (position == (data.size() - 1)) {
				bdr.setBackgroundResource(R.drawable.btm_list_bullet);
			}
			if (!(position == 0) && !(position == (data.size() - 1))) {
				bdr.setBackgroundResource(R.drawable.list_bullet);
			}*/
			
			name.setText(tempValues.getName());

			if (tempValues.getName().length() > 25) 
			{
				String newtemp = tempValues.getName().substring(0, 25);
				name.setText(newtemp + "..");
			} else {
				name.setText(tempValues.getName());
			}

			id.setText(tempValues.getSUB_CAT_Id());
			/*update.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("CustomAdapter=====Row button clicked====="+id.getText().toString());
					Intent intent = new Intent(activity, AddSubCategory.class);
					intent.putExtra("itemid",""+id.getText().toString());
					activity.startActivity(intent);
					activity.finish();
				}
			});*/
		}
		return vi;
	}

	@Override
	public void onClick(View v) {
		Log.v("CustomAdapter", "=====Row button clicked=====");
		System.out.println("CustomAdapter=====Row button clicked=====");
	}

}
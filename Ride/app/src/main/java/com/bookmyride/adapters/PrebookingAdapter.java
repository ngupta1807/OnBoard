package com.bookmyride.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bookmyride.R;
import com.bookmyride.models.Ride;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by vinod on 10/10/2017.
 */

public class PrebookingAdapter extends BaseAdapter {
    Context context;
    List<Ride> data;
    LayoutInflater inflater;

    public PrebookingAdapter(Context context, List<Ride> data) {
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View mView = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            mView = inflater.inflate(R.layout.adaper_prebooking, null);
            holder = new ViewHolder();
            holder.time = (TextView) mView.findViewById(R.id.time);
            holder.puAddress = (TextView) mView.findViewById(R.id.pu_address);
            holder.doAddress = (TextView) mView.findViewById(R.id.do_address);
            mView.setTag(holder);
        } else
            holder = (ViewHolder) mView.getTag();
        Ride ride = data.get(pos);
        String puAddress = "";
        String doAddress = "";
        try {
            JSONObject jobj = new JSONObject(ride.getPuInfo());
            puAddress = jobj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jobj = new JSONObject(ride.getDoInfo());
            if (jobj.getString("address").equals("") || jobj.getString("address").equals("null"))
                doAddress = "Not Selected";
            else
                doAddress = jobj.getString("address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String puTime = ride.getPuDate();
        if (!puTime.equals("null") && !puTime.equals("") && puTime.contains(" "))
            puTime = puTime.substring(puTime.indexOf(" ") + 1, puTime.length());
        holder.time.setText(puTime);
        holder.puAddress.setText(puAddress);
        holder.doAddress.setText(doAddress);

        return mView;
    }

    public static class ViewHolder {
        public TextView time, puAddress, doAddress;
    }

    private Date getDate(String dtStart) {
        Date date;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = format.parse(dtStart);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return date;
    }
}

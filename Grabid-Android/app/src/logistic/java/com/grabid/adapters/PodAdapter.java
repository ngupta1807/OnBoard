package com.grabid.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.common.SessionManager;

import java.util.ArrayList;

/**
 * Created by graycell on 18/12/17.
 */

public class PodAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<String> data;
    SessionManager session;


    public PodAdapter(Context ctx, ArrayList<String> Data) {
        this.ctx = ctx;
        this.data = Data;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        session = new SessionManager(this.ctx);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View mView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        String image = (String) getItem(i);
        if (mView == null) {
            mView = inflater.inflate(R.layout.podadapter, null);
            viewHolder = new ViewHolder();
            viewHolder.podimage = mView.findViewById(R.id.podimg);
            viewHolder.itemNo = mView.findViewById(R.id.poddocno);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        viewHolder.itemNo.setText("POD Doc: " + (1 + i));
        try {
            Bitmap myBitmap = BitmapFactory.decodeFile(image);
            viewHolder.podimage.setImageBitmap(myBitmap);
        } catch (Exception e) {
            e.toString();
        }
        return mView;
    }

    static class ViewHolder {
        ImageView podimage;
        TextView itemNo;

    }
}
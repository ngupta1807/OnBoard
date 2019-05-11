package com.grabid.adapters;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.grabid.R;
import com.grabid.common.SessionManager;
import com.grabid.fragments.Images;

import java.util.ArrayList;

/**
 * Created by graycell on 15/12/17.
 */

public class BarCodeAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater inflater;
    ArrayList<String> data;
    SessionManager session;
    Fragment fragment;


    public BarCodeAdapter(Context ctx, ArrayList<String> Data, Fragment fragment) {
        this.ctx = ctx;
        this.data = Data;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        session = new SessionManager(this.ctx);
        this.fragment = fragment;
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
            mView = inflater.inflate(R.layout.bar_code_scanner, null);
            viewHolder = new ViewHolder();
            viewHolder.barcodeimage = mView.findViewById(R.id.barcodeimg);
            viewHolder.close = mView.findViewById(R.id.barcodeclose);
            viewHolder.itemNo = mView.findViewById(R.id.itemno);
            mView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) mView.getTag();
        }
        try {
            String pureBase64Encoded = image.substring(image.indexOf(",") + 1);
            byte[] decodedBytes = Base64.decode(pureBase64Encoded, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            //  viewHolder.barcodeimage.setImageBitmap(bmp);
            Glide.with(ctx).load(decodedBytes).crossFade().fitCenter().into(viewHolder.barcodeimage);
        } catch (Exception e) {
            e.toString();
            Glide.clear(viewHolder.barcodeimage);
        }

        viewHolder.itemNo.setText("Item: " + (1 + i));
        viewHolder.close.setId(i);

        viewHolder.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.remove(view.getId());
                notifyDataSetChanged();
                ((Images) fragment).removeBarCode(view.getId());
            }
        });
        return mView;
    }

    static class ViewHolder {
        ImageView barcodeimage;
        Button close;
        TextView itemNo;

    }
}


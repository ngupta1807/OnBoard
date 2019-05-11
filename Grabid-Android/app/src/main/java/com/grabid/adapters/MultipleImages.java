package com.grabid.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grabid.R;
import com.grabid.activities.HomeActivity;
import com.grabid.fragments.ProfileStepTwo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by graycell on 20/2/18.
 */

public class MultipleImages extends RecyclerView.Adapter<MultipleImages.ImagesViewHolder> {
    private List<String> horizontalList;
    Context context;
    Fragment fragment;
    int imageType;
    int id;
    String type;
    public static String[] deletedPDF;

    public MultipleImages(List<String> horizontalList, Context context, Fragment fragment, int imageType, int id,String type) {
        this.horizontalList = horizontalList;
        this.context = context;
        this.fragment = fragment;
        this.imageType = imageType;
        this.id = id;
        this.type = type;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == horizontalList.size()) ? R.layout.buttonrecycler : R.layout.recyclerviewmultipleimages;
    }

    @Override
    public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == R.layout.recyclerviewmultipleimages) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewmultipleimages, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.buttonrecycler, parent, false);
        }
        ImagesViewHolder ivh = new ImagesViewHolder(itemView);
        return ivh;
    }

    private int getImageResource(ImageView iv) {
        return (int) iv.getTag();
    }

    @Override
    public void onBindViewHolder(ImagesViewHolder holder, final int position) {
        if (position == horizontalList.size()) {
            holder.Button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("sd","ss"+position);
                    if (!(getImageResource(HomeActivity.edit) == R.drawable.edit_top)) {
                        if(position>4) {
                            displayPrompt(context);
                        }else {
                            ((ProfileStepTwo) fragment).AddPhotos(imageType, id);
                        }
                    }

                }
            });
        } else {
            try {
                if (horizontalList.get(position).contains("http")) {
                    if(horizontalList.get(position).contains(".pdf")){
                        //Bitmap myBitmap = BitmapFactory.decodeFile(horizontalList.get(position));
                        holder.imageView.setImageResource(R.drawable.dummypdf);
                    }else {
                        Picasso.with(context).load(horizontalList.get(position)).into(holder.imageView);
                    }
                } else {
                    Bitmap myBitmap = BitmapFactory.decodeFile(horizontalList.get(position));
                    holder.imageView.setImageBitmap(myBitmap);
                }
                //holder.pic_id.setText();
            } catch (Exception e) {
                e.toString();
            }
            //holder.imageView.setImageResource(horizontalList.get(position).getProductImage());
                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                    if(ProfileStepTwo.IsEdit==false) {
                        if (horizontalList.get(position).contains(".pdf")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse( "http://docs.google.com/viewer?url=" + horizontalList.get(position)), "text/html");
                            context.startActivity(intent);
                        } else {
                        }
                    }
                }
            });
            holder.cross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getImageResource(HomeActivity.edit) == R.drawable.edit_top) {
                    } else    deletePrompt(position, context);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return horizontalList.size() + 1;
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        android.widget.Button cross;
        TextView Button,pic_id;

        public ImagesViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.idImage);
            cross = view.findViewById(R.id.cross);
            pic_id= view.findViewById(R.id.pic_id);
            Button = view.findViewById(R.id.uploadimage);
        }
    }
    int size=0;
    private void deleteImage(int position) {
        deletedPDF = new String[size+1];
        deletedPDF[size]=horizontalList.get(position);
        size++;
        horizontalList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, horizontalList.size());
    }

    public void deletePrompt(final int pos, Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getResources().getString(R.string.app_name));
        if(type.equals("rpaper")) {
            builder.setMessage(ctx.getString(R.string.removevreg));
        }else{
            builder.setMessage(ctx.getString(R.string.removevinc));
        }
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteImage(pos);
            }
        });
        Dialog d = builder.create();
        d.show();
    }

    public void displayPrompt( Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(ctx.getResources().getString(R.string.app_name));
        if (type.equals("rpaper")) {
            builder.setMessage(ctx.getString(R.string.maxvreg));
        } else {
            builder.setMessage(ctx.getString(R.string.maxvinc));
        }
        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = builder.create();
        d.show();
    }

}
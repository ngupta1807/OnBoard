
package com.tipbox.app.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.tipbox.app.R
import com.tipbox.app.util.SimpleSpanBuilder
import com.yuyakaido.android.cardstackview.sample.Spot
import java.util.ArrayList

class CardStackAdapter(
        private var spots: List<Spot> = emptyList(), var mcon: Context,var amt:String,var logo:String
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.list_item_spot, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val spot = spots[position]
        //this.position =position
        holder.name.text = amt
        if(logo.equals("null",true)) {
        }else{
            Glide.with(mcon).load(logo).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.item_img.background = resource
                    }
                }
            })
        }

        Log.v("App","adapter"+holder.name.tag+"::"+position)
        Log.v("App","adapter inside"+position)
            holder.name.tag = spot.name
            if (position % 2 == 0) {
                holder.image.setCardBackgroundColor(ContextCompat.getColor(mcon, R.color.white))
                holder.text_corner.setBackgroundResource(R.drawable.profile_picture_frame)
                //holder.name.setTextColor(ContextCompat.getColor(mcon, R.color.white));
            } else {
                holder.image.setCardBackgroundColor(ContextCompat.getColor(mcon, R.color.white))
                holder.text_corner.setBackgroundResource(R.drawable.profile_picture_frame)
                //holder.name.setTextColor(ContextCompat.getColor(mcon, R.color.colorPrimary));
            }
    }

    override fun getItemCount(): Int {
        return spots.size
    }

    fun setSpots(spots: List<Spot>) {
        this.spots = spots
    }

    fun getSpots(): List<Spot> {
        return spots
    }

    fun getParentSpots(): List<Spot> {
        val spots = ArrayList<Spot>()
        spots.add(Spot(name = "yellow"))
        spots.add(Spot(name = "white"))
        return spots
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.item_name)
        var image: CardView = view.findViewById(R.id.item_image)
        var item_img: ImageView = view.findViewById(R.id.item_img)
        var text_corner: RelativeLayout = view.findViewById(R.id.text_corner)
    }
}


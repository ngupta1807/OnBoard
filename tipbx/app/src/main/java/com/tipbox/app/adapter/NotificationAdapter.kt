package com.sample.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.tipbox.app.modle.NotificationModle
import com.tipbox.app.util.CustomTypefaceSpan
import com.tipbox.app.util.SimpleSpanBuilder
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.tipbox.app.R


class NotificationAdapter(txtList: ArrayList<NotificationModle>) : RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var icon: RelativeLayout
        var card_background: RelativeLayout
        init {
            name = view.findViewById(com.tipbox.app.R.id.name) as TextView
            icon = view.findViewById(com.tipbox.app.R.id.icon) as RelativeLayout

            card_background = view.findViewById<View>(com.tipbox.app.R.id.card_layout) as RelativeLayout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(com.tipbox.app.R.layout.list_adapter_notification, parent, false)
        mcon=parent.context
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txt = txtList[position]
        holder.name.text = txt.name

        var name=txt.name.split("gave")[0]
        var priceval=txt.name.split("gave")[1]
        var price=priceval.split(" ")[1]
        var boldTypeface = Typeface.create(holder.name.getTypeface(), Typeface.BOLD)
        var regularTypeface = Typeface.create(holder.name.getTypeface(),Typeface.NORMAL)
        var ssb = SimpleSpanBuilder(name, ForegroundColorSpan(ContextCompat.getColor(mcon, com.tipbox.app.R.color.black))
        ,CustomTypefaceSpan(boldTypeface)
        )
        ssb += SimpleSpanBuilder.Span(
            " gave ",
            ForegroundColorSpan(ContextCompat.getColor(mcon, com.tipbox.app.R.color.nav_text)), CustomTypefaceSpan(regularTypeface)
        )
        ssb += SimpleSpanBuilder.Span(
            price,
            ForegroundColorSpan(ContextCompat.getColor(mcon, com.tipbox.app.R.color.black)),CustomTypefaceSpan(boldTypeface)
        )
        ssb += SimpleSpanBuilder.Span(
            " as tip to you.",
            ForegroundColorSpan(ContextCompat.getColor(mcon, com.tipbox.app.R.color.nav_text)),CustomTypefaceSpan(regularTypeface)
        )

        holder.name.text = ssb.build()
        //holder.name.text=txt.name

        Glide.with(mcon).load(txt.price).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.icon.background = resource
                }
            }
        })
        //txt.price
        val bgDrawable = holder.card_background.getBackground() as LayerDrawable
        if (position % 2 == 0) {
            try {
                val shape = bgDrawable.findDrawableByLayerId(com.tipbox.app.R.id.gradientDrawble) as GradientDrawable
                shape.setColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.colorPrimary))
            } catch (e: Exception) {
                e.toString()
            }

        } else if (position % 2 != 0) {
            val shape = bgDrawable.findDrawableByLayerId(com.tipbox.app.R.id.gradientDrawble) as GradientDrawable
            shape.setColor(ContextCompat.getColor(mcon, com.tipbox.app.R.color.sidebar_clr))
        }

    }

    override fun getItemCount(): Int {
        return txtList.size
    }

    private val txtList: ArrayList<NotificationModle>
    lateinit var  mcon : Context
    init {
        this.txtList = txtList
    }


}
package com.sample.app.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.tipbox.app.R
import com.tipbox.app.modle.HistoryModle


class HistoryAdapter(txtList: ArrayList<HistoryModle>) : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var date: TextView
        var time: TextView
        var price: TextView
        var t_id: TextView
        var pay_type_blue: ImageView
        var pay_type_yellow: ImageView
        var card_background: RelativeLayout
        init {
            name = view.findViewById<View>(R.id.name) as TextView
            date = view.findViewById<View>(R.id.date) as TextView
            time = view.findViewById<View>(R.id.time) as TextView
            price = view.findViewById<View>(R.id.price) as TextView
            t_id = view.findViewById<View>(R.id.t_id) as TextView
            pay_type_blue = view.findViewById<View>(R.id.pay_type_blue) as ImageView
            pay_type_yellow = view.findViewById<View>(R.id.pay_type_yellow) as ImageView
            card_background = view.findViewById<View>(R.id.card_layout) as RelativeLayout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_adapter_history, parent, false)
        mcon=parent.context
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txt = txtList[position]
        holder.name.text = txt.name
        holder.date.text = txt.date
        holder.time.text = txt.time
        holder.price.text = txt.price
        holder.t_id.text = txt.t_id
        val bgDrawable = holder.card_background.getBackground() as LayerDrawable
        if (position % 2 == 0) {
            try {
                val shape = bgDrawable.findDrawableByLayerId(R.id.gradientDrawble) as GradientDrawable
                shape.setColor(ContextCompat.getColor(mcon, R.color.colorPrimary))
            } catch (e: Exception) {
                e.toString()
            }

        } else if (position % 2 != 0) {
            val shape = bgDrawable.findDrawableByLayerId(R.id.gradientDrawble) as GradientDrawable
            shape.setColor(ContextCompat.getColor(mcon, R.color.sidebar_clr))
        }

        if(txt.type.equals("send")){
            holder.pay_type_blue.visibility=View.VISIBLE
            holder.pay_type_yellow.visibility=View.INVISIBLE
            holder.pay_type_blue.setBackgroundResource(R.drawable.dollar_down)
        }else{
            holder.pay_type_blue.visibility=View.INVISIBLE
            holder.pay_type_yellow.visibility=View.VISIBLE
            holder.pay_type_yellow.setBackgroundResource(R.drawable.dollar_up)
        }

    }

    override fun getItemCount(): Int {
        return txtList.size
    }

    private val txtList: ArrayList<HistoryModle>
    lateinit var  mcon : Context
    init {
        this.txtList = txtList
    }
}
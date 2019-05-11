package com.sample.app.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.sample.app.param.BWord
import com.sample.app.intrface.AdapterCallback


class BadWordAdapter(txtList: ArrayList<BWord>, adapterCallback: AdapterCallback) : RecyclerView.Adapter<BadWordAdapter.MyViewHolder>() {

    private val txtList: ArrayList<BWord>
    private val adapterCallback: AdapterCallback
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        var close: ImageView
        init {
            title = view.findViewById<View>(com.sample.app.R.id.name) as TextView
            close = view.findViewById<View>(com.sample.app.R.id.close) as ImageView
        }
    }

    init {
        this.txtList = txtList
        this.adapterCallback = adapterCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(com.sample.app.R.layout.bword_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txt = txtList[position]
        holder.title.text = txt.title


        holder.close.setOnClickListener(View.OnClickListener
            {
                adapterCallback.onClickCallback(txt)
            }
        )
    }

    override fun getItemCount(): Int {
        return txtList.size
    }
}
package com.sample.app.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sample.app.R
import com.sample.app.param.Tweet


import java.util.ArrayList

class TweetAdapter(txtList: ArrayList<Tweet>) : RecyclerView.Adapter<TweetAdapter.MyViewHolder>() {

    private val txtList: List<Tweet>

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView

        init {
            title = view.findViewById<View>(R.id.name) as TextView
        }
    }

    init {
        this.txtList = txtList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txt = txtList[position]
        holder.title.text = txt.title
    }

    override fun getItemCount(): Int {
        return txtList.size
    }
}
package com.sample.app.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sample.app.R
import com.sample.app.database.AccessDatabase
import com.sample.app.param.Tweet

class TweetAdapter(txtList: ArrayList<Tweet>,aDatabse: AccessDatabase) : RecyclerView.Adapter<TweetAdapter.MyViewHolder>() {
    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView
        init {
            title = view.findViewById<View>(R.id.name) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tweet_item, parent, false)
        mcon=parent.context
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val txt = txtList[position]
        holder.title.text = txt.title

        if(aDatabse.viewRecord(txt.title)==true)
            holder.title.setTextColor(mcon.getResources().getColor(android.R.color.holo_red_dark))
        else
            holder.title.setTextColor(mcon.getResources().getColor(android.R.color.black))
    }

    override fun getItemCount(): Int {
        return txtList.size
    }

    private val txtList: ArrayList<Tweet>
    private val aDatabse: AccessDatabase
    lateinit var  mcon : Context
    init {
        this.txtList = txtList
        this.aDatabse=aDatabse
    }
}
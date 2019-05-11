package com.sample.app.frag

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.sample.app.R
import com.sample.app.adapter.TweetAdapter
import com.sample.app.apis.DownloadTwitterTask
import com.sample.app.database.AccessDatabase
import com.sample.app.intrface.AsyncTaskCompleteListener
import com.sample.app.param.Tweet
import com.sample.app.util.*
import com.sample.app.util.ConstantParam.delay
import java.lang.Exception


class TweetList : Fragment(), AsyncTaskCompleteListener {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.sample.app.R.layout.fragment_tweet, container, false)
        ButterKnife.bind(this, view)
        mcon=activity!!.applicationContext
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler()
        txtList = ArrayList()
        aDatabase = AccessDatabase(mcon)
        mAdapter = TweetAdapter(txtList,aDatabase)
        var mLayoutManager = LinearLayoutManager(this@TweetList.context);
        tweetList.setLayoutManager(mLayoutManager)
        tweetList.setItemAnimator(DefaultItemAnimator())
        tweetList.addItemDecoration(DividerItemDecoration(this@TweetList.context, LinearLayoutManager.VERTICAL));
        tweetList.setAdapter(mAdapter)
        var comn=Common(mcon)
        if(comn.readData().equals(""))
            comn.emptyTagPopup(activity)
        else {
            handler.postDelayed(monitor, delay)
            downloadTweets()

        }
    }

    var monitor =object : Runnable {
        override fun run() {
            downloadTweets()
            handler.postDelayed(this, delay)
        }
    }



    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener{
        fun onFragmentInteraction( uri: Uri)
    }

    // download twitter timeline after first checking to see if there is a network connection
    fun downloadTweets() {
        if (Utils.networkStatus(mcon)) {
            DownloadTwitterTask(pBar,title,mcon,txtList,this@TweetList)?.execute()
        } else {
            Toast.makeText(mcon, getString(com.sample.app.R.string.contect_issue), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTaskComplete(result: String?) {
        try {
            val twits =  Common(mcon).jsonToTwitter(result)
            if(twits?.size==0)
                Toast.makeText(mcon,mcon.getString(R.string.no_data)+" @"+Common(mcon).readData(), Toast.LENGTH_LONG).show()
            else {
                for (tweet in twits!!) { // lets write the results to the console as well
                    txtList.add(Tweet(tweet.text))
                }
            }
        }catch (ex: Exception){
            Toast.makeText(mcon,mcon.getString(R.string.server_error), Toast.LENGTH_LONG).show()
            handler.removeCallbacks(monitor)
        }
        mAdapter.notifyDataSetChanged()
    }


    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(monitor)
    }
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mcon: Context
    private lateinit var handler: Handler
    private lateinit var txtList: ArrayList<Tweet>
    private lateinit var mAdapter: TweetAdapter
    private lateinit var aDatabase: AccessDatabase
    @BindView(R.id.progressbar) lateinit var pBar: ProgressBar
    @BindView(R.id.title) lateinit var title: TextView
    @BindView(R.id.tweet_list) lateinit var tweetList: RecyclerView

}
package com.sample.app.frag

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.Gson
import com.sample.app.R
import com.sample.app.adapter.TweetAdapter
import com.sample.app.param.Twitter
import com.sample.app.util.*
import java.io.*


class Tweet : Fragment()  {
    private var mListener: OnFragmentInteractionListener? = null
    private lateinit var mcon: Context
    private lateinit var handler: Handler
    private lateinit var txtList: ArrayList<Tweet>
    private lateinit var mAdapter: TweetAdapter
    @BindView(R.id.progressbar) lateinit var pBar: ProgressBar
    @BindView(R.id.tweet_list) lateinit var tweetList: RecyclerView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.sample.app.R.layout.fragment_tweet, container, false)
        ButterKnife.bind(this, view)
        mcon=activity!!.applicationContext
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity!!.title = "HashTag"
        handler = Handler()
        txtList = ArrayList<Tweet>()
        mAdapter = TweetAdapter(txtList)
        var mLayoutManager = LinearLayoutManager(this@Tweet.context);
        tweetList.setLayoutManager(mLayoutManager)
        tweetList.setItemAnimator(DefaultItemAnimator())
        tweetList.addItemDecoration(DividerItemDecoration(this@Tweet.context, LinearLayoutManager.VERTICAL));

        tweetList.setAdapter(mAdapter)
        /*handler.postDelayed(object : Runnable {
            override fun run() {
                downloadTweets()
                handler.postDelayed(this, delay)
            }
        }, delay)*/
        downloadTweets()
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
            DownloadTwitterTask()?.execute()
        } else {
            Toast.makeText(mcon, getString(com.sample.app.R.string.contect_issue), Toast.LENGTH_SHORT).show()
        }
    }

    // Uses an AsyncTask to download a Twitter user's timeline
    private inner class DownloadTwitterTask : AsyncTask<String, Void, String>() {

        override fun onPreExecute() {
            super.onPreExecute()
            pBar.visibility= View.VISIBLE
        }
        override fun doInBackground(vararg screenNames: String): String? {
            return getTwitterStream("love")
        }

        // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
        override fun onPostExecute(result: String?) {
            val twits = jsonToTwitter(result)
            pBar.visibility= View.GONE
            for (tweet in twits!!) { // lets write the results to the console as well
                Log.i("values::", tweet.text)
            }
            mAdapter.notifyDataSetChanged();
        }

        // converts a string of JSON data into a Twitter object
        private fun jsonToTwitter(result: String?): Twitter? {
            var twits: Twitter? = null
            if (result != null && result.length > 0) {
                try {
                    val gson = Gson()
                    twits = gson.fromJson<Twitter>(result, Twitter::class.java)

                } catch (ex: IllegalStateException) {
                    // just eat the exception
                }

            }
            return twits
        }

        private fun getTwitterStream(screenName: String): String? {
            var apicall = APICall()
            lateinit var results :String
            try {
                var token_request= apicall.postRequest(TwitterTokenURL) //Obtain a bearer token
                val auth = apicall.jsonToAuthenticated(token_request)
                if (auth != null && auth.token_type.equals("bearer")) {
                    results =apicall.getData(TwitterStreamURL + "love", auth.access_token!!)
                }
            } catch (ex: UnsupportedEncodingException) {
            } catch (ex1: IllegalStateException) {
            }
            return results
        }

    }

    companion object {
         val TwitterTokenURL = "https://api.twitter.com/oauth2/token"
         val TwitterStreamURL = "https://api.twitter.com/1.1/statuses/user_timeline.json?count=10&screen_name="
         //val TwitterStreamURL = "https://api.twitter.com/1.1/search/tweets.json?count=10&q="
         val delay = 2000L  // 300000L 5 minutes // 2000 2 sec
    }


}
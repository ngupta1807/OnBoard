package com.sample.app.apis

import android.content.Context
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.sample.app.R
import com.sample.app.frag.TweetList
import com.sample.app.param.Tweet
import com.sample.app.util.Common
import com.sample.app.util.ConstantParam
import java.io.UnsupportedEncodingException

class DownloadTwitterTask(
    pBar: ProgressBar,
    title: TextView,
    mcon: Context,
    txtList: ArrayList<Tweet>,
    callBack: TweetList
) : AsyncTask<String, Void, String>() {
    private val pBar: ProgressBar
    private val title: TextView
    private val mcon: Context
    private val txtList: ArrayList<Tweet>
    private val callBack: TweetList
    init {
        this.pBar = pBar
        this.title = title
        this.mcon = mcon
        this.txtList = txtList
        this.callBack = callBack
    }
    override fun onPreExecute() {
        super.onPreExecute()
        pBar.visibility= View.VISIBLE
        title.visibility= View.VISIBLE
        title.text= mcon.getString(R.string.static_txt)+" @"+ Common(mcon).readData()
    }
    override fun doInBackground(vararg screenNames: String): String? {
        return getTwitterStream()
    }

    // onPostExecute convert the JSON results into a Twitter object (which is an Array list of tweets
    override fun onPostExecute(result: String?) {
        txtList.clear()
        pBar.visibility= View.INVISIBLE
        title.visibility= View.VISIBLE
        callBack.onTaskComplete(result)
    }

    private fun getTwitterStream(): String? {
        var apicall = APICall()
        var results : String=""
        try {
            var token_request= apicall.postRequest(ConstantParam.TwitterTokenURL) //Obtain a bearer token
            val auth = apicall.jsonToAuthenticated(token_request)
            if (auth != null && auth.token_type.equals("bearer")) {
                results =apicall.getData(ConstantParam.TwitterStreamURL + Common(mcon).readData(), auth.access_token!!)
            }
        } catch (ex: UnsupportedEncodingException) {
        } catch (ex1: IllegalStateException) {
        }
        return results
    }

}
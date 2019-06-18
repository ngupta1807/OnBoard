package com.tipbox.app.frag


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.sample.app.adapter.NotificationAdapter
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.modle.NotificationModle
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.restclient.GetApiData
import com.tipbox.app.util.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


class Notification : Fragment(), ApiCallback,View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.action_login -> {
                txtList.clear()
                getNotification()
            }
        }
    }

    override fun result(res: String) {
        var obj=JSONObject(res)
        if(obj.getInt(ServerUrls.STATUS).equals(0)){
            no_data_view.visibility=View.VISIBLE
            notificationList.visibility=View.GONE
        }else{
                var obj = JSONObject(res)
                var ar = JSONArray(obj.getString(ServerUrls.NOTIFICATIONDATA))
                txtList=GetApiData().getNotificationData(ar)
                notificationList.visibility = View.VISIBLE
                no_data_view.visibility = View.GONE
                mAdapter = NotificationAdapter(txtList)
                notificationList.setAdapter(mAdapter)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_notification, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mcon=activity
        mApplication = activity!!.application as MApplication
        txtList = ArrayList()
        action_login.setOnClickListener(this)
        var mLayoutManager = LinearLayoutManager(this@Notification.context);
        notificationList.setLayoutManager(mLayoutManager)
        notificationList.setItemAnimator(DefaultItemAnimator())
        notificationList.addItemDecoration(DividerItemDecoration(this@Notification.context, 0));
        //mAdapter = NotificationAdapter(txtList)
        //notificationList.setAdapter(mAdapter)
        getNotification()
    }

   /* private fun staticData() {
        txtList.add( NotificationModle("","$1","Kevin",""))
        txtList.add( NotificationModle("","$2","John",""))

        mAdapter.notifyDataSetChanged()
    }*/

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context as OnFragmentInteractionListener
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
    private fun getNotification() {
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@Notification, mcon, ServerUrls.NOTIFICATION).response(
                mApplication!!.getRestClient()!!.riseService.viewNotification(
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
    var mApplication: MApplication?=null
    var mcon: Context?=null
    private var mListener: OnFragmentInteractionListener? = null
    @BindView(R.id.notification_list) lateinit var notificationList: RecyclerView
    @BindView(R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(R.id.no_data_view) lateinit var no_data_view: RelativeLayout
    @BindView(R.id.action_login) lateinit var action_login: Button
    private lateinit var txtList: ArrayList<NotificationModle>
    private lateinit var mAdapter: NotificationAdapter
}
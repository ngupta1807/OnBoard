package com.tipbox.app.frag


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import com.sample.app.adapter.HistoryAdapter
import com.sample.app.adapter.NotificationAdapter
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.modle.HistoryModle
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.restclient.GetApiData
import com.tipbox.app.util.*
import org.json.JSONArray
import org.json.JSONObject


class PaymentHistory : Fragment(), ApiCallback,View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {

        }
    }


    override fun result(res: String) {
        var obj=JSONObject(res)
        if(obj.getInt(ServerUrls.STATUS).equals(0)){
            no_data_view.visibility=View.VISIBLE
            historyList.visibility=View.GONE
        }else{
            var obj=JSONObject(res)
            var ar= JSONArray(obj.getString(ServerUrls.PAYMENTHISTORYDATA))
            if(ar.length()==0){
                no_data_view.visibility=View.VISIBLE
                historyList.visibility=View.GONE
            }else {
                txtList= GetApiData().getPaymentHistory(ar,mApplication)
                historyList.visibility = View.VISIBLE
                no_data_view.visibility = View.GONE
                mAdapter = HistoryAdapter(txtList)
                historyList.setAdapter(mAdapter)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_phistory, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mcon=activity
        mApplication = activity!!.application as MApplication
                txtList = ArrayList()
        var mLayoutManager = LinearLayoutManager(this@PaymentHistory.context);
        historyList.setLayoutManager(mLayoutManager)
        historyList.setItemAnimator(DefaultItemAnimator())
        historyList.addItemDecoration(DividerItemDecoration(this@PaymentHistory.context, 0));
        getHistory()
    }


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
    private fun getHistory() {
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@PaymentHistory, mcon, ServerUrls.PAYHISTORY).response(
                mApplication!!.getRestClient()!!.riseService.viewPayHistory(
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
    @BindView(R.id.progressbar) lateinit var progressbar: ProgressBar
    private var mListener: OnFragmentInteractionListener? = null
    @BindView(R.id.history_list) lateinit var historyList: RecyclerView
    @BindView(R.id.no_data_view) lateinit var no_data_view: RelativeLayout
    private lateinit var txtList: ArrayList<HistoryModle>
    private lateinit var mAdapter: HistoryAdapter
}
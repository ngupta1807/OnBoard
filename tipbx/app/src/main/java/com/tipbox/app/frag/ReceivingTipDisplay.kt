package com.tipbox.app.frag


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.tipbox.app.MainActivity
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_receivetip_display.*
import org.json.JSONObject


class ReceivingTipDisplay : Fragment() ,View.OnClickListener,ApiCallback  {
    override fun result(res: String) {
        var obj= JSONObject(res)
        if(!obj.getInt("status").equals(0)) {
            var board=obj.getJSONObject("UserDashBoard")
            if(!board.getString("qrCode").equals("null",true)) {
                url=board.getString("qrCode").replace("\"//","/")
                Glide.with(this).load(url).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            logo.background = resource
                        }
                    }
                })
            }else{
                Toast.makeText(mcon, obj.getString("message"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
           com.tipbox.app.R.id.action_start -> {
               mApplication!!.storeStringInPreference("session", "receive")
               mcon!!.startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", "18"))
               activity!!.finish()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_receivetip_display, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var boldTypeface = Typeface.create(txt_code.getTypeface(), Typeface.BOLD)
        var regularTypeface = Typeface.create(txt_code.getTypeface(), Typeface.NORMAL)

        var ssb = SimpleSpanBuilder("Display your ", CustomTypefaceSpan(regularTypeface))
        ssb += SimpleSpanBuilder.Span(
            "QR code", CustomTypefaceSpan(boldTypeface)
        )
        mcon=activity
        mApplication = activity!!.application as MApplication
        txt_code.text = ssb.build()
        txt_code.setAllCaps(false)
        action_start.setOnClickListener(this)
        getDashBoard()

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
    private fun getDashBoard() {
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@ReceivingTipDisplay, mcon, ServerUrls.DASHBOARD).response(
                mApplication!!.getRestClient()!!.riseService.viewDashboard(
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
    lateinit  var url:String
    var mApplication: MApplication?=null
    var mcon: Context?=null
    private var mListener: OnFragmentInteractionListener? = null
    @BindView(com.tipbox.app.R.id.txt_code) lateinit var txt_code: TextView
    @BindView(com.tipbox.app.R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(com.tipbox.app.R.id.logo) lateinit var logo: ImageView


}
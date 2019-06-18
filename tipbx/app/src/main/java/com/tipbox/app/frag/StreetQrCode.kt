package com.tipbox.app.frag


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.tipbox.app.BarcodeScan
import com.tipbox.app.MainActivity
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.interfce.BitmapCallback
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*
import org.json.JSONObject


class StreetQrCode : Fragment(), ApiCallback,View.OnClickListener, BitmapCallback {
    override fun result(res: Bitmap) {
        Common().saveTempBitmap(res,mcon)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.tipbox.app.R.id.qr_code -> {
                showDialog()
            }
            com.tipbox.app.R.id.ly_scan -> {
                 mcon!!.startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", "5"))
                 activity!!.finish()
            }
            com.tipbox.app.R.id.ly_tips -> {
                mcon!!.startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", "5"))
                activity!!.finish()
            }
            com.tipbox.app.R.id.ly_tips_yr -> {
                mcon!!.startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", "5"))
                activity!!.finish()
            }
            com.tipbox.app.R.id.scan -> {
                permission_type="scan"
                /*if(mApplication!!.getStringFromPreference("swipe_amt").equals("")){
                    showDialog("Please first setup swipe setting amount")
                }else{*/
                    if (checkPermission()) {
                        val intent = Intent(activity, BarcodeScan::class.java)
                        intent.putExtra("type","qrcode")
                        startActivity(intent)
                        activity!!.finish()
                    } else {
                        requestPermission()
                    }
                /*}*/
            }


        }
    }

    fun showDialog() {
        val dialog = Dialog(getActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)
        dialog.setContentView(com.tipbox.app.R.layout.popup_view_qr)
        val dialogQRcode = dialog.findViewById(com.tipbox.app.R.id.qr_code) as ImageView
        val dialogClose = dialog.findViewById(com.tipbox.app.R.id.close) as ImageView
        val share = dialog.findViewById(com.tipbox.app.R.id.share) as TextView
        val download = dialog.findViewById(com.tipbox.app.R.id.download) as TextView
        val popup_lay = dialog.findViewById(com.tipbox.app.R.id.popup_lay) as LinearLayout
        dialogClose.setOnClickListener(View.OnClickListener { dialog.dismiss() })
        val ly_scan_bg = popup_lay.getBackground() as LayerDrawable
        val shape = ly_scan_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))

        share.setOnClickListener { View.OnClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name).toString()+" "+ "Qr Code")
            sendIntent.putExtra(Intent.EXTRA_TEXT, curl)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        } }

        download.setOnClickListener { View.OnClickListener {
            permission_type="download"
            if (checkPermission()) {
                ImageDownloaderTask(this,curl, context).execute(curl)
            } else {
                requestPermission()
            }
        } }

        Glide.with(this).load(curl).into(object : SimpleTarget<Drawable>() {
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    dialogQRcode.background = resource
                }
            }
        })
        dialog.show()

    }


    override fun result(res: String) {
        var obj= JSONObject(res)
        if(!obj.getInt("status").equals(0)) {
            var board=obj.getJSONObject("UserDashBoard")
            if(board.getString("totalTipAmountThisMonthByStreetPerformer").contains("."))
                scans.setText(board.getString("totalTipAmountThisMonthByStreetPerformer").split(".")[0])
            else
                scans.setText(board.getString("totalTipAmountThisMonthByStreetPerformer"))
            if(board.getString("totalTipAmountReceivedThisYearByStreetPerformer").contains("."))
                tips.setText(board.getString("totalTipAmountReceivedThisYearByStreetPerformer").split(".")[0])
            else
                tips.setText(board.getString("totalTipAmountReceivedThisYearByStreetPerformer"))
            if(board.getString("totalTipAmountReceivedByStreetPerformer").contains("."))
                tips_y.setText(board.getString("totalTipAmountReceivedByStreetPerformer").split(".")[0])
            else
                tips.setText(board.getString("totalTipAmountReceivedByStreetPerformer"))

            if(!board.getString("qrCode").equals("null",true)) {
                url=board.getString("qrCode").replace("\"//","/")
                Glide.with(this).load(url).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            qr_code.background = resource
                        }
                    }
                })
            }else{
            }

            if(!board.getString("qrCodeColored").equals("null",true)) {
                curl=board.getString("qrCodeColored").replace("\"//","/")

            }

        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_st_qrcode, container, false)
        ButterKnife.bind(this, view)
        mcon=activity
        mApplication = activity!!.application as MApplication
        qr_code.setOnClickListener(this)
        lscan.setOnClickListener(this)
        getDashBoard()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var ssb = SimpleSpanBuilder("Tap to display ", ForegroundColorSpan(ContextCompat.getColor(activity!!.applicationContext,R.color.white)))
        ssb += SimpleSpanBuilder.Span(
            " QR Code",
            ForegroundColorSpan(ContextCompat.getColor(activity!!.applicationContext,R.color.colorPrimary))
        )

        ssb += SimpleSpanBuilder.Span(
            "\n full screen",
            ForegroundColorSpan(ContextCompat.getColor(activity!!.applicationContext,R.color.white))
        )

        title.text = ssb.build()
        ly_scan.setOnClickListener(this)
        ly_tips.setOnClickListener(this)
        ly_tips_yr.setOnClickListener(this)

        val ly_scan_bg = ly_scan.getBackground() as LayerDrawable
        val shape = ly_scan_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape.setColor(ContextCompat.getColor(activity!!.applicationContext,R.color.colorPrimary))

        val ly_tips_bg = ly_tips.getBackground() as LayerDrawable
        val shape_ly_tips = ly_tips_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape_ly_tips.setColor(ContextCompat.getColor(activity!!.applicationContext,R.color.white))

        val ly_tips_yr_bg = ly_tips_yr.getBackground() as LayerDrawable
        val shape_ly_tips_yr = ly_tips_yr_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape_ly_tips_yr.setColor(ContextCompat.getColor(activity!!.applicationContext,R.color.tips_yr))

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
            ApiResponse(progressbar, this@StreetQrCode, mcon, ServerUrls.DASHBOARD).response(
                mApplication!!.getRestClient()!!.riseService.viewDashboard(
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
    fun  checkPermission():Boolean {
        var result =  ContextCompat.checkSelfPermission(mcon!!.applicationContext,
            Manifest.permission.CAMERA)
        var resultone = ContextCompat.checkSelfPermission(mcon!!.applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var resulttwo = ContextCompat.checkSelfPermission(mcon!!.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED&&
                resultone == PackageManager.PERMISSION_GRANTED &&
                resulttwo == PackageManager.PERMISSION_GRANTED
    }

    fun  requestPermission() {
        requestPermissions(perms, PERMISSION_REQUEST_CODE);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                var cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                var writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                var readAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted && writeAccepted && readAccepted){
                    if(permission_type.equals("download")){
                        ImageDownloaderTask(this,curl, context).execute(curl)
                    }else {
                        val intent = Intent(activity, BarcodeScan::class.java)
                        intent.putExtra("type", "qrcode")
                        startActivity(intent)
                        activity!!.finish()
                    }
                }
                else {
                    // Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) &&
                            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            val positiveButtonClick= { dialog: DialogInterface, which: Int ->
                                requestPermissions(perms, PERMISSION_REQUEST_CODE
                                )
                            }
                            Common().showMessageOKCancel("You need to allow access to all permission",
                                positiveButtonClick,mcon)

                            return
                        }
                    }

                }
            }

        }
    }
    lateinit  var url:String
    var curl:String=""
    var permission_type:String=""
    var mApplication: MApplication?=null
    var mcon: Context?=null
    private var mListener: OnFragmentInteractionListener? = null
    private val RC_BARCODE_CAPTURE = 9001
    @BindView(com.tipbox.app.R.id.title) lateinit var title: TextView
    @BindView(com.tipbox.app.R.id.ly_scan) lateinit var ly_scan: LinearLayout
    @BindView(com.tipbox.app.R.id.ly_tips) lateinit var ly_tips: LinearLayout
    @BindView(com.tipbox.app.R.id.ly_tips_yr) lateinit var ly_tips_yr: LinearLayout
    @BindView(com.tipbox.app.R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(com.tipbox.app.R.id.scans) lateinit var scans: TextView
    @BindView(com.tipbox.app.R.id.tips) lateinit var tips: TextView
    @BindView(com.tipbox.app.R.id.tips_y) lateinit var tips_y: TextView
    @BindView(com.tipbox.app.R.id.qr_code) lateinit var qr_code: ImageView
    @BindView(com.tipbox.app.R.id.scan) lateinit var lscan: LinearLayout
    var perms = arrayOf("android.permission.CAMERA")
    val PERMISSION_REQUEST_CODE = 200
}
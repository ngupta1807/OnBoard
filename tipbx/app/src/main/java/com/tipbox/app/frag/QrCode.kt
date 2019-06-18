package com.tipbox.app.frag


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.tipbox.app.MainActivity
import com.tipbox.app.BarcodeScan
import com.tipbox.app.R
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.util.Common
import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls
import com.tipbox.app.util.Utils
import kotlinx.android.synthetic.main.popup_setting.*
import org.json.JSONObject




class QrCode : Fragment() ,View.OnClickListener, ApiCallback {
    override fun result(res: String) {
        var obj=JSONObject(res)
        if(type.equals("DASH")) {

            if (!obj.getString("status").equals("0")) {
                var board = obj.getJSONObject("UserDashBoard")
                if (board.getString("numberOfScanThisMonthByUser").contains("."))
                    scans.setText(board.getString("numberOfScanThisMonthByUser").split(".")[0])
                else
                    scans.setText(board.getString("numberOfScanThisMonthByUser"))
                if (board.getString("numberOfScanThisMonthByUser").contains("."))
                    tips.setText(board.getString("totalTipAmountThisMonthByUser").split(".")[0])
                else
                    tips.setText(board.getString("totalTipAmountThisMonthByUser"))
                if (board.getString("numberOfScanThisMonthByUser").contains("."))
                    tips_y.setText(board.getString("totalTipAmountThisYearByUser").split(".")[0])
                else
                    tips.setText(board.getString("totalTipAmountThisMonthByUser"))
                getSwipeSetting()
            } else {
                Toast.makeText(mcon, obj.getString("message"), Toast.LENGTH_SHORT).show()
            }
        }
        else{
            if(obj.getInt("status")==0){
                mApplication!!.storeStringInPreference("swipe_amt","")
            }
            else{
                var amt = obj.getJSONObject("UserSwipeSettings").getString("userSwipeAmt")
                mApplication!!.storeStringInPreference("swipe_amt",amt)
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.tipbox.app.R.id.read_barcode -> {
                Log.v("::","swipe_amt:"+mApplication!!.getStringFromPreference("swipe_amt"))
                if(mApplication!!.getStringFromPreference("swipe_amt").equals("")){
                    showDialog("Please first setup swipe setting amount")
                }else{
                    if (checkPermission()) {
                        val intent = Intent(activity, BarcodeScan::class.java)
                        intent.putExtra("type","qrcode")
                        startActivity(intent)
                        activity!!.finish()
                    } else {
                        requestPermission()
                    }
                }
            }
            com.tipbox.app.R.id.ly_scan -> {
               /* mcon!!.startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", "5"))
                activity!!.finish()*/
            }
            com.tipbox.app.R.id.ly_tips -> {
                mcon!!.startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", "5"))
                activity!!.finish()
            }
            com.tipbox.app.R.id.ly_tips_yr -> {
                mcon!!.startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", "5"))
                activity!!.finish()
            }
        }
    }

    fun  checkPermission():Boolean {
        var result =  ContextCompat.checkSelfPermission(mcon!!.applicationContext, Manifest.permission.CAMERA)
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun  requestPermission() {
        requestPermissions(perms, PERMISSION_REQUEST_CODE);
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                var cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (cameraAccepted){
                    val intent = Intent(activity, BarcodeScan::class.java)
                    intent.putExtra("type","qrcode")
                    startActivity(intent)
                    activity!!.finish()
                }
                //Snackbar.make(view, "Permission Granted, Now you can access location data and camera.", Snackbar.LENGTH_LONG).show()
                else {
                    // Snackbar.make(view, "Permission Denied, You cannot access location data and camera.", Snackbar.LENGTH_LONG).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            val positiveButtonClick= { dialog: DialogInterface, which: Int ->
                                requestPermissions(perms, PERMISSION_REQUEST_CODE
                                )
                            }
                            Common().showMessageOKCancel("You need to allow access to camera permission",
                                positiveButtonClick,mcon)

                            return
                        }
                    }

                }
            }

        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_qrcode, container, false)
        ButterKnife.bind(this, view)
        mcon=activity
        mApplication = activity!!.application as MApplication
        getDashBoard()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var imageViewTarget = DrawableImageViewTarget(read_barcode);
        Glide.with(this).load(com.tipbox.app.R.drawable.dashboard_animated_logo).into(imageViewTarget);

        read_barcode.setOnClickListener(this)

        ly_scan.setOnClickListener(this)
        ly_tips.setOnClickListener(this)
        ly_tips_yr.setOnClickListener(this)

        val ly_scan_bg = ly_scan.getBackground() as LayerDrawable
        val shape = ly_scan_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape.setColor(ContextCompat.getColor(activity!!.applicationContext, com.tipbox.app.R.color.colorPrimary))

        val ly_tips_bg = ly_tips.getBackground() as LayerDrawable
        val shape_ly_tips = ly_tips_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape_ly_tips.setColor(ContextCompat.getColor(activity!!.applicationContext, com.tipbox.app.R.color.white))

        val ly_tips_yr_bg = ly_tips_yr.getBackground() as LayerDrawable
        val shape_ly_tips_yr = ly_tips_yr_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape_ly_tips_yr.setColor(ContextCompat.getColor(activity!!.applicationContext, com.tipbox.app.R.color.tips_yr))

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
        type="DASH"
        if (Utils.networkStatus(activity!!)) {
                ApiResponse(progressbar, this@QrCode, mcon, ServerUrls.DASHBOARD).response(
                    mApplication!!.getRestClient()!!.riseService.viewDashboard(
                        JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                    ), mApplication!!
                )
            }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSwipeSetting() {
        type="GET"
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@QrCode, mcon, ServerUrls.SWIPESETTING).response(
                mApplication!!.getRestClient()!!.riseService.viewSwipeSetting(
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
    fun showDialog( textMsg: String) {
        val dialog = Dialog(mcon)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false)
        dialog.setContentView(com.tipbox.app.R.layout.popup_setting)

        val dialogButton = dialog.findViewById(com.tipbox.app.R.id.cancel) as TextView
        val titl_txt = dialog.findViewById(com.tipbox.app.R.id.titl_txt) as TextView
        val p_icon = dialog.findViewById(com.tipbox.app.R.id.p_icon) as ImageView
        val amt = dialog.findViewById(com.tipbox.app.R.id.amt) as EditText
        val singleframe = dialog.findViewById(com.tipbox.app.R.id.singleframe) as LinearLayout
        val doubleframe = dialog.findViewById(com.tipbox.app.R.id.doubleframe) as LinearLayout
        val _txt = dialog.findViewById(com.tipbox.app.R.id._txt) as TextView
        _txt.visibility=View.GONE
        p_icon.visibility=View.GONE
        amt.visibility=View.GONE
        doubleframe.visibility=View.GONE
        singleframe.visibility=View.VISIBLE
        titl_txt.setText(textMsg)
        dialogButton.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val save = dialog.findViewById(com.tipbox.app.R.id.save) as TextView
        save.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        val ok = dialog.findViewById(com.tipbox.app.R.id.ok) as TextView
        ok.setText("Ok")
        ok.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
            startActivity(Intent(activity, MainActivity::class.java)
                .putExtra("activity", "17")
                .putExtra("p_data","")
                )
            activity!!.finish()
        })

        dialog.show()

    }
    var mApplication: MApplication?=null
    var mcon: Context?=null
    private var mListener: OnFragmentInteractionListener? = null
    private val RC_BARCODE_CAPTURE = 9001
    @BindView(com.tipbox.app.R.id.read_barcode) lateinit var read_barcode: ImageView
    @BindView(com.tipbox.app.R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(com.tipbox.app.R.id.ly_scan) lateinit var ly_scan: LinearLayout
    @BindView(com.tipbox.app.R.id.ly_tips) lateinit var ly_tips: LinearLayout
    @BindView(com.tipbox.app.R.id.ly_tips_yr) lateinit var ly_tips_yr: LinearLayout
    @BindView(com.tipbox.app.R.id.scans) lateinit var scans: TextView
    @BindView(com.tipbox.app.R.id.tips) lateinit var tips: TextView
    @BindView(com.tipbox.app.R.id.tips_y) lateinit var tips_y: TextView
    lateinit  var type: String
    var perms = arrayOf("android.permission.CAMERA")
    val PERMISSION_REQUEST_CODE = 200

}
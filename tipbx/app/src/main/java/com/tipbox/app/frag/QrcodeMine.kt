package com.tipbox.app.frag


import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.tipbox.app.restclient.ApiResponse
import org.json.JSONObject
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.*
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.interfce.BitmapCallback
import com.tipbox.app.util.*
import java.io.File
import java.io.FileOutputStream



class QrcodeMine : Fragment() , ApiCallback,View.OnClickListener,
    BitmapCallback {
    var perms = arrayOf("android.permission.READ_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE")
    val PERMISSION_REQUEST_CODE = 200

    override fun result(res: Bitmap) {
        Common().saveTempBitmap(res,mcon)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            com.tipbox.app.R.id.btn_full_screen -> {
                showDialog()
            }
            com.tipbox.app.R.id.share -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name).toString()+" "+ "Qr Code")
                sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                sendIntent.type = "text/plain"
                startActivity(sendIntent)
            }
            com.tipbox.app.R.id.download -> {
                if (checkPermission()) {
                    ImageDownloaderTask(this@QrcodeMine,url, context).execute(url)
                } else {
                    requestPermission()
                }
            }
        }
    }
    fun  checkPermission():Boolean {
        var result = ContextCompat.checkSelfPermission(mcon!!.applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        var result2 = ContextCompat.checkSelfPermission(mcon!!.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return result == PackageManager.PERMISSION_GRANTED  &&
                result2 == PackageManager.PERMISSION_GRANTED
    }

    fun  requestPermission() {
        requestPermissions(perms, PERMISSION_REQUEST_CODE);
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                var locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted){
                    ImageDownloaderTask(this@QrcodeMine,url, context).execute(url)
                }
                else {
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            val positiveButtonClick= { dialog: DialogInterface, which: Int ->
                                requestPermissions(perms, PERMISSION_REQUEST_CODE
                                )
                            }
                            Common().showMessageOKCancel("You need to allow access to both the permissions",
                                positiveButtonClick,mcon)

                            return
                        }
                    }

                }
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

        val popup_lay = dialog.findViewById(com.tipbox.app.R.id.popup_lay) as LinearLayout

        val ly_scan_bg = popup_lay.getBackground() as LayerDrawable
        val shape = ly_scan_bg.findDrawableByLayerId(com.tipbox.app.R.id.gradientBoxDrawble) as GradientDrawable
        shape.setColor(ContextCompat.getColor(activity!!.applicationContext, R.color.colorPrimary))


        dialogClose.setOnClickListener(View.OnClickListener {
            dialog.dismiss()
        })

        Glide.with(this).load(url).into(object : SimpleTarget<Drawable>() {
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
            if(!board.getString("qrCode").equals("null",true)) {
                url=board.getString("qrCodeColored").replace("\"//","/")
                Glide.with(this).load(url).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            qr_code.background = resource
                        }
                    }
                })
            }else{
                Toast.makeText(mcon, obj.getString("message"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_myqrcode, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mcon=activity
        mApplication = activity!!.application as MApplication
        share.setOnClickListener(this)
        download.setOnClickListener(this)
        btn_full_screen.setOnClickListener(this)
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
            ApiResponse(progressbar, this@QrcodeMine, mcon, ServerUrls.DASHBOARD).response(
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
    @BindView(com.tipbox.app.R.id.download) lateinit var download: TextView
    @BindView(com.tipbox.app.R.id.share) lateinit var share: TextView
    @BindView(com.tipbox.app.R.id.qr_code) lateinit var qr_code: ImageView
    @BindView(com.tipbox.app.R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(com.tipbox.app.R.id.btn_full_screen) lateinit var btn_full_screen: FrameLayout
}
package com.tipbox.app

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.widget.*
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls
import com.tipbox.app.util.Utils
import org.json.JSONObject
import java.io.IOException

class BarcodeScan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)
        initViews()

    }

    private fun initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue)
        surfaceView = findViewById(R.id.surfaceView)
        topLayout = findViewById(R.id.topLayout)
        progressbar = findViewById(R.id.progressbar) as ProgressBar
        val nav = findViewById<View>(R.id.nav) as ImageView
        nav.setOnClickListener {
            onBackPressed()
        }
        uploaddata=0
        mApplication = this.application as MApplication
        mcon=this
    }

    override fun onBackPressed() {
        try {
            if (intent.getStringExtra("type").equals("setting")) {
                startActivity(Intent(this@BarcodeScan, MainActivity::class.java).putExtra("activity", "3"))
                finish()
            } else {
                startActivity(Intent(this@BarcodeScan, MainActivity::class.java).putExtra("activity", ""))
                finish()
            }
        }catch(ex:Exception){
            startActivity(Intent(this@BarcodeScan, MainActivity::class.java).putExtra("activity", ""))
            finish()
        }
    }
    private fun initialiseDetectorsAndSources() {
        barcodeDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .build()

        cameraSource = CameraSource.Builder(this, barcodeDetector!!)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()
        //Toast.makeText(applicationContext, "Barcode scanner started", Toast.LENGTH_SHORT).show()
        surfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this@BarcodeScan,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        cameraSource!!.start(surfaceView.holder)
                    } else {
                        ActivityCompat.requestPermissions(
                            this@BarcodeScan,
                            arrayOf(Manifest.permission.CAMERA),
                            REQUEST_CAMERA_PERMISSION
                        )
                    }

                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource!!.stop()
            }
        })


        barcodeDetector!!.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                /*Toast.makeText(
                    applicationContext,
                    "To prevent memory leaks barcode scanner has been stopped",
                    Toast.LENGTH_SHORT
                ).show()*/
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post {
                        if (barcodes.valueAt(0).email != null) {
                            txtBarcodeValue.removeCallbacks(null)
                            intentData = barcodes.valueAt(0).email.address
                            txtBarcodeValue.text = intentData
                            isEmail = true
                            uploadData(txtBarcodeValue.text.toString())
                        } else {
                            isEmail = false
                            intentData = barcodes.valueAt(0).displayValue
                            txtBarcodeValue.text = intentData
                            uploadData(txtBarcodeValue.text.toString())
                        }
                    }
                }
            }
        })
    }


    override fun onPause() {
        super.onPause()
        cameraSource!!.release()

    }

    override fun onResume() {
        super.onResume()
         initialiseDetectorsAndSources()

    }

    fun uploadData(value:String){
        if(uploaddata==0) {
            uploaddata = 1
            if (Utils.networkStatus(this)) {
                ApiResponse(object : ApiCallback {
                    override fun result(res: String) {
                        var obj = JSONObject(res)
                        if (obj.getInt("status")==0) {
                            progressbar.visibility = View.GONE
                            showDialog("Please scan a valid QR code")
                        } else {
                            /*val intent = Intent(mcon, QrCodeSwipe::class.java)
                            intent.putExtra("p_data", obj.getJSONObject("PerformerProfile").toString())
                            startActivity(intent)
                            finish()*/
                            startActivity(Intent(this@BarcodeScan, MainActivity::class.java)
                                .putExtra("activity", "17")
                                .putExtra("p_data",obj.getJSONObject("PerformerProfile").toString())
                            )
                            finish()

                        }
                    }
                }, this, ServerUrls.PERFORMERPROFILE, progressbar).response(
                    mApplication!!.getRestClient()!!.riseService.viewPerformerDetail(value)
                    , mApplication!!
                )
            } else {
                Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun showDialog(textMsg: String) {
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
            val intent = Intent(this, BarcodeScan::class.java)
            startActivity(intent)
            finish()
        })

        dialog.show()

    }
    companion object {
        private val REQUEST_CAMERA_PERMISSION = 201
        private val RC_HANDLE_CAMERA_PERM = 2
    }
    lateinit var surfaceView: SurfaceView
    lateinit var txtBarcodeValue: TextView
    private var barcodeDetector: BarcodeDetector? = null
    private var cameraSource: CameraSource? = null
    private var topLayout: RelativeLayout? = null
    internal var intentData = ""
    internal var isEmail = false
    lateinit var progressbar: ProgressBar
    var mApplication: MApplication?=null
    lateinit  var mcon: Context
    var uploaddata:Int=0
}
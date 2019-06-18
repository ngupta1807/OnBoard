package com.tipbox.app.frag


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.tipbox.app.Login
import com.tipbox.app.MainActivity
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException


class ReceivingTipSet : Fragment() ,View.OnClickListener,ApiCallback  {
    override fun result(res: String) {
        if (res.contains("successfully",true)) {
            startActivity(Intent(activity, MainActivity::class.java)
                .putExtra("activity", "16"))
            activity!!.finish()
        }
        else{
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            com.tipbox.app.R.id.skip -> {
                startActivity(Intent(activity, MainActivity::class.java)
                    .putExtra("activity", "16"))
                activity!!.finish()
            }
            com.tipbox.app.R.id.add -> {
                if (checkPermission()) {
                    showPictureDialog()
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
        var result1 = ContextCompat.checkSelfPermission(mcon!!.applicationContext, Manifest.permission.CAMERA)
        var result2 = ContextCompat.checkSelfPermission(mcon!!.applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED &&
                result2 == PackageManager.PERMISSION_GRANTED
    }

    fun  requestPermission() {
        requestPermissions(perms, PERMISSION_REQUEST_CODE);
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                var locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                var cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (locationAccepted && cameraAccepted){
                    showPictureDialog()
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
                            Common().showMessageOKCancel("You need to allow access to both the permissions",
                                positiveButtonClick,mcon)

                            return
                        }
                    }

                }
            }

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.tipbox.app.R.layout.fragment_receivetip_set, container, false)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var boldTypeface = Typeface.create(tv.getTypeface(), Typeface.BOLD)
        var regularTypeface = Typeface.create(tv.getTypeface(), Typeface.NORMAL)

        var ssb = SimpleSpanBuilder("To increase your ", CustomTypefaceSpan(regularTypeface))
        ssb += SimpleSpanBuilder.Span(
            "tips", CustomTypefaceSpan(boldTypeface)
        )
        ssb += SimpleSpanBuilder.Span(
            "\n upload your picture", CustomTypefaceSpan(regularTypeface)
        )
        tv.text = ssb.build()
        tv.setAllCaps(false)
        mcon=activity
        mApplication = activity!!.application as MApplication
        skip.setOnClickListener(this)
        add.setOnClickListener(this)

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
    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this.activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMER)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        var inputStream = this.activity!!.contentResolver.openInputStream(contentURI);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        saveImage(bitmap)
                        Toast.makeText(this.activity, getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this.activity, getString(R.string.image_save_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (requestCode == CAMER) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                logo.setBackground(BitmapDrawable(this.getResources(), thumbnail))
                saveImage(thumbnail)
                Toast.makeText(this.activity, getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveImage(myBitmap: Bitmap) {
        var stream = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        savedImage=android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
        uploadImage()
    }

    private fun uploadImage() {
        if (Utils.networkStatus(activity!!)) {
            ApiResponse(progressbar, this@ReceivingTipSet, mcon, ServerUrls.EDITPROFILE).response(
                mApplication!!.getRestClient()!!.riseService.updateProfileImage(
                    ApiParameter().updateImageData(savedImage),
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }
    private var mListener: OnFragmentInteractionListener? = null
    var mApplication: MApplication?=null
    var mcon: Context?=null
    @BindView(com.tipbox.app.R.id.tv) lateinit var tv: TextView
    @BindView(com.tipbox.app.R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(com.tipbox.app.R.id.skip) lateinit var skip: TextView
    @BindView(com.tipbox.app.R.id.add) lateinit var add: Button
    @BindView(com.tipbox.app.R.id.logo) lateinit var logo: RelativeLayout
    var perms = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA","android.permission.READ_EXTERNAL_STORAGE")
    val PERMISSION_REQUEST_CODE = 200
    private val GALLERY = 1
    private val CAMER = 2
    var savedImage:String=""
    lateinit var bitmap: Bitmap
}
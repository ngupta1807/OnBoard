package com.tipbox.app.frag

import android.Manifest.permission.*
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.tipbox.app.MainActivity
import com.tipbox.app.R
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*
import org.json.JSONObject
import java.io.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Profile.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class Profile : Fragment(), View.OnClickListener, ApiCallback {
    override fun result(res: String) {
        if (res.contains("successfully",true)) {
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
            Common().changeFragment(activity,"")
        } else {
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }
    }

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var mListener: OnFragmentInteractionListener? = null
    @BindView(R.id.fname)
    lateinit var fname: EditText
    @BindView(R.id.lname)
    lateinit var lname: EditText
    @BindView(R.id.mname)
    lateinit var mname: EditText
    @BindView(R.id.pno)
    lateinit var pno: EditText
    @BindView(R.id.txt_email_add)
    lateinit var txt_email_add: EditText
    @BindView(R.id.add)
    lateinit var add: Button
    @BindView(R.id.progressbar)
    lateinit var progressbar: ProgressBar
    var mValidate: Validate?=null

    var mApplication: MApplication?=null
    var util: PhoneNumberUtil?=null
    var mcon: Context?=null
    @BindView(R.id.action_login)
    lateinit var action_registor: Button
    @BindView(R.id.logo)
    lateinit var imageview: RelativeLayout
    var type:String=""
    private val GALLERY = 1
    private val CAMER = 2
    var savedImage:String=""
    var perms = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA","android.permission.READ_EXTERNAL_STORAGE")
    val PERMISSION_REQUEST_CODE = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        ButterKnife.bind(this, view)
        mcon=activity
        mApplication = activity!!.application as MApplication
        util = PhoneNumberUtil.getInstance()

        mValidate = Validate(activity!!)
        action_registor.setOnClickListener(this)
        add.setOnClickListener(this)

        setData()
        return view
    }
    private fun setData( ) {
        var obj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
        Log.v("Profile","obj"+obj)
        try {
            if(!obj.getString("userNameFirst").equals("null",true))
                fname.setText(""+obj.getString("userNameFirst"))
            if(!obj.getString("userNameLast").equals("null",true) &&!obj.getString("userNameLast").equals("0") )
                lname.setText(""+obj.getString("userNameLast"))
            if(!obj.getString("email").equals("null",true))
                txt_email_add.setText(""+obj.getString("email"))
            if(!obj.getString("profileImage").equals("null",true)) {
                savedImage=obj.getString("profileImage").replace("\"//","/")
                Glide.with(this).load(savedImage).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imageview.background = resource
                        }
                    }
                })
            }else{
            }

            if(!obj.getString("userNameMiddle").equals("null",true))
                mname.setText(""+obj.getString("userNameMiddle"))
            try{
                if(!obj.getString("userPhone").equals("0") && !obj.getString("userPhone").equals("null",true)) {
                    pno.append(obj.getString("userPhone"))
                }else{
                    Log.v("aa","Name("+Common().getINCountryName())
                    pno.append("+"+(util!!.getCountryCodeForRegion(Common().getINCountryName()).toString()))
                }
            }
            catch (ex:Exception){
                Log.v("aa","Name("+Common().getINCountryName())
                pno.append("+"+(util!!.getCountryCodeForRegion(Common().getINCountryName()).toString()))
            }

        } catch ( ex:Exception) {
            Log.v("r","er:"+ex.message)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        if (mListener != null) {
            mListener!!.onFragmentInteraction(uri)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onClick(view: View) {
        if (view.id == R.id.action_login) {
            validateUser()
        }
        if (view.id == R.id.add) {
            if (checkPermission()) {
                showPictureDialog()
            } else {
                requestPermission()
            }

        }

    }

    fun  checkPermission():Boolean {
        var result = ContextCompat.checkSelfPermission(mcon!!.applicationContext, WRITE_EXTERNAL_STORAGE )
        var result1 = ContextCompat.checkSelfPermission(mcon!!.applicationContext, CAMERA)
        var result2 = ContextCompat.checkSelfPermission(mcon!!.applicationContext, READ_EXTERNAL_STORAGE)

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
                        if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
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

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }


    private fun validateUser() {
        if (Utils.networkStatus(activity!!)) {
            if (
                mValidate!!.isValidName(fname.getText().toString(), fname,mcon!!.getString(R.string.e_fname)) &&
                mValidate!!.isValidName(lname.getText().toString(), lname,mcon!!.getString(R.string.e_lname)) &&
                mValidate!!.isValidMobile(pno.getText().toString(), pno)
                //&& mValidate!!.isValidImage(savedImage,mcon)
            ) {
                type = "POST"

                ApiResponse(progressbar, this@Profile, mcon, ServerUrls.USERPROFILE).response(
                    mApplication!!.getRestClient()!!.riseService.updateUser(
                        ApiParameter().updateData(fname, lname, mname, pno,savedImage),
                        JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                    ), mApplication!!
                )
            }


        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        fun newInstance(param1: String, param2: String): Profile {
            val fragment = Profile()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            fragment.arguments = args
            return fragment
        }
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
        val galleryIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMER)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        var inputStream = this.activity!!.contentResolver.openInputStream(contentURI);
                        val bitmap = BitmapFactory.decodeStream(inputStream);
                        saveImage(bitmap)
                        //Toast.makeText(this.activity, getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
                        imageview.setBackground(BitmapDrawable(this.getResources(), bitmap))

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this.activity, getString(R.string.image_save_failed), Toast.LENGTH_SHORT).show()
                    }

                }

            }
            else if (requestCode == CAMER) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                imageview.setBackground(BitmapDrawable(this.getResources(), thumbnail))
                saveImage(thumbnail)
                //Toast.makeText(this.activity, getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun saveImage(myBitmap: Bitmap) {
        var stream = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        savedImage=android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
    }

}

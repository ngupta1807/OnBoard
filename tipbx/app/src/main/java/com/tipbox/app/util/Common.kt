package com.tipbox.app.util
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Environment
import android.util.Log
import com.tipbox.app.frag.Profile
import org.json.JSONObject
import java.io.*
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.support.v4.app.FragmentActivity
import android.support.v4.os.ConfigurationCompat
import android.widget.Toast
import com.tipbox.app.MainActivity
import com.tipbox.app.restclient.GetApiData
import java.text.SimpleDateFormat
import java.util.*
import java.text.ParseException


class Common {

    fun saveAuth(result: String, mApplication: MApplication,mcon: Context?) {
        try {
            val obj = JSONObject(result)
            val user = obj.getJSONObject("User")
            var objdata=GetApiData().saveProfileData(user).toString()

            if(GetApiData().saveProfileData(user).getInt("userType")==0)
                mApplication!!.getStringFromPreference("session").equals("give")
            else
                mApplication!!.getStringFromPreference("session").equals("receive")
            mApplication.storeStringInPreference(ServerUrls.USER, objdata)
            mApplication.storeStringInPreference(ServerUrls.AUTH, obj.getJSONObject("User").getString("authCode"))
        } catch (ex: Exception) {
            Log.v("er:..", "er ." + ex.message)
        }
    }


    fun showMessageOKCancel(s: String, positiveButtonClick: (DialogInterface, Int) -> Unit, mcon: Context?) {
        AlertDialog.Builder(mcon)
            .setMessage(s)
            .setPositiveButton("OK", DialogInterface.OnClickListener(function = positiveButtonClick))
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }


   /* fun showDialog(s: String, positiveButtonClick: (DialogInterface, Int){

    }*/

    fun getDateTime(milliSeconds: Long, dateFormat: String): String {
        val milliSeconds = milliSeconds * 1000L
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat,Locale.ENGLISH)
        //formatter.timeZone = TimeZone.getTimeZone("GMT")
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        //return formatter.parse(calendar.getTime().toString()).toString()
        return formatter.format(calendar.getTime())
    }


    fun getDate(milliSeconds: String): String {
        if(milliSeconds.contains(" ")) {
            var date = milliSeconds.split(" ")
            return date[0]
        }else{
            return milliSeconds
        }
    }

    fun getTime(milliSeconds: String): String {
        var out=""
        if(milliSeconds.contains(" ")) {
            var time = milliSeconds.split(" ")
            val dateFormat = SimpleDateFormat("HH:mm:ss")
            val dateFormat2 = SimpleDateFormat("hh:mm aa")
            try {
                val date = dateFormat.parse(time[1])
                out = dateFormat2.format(date)
                Log.e("Time", out)
            } catch (e: ParseException) {
            }

            return out
        }else{
            return ""
        }
    }

    fun saveTempBitmap(bitmap: Bitmap,mcon: Context?) {
        if (isExternalStorageWritable()) {
            saveImage(bitmap,mcon)
        } else {
            Log.v("asdas","not mounte");
        }
    }
    private fun saveImage(finalBitmap: Bitmap,mcon: Context?) {

        val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val myDir = File("$root/tipboxme")
        myDir.mkdirs()
        val fname = "qrcode.png"

        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            Toast.makeText(mcon,mcon!!.getString(com.tipbox.app.R.string.image_saved),Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(mcon,mcon!!.getString(com.tipbox.app.R.string.image_save_failed),Toast.LENGTH_SHORT).show()
        }
    }

    /* Checks if external storage is available for read and write */
    fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return if (Environment.MEDIA_MOUNTED == state) {
            true
        } else false
    }

    fun getCountryName():String?{
        var locale = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration()).get(0)
        return locale.country
    }

    fun getINCountryName():String?{
        return "IN"
    }

    fun changeFragment(activity: FragmentActivity?, value:String){
         activity!!.startActivity(Intent(activity, MainActivity::class.java).putExtra("activity", value))
         activity!!.finish()
    }

    fun getUserFullName(fname:String,mname:String,lname:String):String?{
        var fullName:String=""
        if(!fname.equals("null",true)){
            fullName=fullName+fname
        }
       /* if(!mname.equals("null",true)){
            fullName=fullName+" "+mname
        }
        if(!lname.equals("null",true)){
            fullName=fullName+" "+lname
        }*/
        return fullName
    }

    fun clearData(mApplication:MApplication){
        mApplication.storeStringInPreference(ServerUrls.AUTH, "")
        mApplication.storeStringInPreference(ServerUrls.USER, "")
        mApplication.storeStringInPreference(ServerUrls.SESSION, "")
        mApplication.storeStringInPreference(ServerUrls.SWIPEAMT, "")
        mApplication.storeStringInPreference(ServerUrls.INFO, "")
    }


}
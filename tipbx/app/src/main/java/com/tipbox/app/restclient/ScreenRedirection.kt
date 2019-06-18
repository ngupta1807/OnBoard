package com.tipbox.app.restclient

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.support.v4.content.ContextCompat.startActivity
import android.widget.EditText
import android.widget.Toast
import com.tipbox.app.Login
import com.tipbox.app.MainActivity
import com.tipbox.app.R

class ScreenRedirection(var result: String,var mcon: Context?) {
   /* fun loginData(){
        if (result.contains("Successfully")) {
            startActivity(Intent(this.mcon, MainActivity::class.java))
            ((activity) mcon).finish()
        }
        else{
            Toast.makeText(mcon, R.string.try_again, Toast.LENGTH_SHORT).show()
        }
    }*/

    fun signupData(email: EditText, pwd: EditText, mcon: Context?):HashMap<String,String>{
        val androidId = Settings.Secure.getString(
            mcon?.getContentResolver(),
            Settings.Secure.ANDROID_ID
        )
        var hm= HashMap<String,String>()
        hm.put("email",email.getText().toString())
        hm.put("userPassword",pwd.getText().toString())
        hm.put("uuid",androidId)
        hm.put("deviceType","Android")
        return hm;
    }

    fun forgotData( email: EditText):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("email",email.getText().toString())
        return hm;
    }

    fun changePwdData(email: EditText, pwd: EditText):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("newUserPassword",email.getText().toString())
        hm.put("userPassword",pwd.getText().toString())
        return hm;
    }
}
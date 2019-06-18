package com.tipbox.app


import android.content.Intent
import android.icu.text.Normalizer
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.EditText
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import com.crashlytics.android.Crashlytics
import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls
import io.fabric.sdk.android.Fabric


class Splash : AppCompatActivity(){
    //@BindView(R.id.txt) lateinit var txt: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_splash)
        ButterKnife.bind(this)
        Fabric.with(this,  Crashlytics())
        var mApplication = application as MApplication
        Log.v("key","token:"+mApplication.getStringFromPreference(ServerUrls.SET_TOKEN));
        Log.v("key","AUTH:"+mApplication.getStringFromPreference(ServerUrls.AUTH));
        /*txt.setText(mApplication.getStringFromPreference(ServerUrls.SET_TOKEN))
        */

        var handler = Handler()
        var runnable = Runnable {
            Log.v("key","token:"+mApplication.getStringFromPreference(ServerUrls.SET_TOKEN));
            if (mApplication!!.getStringFromPreference(ServerUrls.AUTH) == "") {
                startActivity(Intent(this, Login::class.java))
                finish()
            } else {
                //startActivity(Intent(this, QrCodeSwipe::class.java))
                startActivity(Intent(this, MainActivity::class.java).putExtra("activity", ""))
                finish()
        } }
        handler.postDelayed(runnable, 5000)
    }
}

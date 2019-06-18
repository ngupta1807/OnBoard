package com.tipbox.app


import android.content.Intent
import android.graphics.drawable.Drawable
import android.icu.text.Normalizer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls





class ThankuScreen : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_thanku)
        ButterKnife.bind(this)
        if(intent.getStringExtra("company_logo").equals("null",true)) {
        }else{
            var url=intent.getStringExtra("company_logo")
            Glide.with(this).load(url).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        img.background = resource
                    }
                }
            })
        }
    }
    fun back(view: View) {
      onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java).putExtra("activity", ""))
        finish()
    }
    @BindView(R.id.logo_img) lateinit var img: ImageView

}

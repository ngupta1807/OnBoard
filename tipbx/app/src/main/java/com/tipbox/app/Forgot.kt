package com.tipbox.app


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*


class Forgot : AppCompatActivity(), ApiCallback {
    override fun result(res: String) {
        if (res.contains("successfully",true)) {
            startActivity(Intent(mcon, Login::class.java))
            finish()
        }
        else{
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }
    }

    private var mApplication: MApplication? = null
    var mValidate: Validate?=null
    @BindView(R.id.login_email_id) lateinit var login_email_id: EditText
    @BindView(R.id.move_to_register) lateinit var move_to_register: Button
    @BindView(R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(R.id.logo) lateinit var logo: ImageView
    var mcon: Context?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.ac_forgot)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        mcon=this
        mApplication = application as MApplication
        mValidate = Validate(this)

        var ssb = SimpleSpanBuilder("Already ", ForegroundColorSpan(ContextCompat.getColor(this,R.color.black)))
        ssb += SimpleSpanBuilder.Span(
            "Registered?",
            ForegroundColorSpan(ContextCompat.getColor(this,R.color.colorPrimary))
        )

        move_to_register.text = ssb.build()
        move_to_register.setAllCaps(false)
    }

    fun registor(view: View) {
        startActivity(Intent(this@Forgot, Login::class.java))
        finish()
    }
    fun forgot(view: View) {
        if (Utils.networkStatus(this)) {
            if (mValidate!!.isValidEmail(login_email_id.text.toString(), login_email_id)) {
                forgotUser()
            }
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@Forgot, Login::class.java))
        finish()
    }

    private fun forgotUser() {
        if (Utils.networkStatus(this)) {
            if (
                mValidate!!.isValidEmail(login_email_id.text.toString(), login_email_id)
            ) {
                userForgot()
            }

        } else {
            Toast.makeText(this, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun userForgot() {
            ApiResponse(progressbar, this@Forgot,mcon, ServerUrls.FORGOT).
                response(mApplication!!.getRestClient()!!.
                    riseService.ac_forgot(
                        ApiParameter().
                            forgotData(login_email_id)), mApplication!!)

    }


}

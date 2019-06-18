package com.tipbox.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import butterknife.BindView
import butterknife.ButterKnife
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*


class Registor : AppCompatActivity(), ApiCallback {
    override fun result(res: String) {
        if (res.contains("successfully",true)) {
            startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", ""))
            finish()
        }
        else{
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }
    }

    @BindView(R.id.login_email_id) lateinit var login_email_id: EditText
    @BindView(R.id.login_password) lateinit var login_password: EditText
    @BindView(R.id.move_to_register) lateinit var move_to_register: Button
    @BindView(R.id.progressbar) lateinit var progressbar: ProgressBar
    var mValidate: Validate?=null
    var mcon: Context?=null
    @BindView(R.id.eye) lateinit var eye: ImageView
    private var mApplication: MApplication? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.ac_registor)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        init()
    }

    private fun init() {
        mApplication = application as MApplication
        mcon=this
        mValidate = Validate(this)
        var ssb = SimpleSpanBuilder("Already", ForegroundColorSpan(ContextCompat.getColor(this,R.color.black)))
        ssb += SimpleSpanBuilder.Span(
            " Registered?",
            ForegroundColorSpan(ContextCompat.getColor(this,R.color.colorPrimary))
        )

        move_to_register.text = ssb.build()
        move_to_register.setAllCaps(false)
    }
    fun clickEyes(view: View) {
        if(eye.getTag().equals("hide")){
            eye.setBackgroundResource(R.drawable.login_input_cross_eyes_icon)
            login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
            eye.setTag("view")
        }else{
            eye.setBackgroundResource(R.drawable.login_input_eyes_icon)
            login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            eye.setTag("hide")
        }
        login_password.setSelection(login_password.length())
    }

    fun registor(v: View) {
        startActivity(Intent(this@Registor, Login::class.java))
        finish()
    }

    fun forgot(v: View) {
        startActivity(Intent(this@Registor, Forgot::class.java))
        finish()
    }

    fun login(v: View) {
        validateUser()
    }


    private fun validateUser() {
        if (Utils.networkStatus(this)) {
            if (
                mValidate!!.isValidEmail(login_email_id.text.toString(), login_email_id) &&
                mValidate!!.isValidPassword(login_password.text.toString(), login_password)
            ) {
                userRegistor()
            }

        } else {
            Toast.makeText(this, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun userRegistor() {
            ApiResponse(progressbar, this@Registor,mcon, ServerUrls.REGISTOR).
                response(mApplication!!.getRestClient()!!.
                    riseService.createUser(
                    ApiParameter().
                        signupData(login_email_id,login_password,mcon,mApplication!!.getStringFromPreference(ServerUrls.SET_TOKEN))), mApplication!!)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@Registor, Login::class.java))
        finish()
    }
}

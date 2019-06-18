package com.tipbox.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.style.ForegroundColorSpan
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.restclient.ApiParameter
import com.tipbox.app.restclient.ApiResponse
import com.tipbox.app.util.*


class Login : AppCompatActivity(), ApiCallback {
    override fun result(res: String) {
        if (res.contains("successfully",true)) {
            startActivity(Intent(mcon, MainActivity::class.java).putExtra("activity", ""))
            finish()
        }
        else{
            Toast.makeText(mcon, res, Toast.LENGTH_SHORT).show()
        }
    }

    private var mApplication: MApplication? = null

    @BindView(R.id.login_email_id) lateinit var login_email_id: EditText
    @BindView(R.id.login_password) lateinit var login_password: EditText
    @BindView(R.id.move_to_register) lateinit var move_to_register: Button
    @BindView(R.id.progressbar) lateinit var progressbar: ProgressBar
    @BindView(R.id.eye) lateinit var eye: ImageView
    lateinit var mcon: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_login)
        mcon=this
        ButterKnife.bind(this)
        init()
    }

    private fun init() {
        mApplication = application as MApplication
        var ssb = SimpleSpanBuilder("New User? ",ForegroundColorSpan(ContextCompat.getColor(this,R.color.black)))
        ssb += SimpleSpanBuilder.Span(
            "Sign Up",
            ForegroundColorSpan(ContextCompat.getColor(this,R.color.colorPrimary))
        )

        move_to_register.text = ssb.build()
        move_to_register.setAllCaps(false)
       /* var apicall=CommonApi(mcon,mApplication,progressbar,this)
        apicall.getProfileImage()*/

    }

    fun login(v: View) {
        loginUser()
    }

    fun forgot(v: View) {
        startActivity(Intent(mcon, Forgot::class.java))
        finish()
    }

    fun registor(view: View) {
        startActivity(Intent(this@Login, Registor::class.java))
        finish()
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



    private fun loginUser() {
        if (Utils.networkStatus(this)) {
            if ( Validate(this@Login).isValidEmail(login_email_id.text.toString(), login_email_id) &&
                Validate(this@Login).isValidPassword(login_password.text.toString(), login_password)) {
                userLogin()
            }
        } else {
            Toast.makeText(mcon, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    private fun userLogin() {
            ApiResponse(progressbar, this@Login,mcon, ServerUrls.LOGIN).
                response(mApplication!!.getRestClient()!!.
                    riseService.ac_login(ApiParameter().
                    loginData(login_email_id,login_password,mApplication!!.getStringFromPreference(ServerUrls.SET_TOKEN))), mApplication!!)


    }



}

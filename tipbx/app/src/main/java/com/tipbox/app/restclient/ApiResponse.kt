package com.tipbox.app.restclient

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import com.google.gson.Gson

import com.tipbox.app.interfce.ApiCallback
import com.tipbox.app.util.Common
import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.tipbox.app.Login
import com.tipbox.app.util.ServerUrls.EDITPROFILE
import com.tipbox.app.util.ServerUrls.SESSION
import com.tipbox.app.util.ServerUrls.SWIPEAMT
import com.tipbox.app.util.ServerUrls.TRANSFERAMOUNT


class ApiResponse {
    lateinit var callback: ApiCallback
    lateinit var progressDialog: ProgressBar
    var mcon: Context? = null
    var type: String? = null
    var loader:ImageView?=null
    var api_callback :Call<Any>?=null
    constructor(progressDialog: ProgressBar, callback: ApiCallback,
                mcon: Context?, type: String){
        this.callback=callback
        this.progressDialog=progressDialog
        this.mcon=mcon
        this.type=type
        progressDialog.visibility = View.VISIBLE
        api_callback?.cancel()
    }

    constructor(callback: ApiCallback,
                mcon: Context?, type: String, loader:ImageView){
        this.callback=callback
        this.mcon=mcon
        this.type=type
        this.loader=loader
        loader!!.visibility=View.VISIBLE
        api_callback?.cancel()
    }

    constructor(callback: ApiCallback,
                mcon: Context?, type: String, loader:ProgressBar){
        this.callback=callback
        this.mcon=mcon
        this.type=type
        this.progressDialog=loader
        progressDialog!!.visibility=View.VISIBLE
        api_callback?.cancel()
    }


    fun response(call: Call<Any>, mApplication: MApplication) {
        call.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                api_callback = call
                if(type == ServerUrls.LOGOUT)
                    loader!!.visibility=View.GONE
                else if(type == ServerUrls.PERFORMERPROFILE)
                    progressDialog.visibility = View.INVISIBLE
                else
                    progressDialog.visibility = View.INVISIBLE
                    try {
                    if (response.isSuccessful) {
                        val result = Gson().toJson(response.body())
                        val obj = JSONObject(result)
                        if (obj.getString("status").equals("1.0")) {
                            manageResult(result, mApplication,mcon,obj)
                        } else {
                            manageResult(result, mApplication,mcon,obj)
                        }
                    } else {
                        try {
                            response.code()
                            var res=response.errorBody().string() //405
                            val obj = JSONObject(res)
                            if(response.code()==405){
                                Common().clearData(mApplication)
                                showDialog(obj.getString("message"))
                            } else{
                                if (type == ServerUrls.NOTIFICATION || type == ServerUrls.PAYHISTORY || type == ServerUrls.SWIPESETTING
                                    || type == ServerUrls.PERFORMERPROFILE
                                ) {
                                    callback.result(res)

                                }else {
                                    callback.result(obj.getString("message"))
                                }
                            }
                        } catch (ex: Exception) {
                            Log.v("result:", "message ex::" + ex.message)
                        }
                    }
                } catch (ex: Exception) {
                    Log.v("result:", "message ex::" + ex.message)
                }

            }

            override fun onFailure(call: Call<Any>, t: Throwable) {

                Log.v("App","eror:"+t.printStackTrace())

                call.cancel()
                if(type == ServerUrls.LOGOUT)
                    loader!!.visibility=View.GONE
                else if(type == ServerUrls.PERFORMERPROFILE)
                    progressDialog.visibility = View.INVISIBLE
                else
                    progressDialog.visibility = View.INVISIBLE
                Toast.makeText(mcon, com.tipbox.app.R.string.server_error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun manageResult(result: String, mApplication: MApplication,mcon:Context?,obj:JSONObject) {
            if (type == ServerUrls.LOGIN || type == ServerUrls.EDITPROFILE) {
                Common().saveAuth(result, mApplication, mcon)
                callback.result(obj.getString("message"))
            } else if (type == ServerUrls.REGISTOR) {
                Common().saveAuth(result, mApplication, mcon)
                callback.result(obj.getString("message"))
            } else if (type == ServerUrls.USERPROFILE) {
                Common().saveAuth(result, mApplication, mcon)
                callback.result(obj.getString("message"))
            } else if (type == ServerUrls.FORGOT) {
                callback.result(obj.getString("message"))
            } else if (type == ServerUrls.CHANGEPASSWORD ||  type == ServerUrls.TRANSFERAMOUNT) {
                callback.result(obj.getString("message"))
            } else if (type == ServerUrls.LOGOUT) {
                Common().clearData(mApplication)
                if (obj.getString("message").contains("successfully",true)) {
                    (mcon as Activity).startActivity(Intent(mcon, Login::class.java))
                    (mcon as Activity).finish()
                    //showDialog("logged out")
                } else {
                    Toast.makeText(mcon, com.tipbox.app.R.string.try_again, Toast.LENGTH_SHORT).show()
                }

            } else if (type == ServerUrls.DASHBOARD) {
                callback.result(result)
            } else if (type == ServerUrls.NOTIFICATION || type == ServerUrls.PAYHISTORY || type == ServerUrls.PERFORMERPROFILE) {
                callback.result(result)
            } else if (type == ServerUrls.BANKDETAIL) {
                callback.result(result)
            } else if (type == ServerUrls.SWIPESETTING ) {
                callback.result(result)
            }
    }

    fun showDialog( textMsg: String) {
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
            (mcon as Activity).startActivity(Intent(mcon, Login::class.java))
            (mcon as Activity).finish()

        })

        dialog.show()

    }


}

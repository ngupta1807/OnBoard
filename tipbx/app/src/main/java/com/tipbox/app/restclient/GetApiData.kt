package com.tipbox.app.restclient

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.google.gson.JsonArray
import com.tipbox.app.MainActivity
import com.tipbox.app.modle.HistoryModle
import com.tipbox.app.modle.NotificationModle
import com.tipbox.app.util.Common
import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls
import org.json.JSONArray
import org.json.JSONObject

class GetApiData{
    fun saveProfileData(user:JSONObject):JSONObject{
        var jsondata= JSONObject()
        if(!user.has("userNameFirst"))
            jsondata.put("userNameFirst","NULL")
        else
            jsondata.put("userNameFirst",user.getString("userNameFirst"))
        if(!user.has("userNameLast"))
            jsondata.put("userNameLast","NULL")
        else
            jsondata.put("userNameLast",user.getString("userNameLast"))
        if(!user.has("userNameMiddle"))
            jsondata.put("userNameMiddle","NULL")
        else
            jsondata.put("userNameMiddle",user.getString("userNameMiddle"))
        if(!user.has("profileImage"))
            jsondata.put("profileImage","NULL")
        else
            jsondata.put("profileImage",user.getString("profileImage"))
        if(!user.has("userType"))
            jsondata.put("userType","NULL")
        else {
            jsondata.put("userType", user.getInt("userType"))

        }
        if(!user.has("userNameFirst"))
            jsondata.put("userNameFirst","NULL")
        else
            jsondata.put("userNameFirst",user.getString("userNameFirst"))
        if(!user.has("email"))
            jsondata.put("email","NULL")
        else
            jsondata.put("email",user.getString("email"))
        if(!user.has("userId"))
            jsondata.put("userId","NULL")
        else
            jsondata.put("userId",user.getInt("userId"))
        if(!user.has("userPhone"))
            jsondata.put("userPhone","NULL")
        else
            jsondata.put("userPhone",user.getString("userPhone"))
        Log.v("profile","addata"+jsondata)
        return jsondata
    }

    fun updateBankAccount(mApplication:MApplication?,mcon:FragmentActivity?){
        var saveObj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
        if(saveObj.getString("profileImage").equals("null",true)) {
            mcon!!.startActivity(
                Intent(mcon, MainActivity::class.java)
                    .putExtra("activity", "19"))
            mcon!!.finish()
        }else{
            mcon!!.startActivity(
                Intent(mcon, MainActivity::class.java)
                    .putExtra("activity", "16"))
            mcon!!.finish()
        }
    }

    fun getBankAccount(jsonObject: JSONObject?, h_name: EditText, b_name: EditText, i_code: EditText, a_number: EditText) {
        var jsondata= JSONObject()
        if(!jsonObject!!.has("accountHolderName"))
            jsondata.put("accountHolderName","")
        else
            jsondata.put("accountHolderName",jsonObject.getString("accountHolderName"))
        if(!jsonObject!!.has("bankName"))
            jsondata.put("bankName","")
        else
            jsondata.put("bankName",jsonObject.getString("bankName"))
        if(!jsonObject!!.has("bankCode"))
            jsondata.put("bankCode","")
        else
            jsondata.put("bankCode",jsonObject.getString("bankCode"))
        if(!jsonObject!!.has(ServerUrls.ACCOUNTNUMBER))
            jsondata.put(ServerUrls.ACCOUNTNUMBER,"")
        else
            jsondata.put(ServerUrls.ACCOUNTNUMBER,jsonObject.getString(ServerUrls.ACCOUNTNUMBER))

        h_name.setText(jsondata!!.getString("accountHolderName"))
        b_name.setText(jsondata!!.getString("bankName"))
        i_code.setText(jsondata!!.getString("bankCode"))
        a_number.setText(jsondata!!.getString(ServerUrls.ACCOUNTNUMBER))
    }

    fun getPerformerData(jsonObject: JSONObject?, p_name: TextView, logo:RelativeLayout,mcon:Context):JSONObject {
        var jsondata= JSONObject()
        if(!jsonObject!!.has("userNameFirst"))
            jsondata.put("userNameFirst","NULL")
        else
            jsondata.put("userNameFirst",jsonObject.getString("userNameFirst"))
        if(!jsonObject!!.has("userNameMiddle"))
            jsondata.put("userNameMiddle","NULL")
        else
            jsondata.put("userNameMiddle",jsonObject.getString("userNameMiddle"))
        if(!jsonObject!!.has("userNameLast"))
            jsondata.put("userNameLast","NULL")
        else
            jsondata.put("userNameLast",jsonObject.getString("userNameLast"))
        if(!jsonObject!!.has("profileImage"))
            jsondata.put("profileImage","NULL")
        else
            jsondata.put("profileImage",jsonObject.getString("profileImage"))
        if(!jsonObject!!.has("companyLogo"))
            jsondata.put("companyLogo","NULL")
        else
            jsondata.put("companyLogo",jsonObject.getString("companyLogo"))
        if(!jsonObject!!.has("userId"))
            jsondata.put("userId","NULL")
        else
            jsondata.put("userId",jsonObject.getString("userId"))

        if(!jsondata.getString("userNameFirst").equals("null",true)) {
            p_name.visibility= View.VISIBLE
            p_name.setText(
                Common().getUserFullName(jsondata.getString("userNameFirst"),
                    jsondata.getString("userNameMiddle"),jsondata.getString("userNameLast")))
        }
        else {
            p_name.setText("")
        }
        if(!jsondata.getString("profileImage").equals("null",true)) {
            var url=jsondata.getString("profileImage").replace("\"//","/")
            Glide.with(mcon).load(jsondata.getString("profileImage").replace("\"//","/")).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: com.bumptech.glide.request.transition.Transition<in Drawable>?) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        logo.background = resource
                    }
                }
            })
        }
        /*if(!jsondata.getString("companyLogo").equals("null",true)) {
            Glide.with(mcon).load(jsondata.getString("companyLogo")).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: com.bumptech.glide.request.transition.Transition<in Drawable>?
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        company_img.background = resource
                    }
                }
            })
        }*/
        return jsondata

    }

    fun getNotificationData(ar: JSONArray):ArrayList<NotificationModle>{
        var txtList: ArrayList<NotificationModle> =  ArrayList()
        for(i in 0 until ar.length()){
            var ob=ar.getJSONObject(i)
            var pimage:String=""
            if(ob.getJSONObject("user").has("profileImage"))
                pimage=ob.getJSONObject("user").getString("profileImage").replace("\"//","/")
            else
                pimage=""
            var modle= NotificationModle("",
                pimage,  //ob.getString("senderImage"),
                ob.getString("message"),
                "")
            txtList.add(modle)
        }
        return txtList
    }

    fun getPaymentHistory(ar:JSONArray,mApplication:MApplication?):ArrayList<HistoryModle> {
        var txtHList: ArrayList<HistoryModle> =  ArrayList()
        var uobj=JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER))
        for (i in 0 until ar.length()) {
            var ob = ar.getJSONObject(i)
            var user_name:String=""
            var type:String=""
            if(ob.getJSONObject("user").getString("userId").equals(uobj.getString("userId"))){
                user_name=ob.getJSONObject("recUser").getString("userNameFirst")
                type="receive"
            }else{
                user_name=ob.getJSONObject("user").getString("userNameFirst")
                type="send"
            }
            var modle = HistoryModle(
                Common().getDate(ob.getString("trnsDate")),
                "$" + ob.getString("trnsAmt"),
                 user_name,Common().getTime(ob.getString("trnsDate")),
                "Transaction ID - " + ob.getString("trnsId"),type
            )
            txtHList.add(modle)
        }
        return txtHList
    }
}

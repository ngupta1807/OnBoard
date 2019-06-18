package com.tipbox.app.restclient

import android.content.Context
import android.provider.Settings
import android.widget.EditText
import com.tipbox.app.util.ServerUrls


class ApiParameter {
     fun loginData( email:EditText,pwd: EditText,androidID :String):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("email",email.getText().toString())
        hm.put("userPassword",pwd.getText().toString())
        hm.put("uuid",androidID)
        hm.put("deviceType","Android")
        return hm
    }

    fun signupData( email:EditText,pwd: EditText,mcon: Context?,androidID :String):HashMap<String,String>{

        var hm= HashMap<String,String>()
        hm.put("email",email.getText().toString())
        hm.put("userPassword",pwd.getText().toString())
        hm.put("uuid",androidID)
        hm.put("deviceType","Android")
        return hm
    }

    fun forgotData( email:EditText):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("email",email.getText().toString())
        return hm;
    }

    fun updateData( fname:EditText,lname: EditText,mname: EditText,pno: EditText,data:String):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("userNameFirst",fname.getText().toString())
        hm.put("userNameLast",lname.getText().toString())
        hm.put("userNameMiddle",mname.getText().toString())
        hm.put("userPhone",pno.getText().toString())
        if(!data.equals("") && !data.startsWith("http"))
            hm.put("profileImage","data:image/png;base64,"+data)
        return hm
    }

    fun updateSwipe( price:String):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("userSwipeAmt",price)
        return hm;
    }


    fun performData(amt:String,swipe: Int,id: String):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("trnsAmt",amt)
        hm.put("numOfTrnsSwipe",""+(swipe+1))
        hm.put("recUserId",id)
        return hm
    }


    fun bankData(h_name:EditText,a_number: EditText,b_name: EditText,i_code: EditText):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("accountHolderName",h_name.getText().toString())
        hm.put(ServerUrls.ACCOUNTNUMBER,a_number.getText().toString())
        hm.put("bankName",b_name.getText().toString())
        hm.put("bankCode",i_code.getText().toString())
        return hm
    }

    fun changePwdData( email:EditText,pwd: EditText):HashMap<String,String>{
        var hm= HashMap<String,String>()
        hm.put("newUserPassword",email.getText().toString())
        hm.put("userPassword",pwd.getText().toString())
        return hm
    }

    fun updateImageData(data:String):HashMap<String,String>{
        var hm= HashMap<String,String>()
        if(!data.startsWith("http"))
            hm.put("profileImage","data:image/png;base64,"+data)
        return hm
    }

    fun logout():HashMap<String,String>{
        var hm= HashMap<String,String>()
        return hm
    }

}
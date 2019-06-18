/*
package com.tipbox.app.util
import android.content.Context
import org.json.JSONObject
import android.widget.ProgressBar
import android.widget.Toast
import com.tipbox.app.Login
import com.tipbox.app.restclient.ApiResponse


class CommonApi(
    var mcon: Context,
    var mApplication: MApplication?,
    var progressbar: ProgressBar
    ) {

    init{

    }
     constructor(mcon: Context,
     mApplication: MApplication?,
     progressbar: ProgressBar,login: Login){
        this.mcon=mcon
        this.mApplication=mApplication
        this.progressbar=progressbar
        this.login=login
    }
    public fun getProfileImage() {
        if (Utils.networkStatus(mcon)) {
            ApiResponse(progressbar, login, mcon, ServerUrls.SWIPESETTING).response(
                mApplication!!.getRestClient()!!.riseService.viewSwipeSetting(
                    JSONObject(mApplication!!.getStringFromPreference(ServerUrls.USER)).getString("userId")
                ), mApplication!!
            )
        }
        else {
            Toast.makeText(mcon, "getString(R.string.no_internet)", Toast.LENGTH_SHORT).show()
        }
    }

}*/

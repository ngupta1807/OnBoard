package com.sample.app.util


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.google.gson.Gson
import com.sample.app.R
import com.sample.app.database.BWordModel
import com.sample.app.database.DatabaseHandler
import com.sample.app.frag.BadWordList
import com.sample.app.param.Twitter


class Common(var mcon:Context) {
    val preferences = PreferenceManager.getDefaultSharedPreferences(mcon)
    fun writeData(data: String) {
        val editor = preferences.edit()
        editor.putString(mcon.getString(com.sample.app.R.string.preference_file_key), data)
        editor.commit()
    }

    fun readData(): String {
        return preferences.getString(mcon.getString(com.sample.app.R.string.preference_file_key),"")
    }

    // converts a string of JSON data into a Twitter object
    fun jsonToTwitter(result: String?): Twitter? {
        var twits: Twitter? = null
        if (result != null && result.length > 0) {
            try {
                val gson = Gson()
                twits = gson.fromJson<Twitter>(result, Twitter::class.java)
            } catch (ex: IllegalStateException) {
                // just eat the exception
            }
        }
        return twits
    }

    fun emptyTagPopup(activity: FragmentActivity?){
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder
            .setMessage("Please add HashTag from menu.")
            .setPositiveButton(mcon.getString(R.string.ok), DialogInterface.OnClickListener {
                    dialog, id ->
                    dialog.cancel()
            })

        val b = dialogBuilder.create()
        b.setTitle(mcon.getString(R.string.alert))

        b.show()
    }


}
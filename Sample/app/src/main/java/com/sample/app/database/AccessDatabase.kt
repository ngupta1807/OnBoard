package com.sample.app.database

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.widget.EditText
import android.widget.Toast
import com.sample.app.R
import com.sample.app.adapter.BadWordAdapter
import com.sample.app.frag.BadWordList
import com.sample.app.param.BWord


class AccessDatabase(private val mcon:Context) {
    //method for saving records in database
    fun saveRecord(name:String,b_name:EditText){
        val databaseHandler = DatabaseHandler(mcon) as DatabaseHandler
            val status = databaseHandler.addBWord(BWordModel(0,name))
            if(status > -1){
                Toast.makeText(mcon,mcon.getString(R.string.r_saved),Toast.LENGTH_LONG).show()
                b_name.text.clear()
            }
    }
    //method for read records from database in ListView
    fun viewRecord(badWordList: RecyclerView,callback: BadWordList){
        var txtList= ArrayList<BWord>()
        val databaseHandler= DatabaseHandler(mcon)  as DatabaseHandler
        val data: List<BWordModel> = databaseHandler.viewBWord()
        for(i in data.indices){
            var bword = BWord()
            bword.title=data[i].Name
            bword.id=data[i].Id
            txtList.add(bword)
        }
        badWordList.setAdapter(BadWordAdapter(txtList,callback))
    }
    //method for validate records from database
    fun viewRecord(tweetTxt: String): Boolean?{
        var txt: Boolean? = false
        val databaseHandler= DatabaseHandler(mcon)  as DatabaseHandler
        val data: List<BWordModel> = databaseHandler.viewBWord()
        for(i in data.indices){
            if(tweetTxt.toLowerCase().contains(data[i].Name.toLowerCase().trim())){
                txt=true
                break
            }
        }
        return  txt
    }


    fun deleteRecord(deleteId: Int, activity: FragmentActivity?,
                     badWordList: RecyclerView,callback: BadWordList,title : String){
        val dialogBuilder = AlertDialog.Builder(activity)
        dialogBuilder
         .setMessage("Do you want to delete "+ title + "!")
        .setPositiveButton(mcon.getString(R.string.delete), DialogInterface.OnClickListener {
                dialog, id ->
            val databaseHandler= DatabaseHandler(mcon)  as DatabaseHandler
            //calling the deleteBWord method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteBWord(BWordModel(deleteId,""))
            if(status > -1){
                Toast.makeText(mcon,mcon.getString(R.string.bwrd_deleted),Toast.LENGTH_LONG).show()
                viewRecord(badWordList,callback)
                dialog.cancel()
            }
        })
        .setNegativeButton(mcon.getString(R.string.cancel), DialogInterface.OnClickListener {
                dialog, id -> dialog.cancel()
        })

        val b = dialogBuilder.create()
        b.setTitle(mcon.getString(R.string.r_deleted))

        b.show()
    }
}
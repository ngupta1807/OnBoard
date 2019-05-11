package com.sample.app.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DATABASE_NAME,null,DATABASE_VERSION) {
    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "TwitterDatabase"
        private val TABLE_CONTACTS = "BadWordTable"
        private val KEY_ID = "id"
        private val KEY_NAME = "bword"
    }
    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE = ("CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT " + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }

    //method to insert data
    fun addBWord(bword: BWordModel):Long{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        //contentValues.put(KEY_ID, bword.userId)
        contentValues.put(KEY_NAME, bword.Name) //  Name
        val success = db.insert(TABLE_CONTACTS, null, contentValues)
        db.close() // Closing database connection
        return success
    }
    //method to read data
    fun viewBWord():List<BWordModel>{
        val bwordList:ArrayList<BWordModel> = ArrayList<BWordModel>()
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var name: String
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                name = cursor.getString(cursor.getColumnIndex("bword"))
                val emp= BWordModel(Id = id, Name = name)
                bwordList.add(emp)
            } while (cursor.moveToNext())
        }
        return bwordList
    }
    //method to delete data
    fun deleteBWord(bword: BWordModel):Int{
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, bword.Id) //  UserId
        val success = db.delete(TABLE_CONTACTS,"id="+bword.Id,null)
        db.close() // Closing database connection
        return success
    }
}
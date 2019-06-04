package com.example.servicetypes

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.Context.BIND_AUTO_CREATE
import android.content.ComponentName
import android.os.IBinder
import android.content.ServiceConnection
import android.view.View


class MainActivity : AppCompatActivity() {
    var myService: BoundService? = null
    var isBound = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private val MyConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as BoundService.LocalBinder
            myService = binder.service
            isBound = true
            myService!!.downloadFile("https://static.pexels.com/photos/4825/red-love-romantic-flowers.jpg")
            //myService!!.playFile()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }



    override fun onStart() {
        super.onStart()
        /*val i = Intent(this, UnboundMyService::class.java)
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service")
        startService(i)*/
        val intent = Intent(this, BoundService::class.java)
        //start service with binding
        bindService(intent, MyConnection, Context.BIND_AUTO_CREATE)

    }

    override fun onStop() {
        super.onStop()
        /*val i = Intent(this, UnboundMyService::class.java)
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service")
        stopService(i)*/
        if (isBound) {
            //unbind service
            unbindService(MyConnection)
            isBound = false
        }
    }
}

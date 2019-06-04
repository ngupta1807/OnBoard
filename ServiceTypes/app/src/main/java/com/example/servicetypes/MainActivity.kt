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
import android.widget.TextView
import android.content.BroadcastReceiver
import android.content.IntentFilter






class MainActivity : AppCompatActivity() {
    var myService: BoundService? = null
    var isBound = false
    lateinit var tv:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         tv=findViewById<TextView>(R.id.txt)
    }

    override fun onStart() {
        super.onStart()
        //startUnboundType()
        //startBoundType()
        startIntentServiceType()
    }

    override fun onStop() {
        super.onStop()
        //stopUnboundType()
        // stopBoundType()
        stopIntentServiceType()
    }


    fun startUnboundType(){
        val i = Intent(this, UnboundMyService::class.java)
        i.putExtra("KEY1", "Value to be used by the service")
        startService(i)
    }


    fun startBoundType(){
        val intent = Intent(this, BoundService::class.java)
        bindService(intent, MyConnection, Context.BIND_AUTO_CREATE)
    }

    fun startIntentServiceType(){
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.example.service")
        registerReceiver(broadcastReceiver, intentFilter)
        startService(Intent(this@MainActivity, IntentServiceType::class.java))
    }

    fun stopUnboundType(){
        val i = Intent(this, UnboundMyService::class.java)
        // potentially add data to the intent
        i.putExtra("KEY1", "Value to be used by the service")
        stopService(i)
    }


    fun stopBoundType(){
        if (isBound) {
            unbindService(MyConnection) //unbind service
            isBound = false
        }
    }

    fun stopIntentServiceType(){
        unregisterReceiver(broadcastReceiver);
    }




    /*
        update ui from service:
    */

    var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val s1 = intent.getStringExtra("DATAPASSED")
            tv.setText(s1)
        }
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



}

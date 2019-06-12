package com.example.servicetypes

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.content.Intent
import android.content.Context.BIND_AUTO_CREATE
import android.content.ComponentName
import android.content.ServiceConnection
import android.view.View
import android.widget.TextView
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.*
import com.example.servicetypes.BoundServiceUsingMessanger.JOB_1
import android.widget.Toast
import com.MyAidlInterface
import com.example.servicetypes.BoundServiceUsingMessanger.JOB_RESPONSE_1












class MainActivity : AppCompatActivity() {
    var myService: BoundService? = null
    private var messenger: Messenger? = null
    var isBound = false
    lateinit var tv:TextView
    private var iservice: MyAidlInterface? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         tv=findViewById<TextView>(R.id.txt)
    }

    override fun onStart() {
        super.onStart()
        //startUnboundType()
        //startBoundType()
        //startIntentServiceType()
        startBoundedUsingMesager()
        startBoundedUsingAIDL()
    }

    private fun startBoundedUsingAIDL() {
        bindService( Intent("com.example.servicetypes.AidlService").setPackage("com.example.servicetypes")
            ,MyConnection,Context.BIND_AUTO_CREATE);
    }


    private fun startBoundedUsingMesager() {
        if (!isBound) {
            val intent = Intent(this@MainActivity, BoundServiceUsingMessanger::class.java)
            bindService(intent, MyConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopBoundedUsingMesager() {
        isBound = false
        messenger = null
    }


    private val mActivityMessenger = Messenger(
        ResponseTakeHandler(this)
    )

    inner class ResponseTakeHandler(context: Context) : Handler() {
        override fun handleMessage(msg: Message) {

            when (msg.what) {
                BoundServiceUsingMessanger.JOB_RESPONSE_1 -> {
                    val result = msg.data.getString("message_res")
                    Toast.makeText(this@MainActivity, "Response: " + result!!, Toast.LENGTH_LONG).show()
                }
                else -> super.handleMessage(msg)
            }

        }
    }

    override fun onStop() {
        super.onStop()
        //stopUnboundType()
        // stopBoundType()
        //stopIntentServiceType()
        stopBoundedUsingMesager()
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
        unregisterReceiver(broadcastReceiver)
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
            /*
                Using bounded
            */
            /*val binder = service as BoundService.LocalBinder
            myService = binder.service
            isBound = true
            myService!!.downloadFile("https://static.pexels.com/photos/4825/red-love-romantic-flowers.jpg")
*/

            /*
                Using bounded messagner
            */
            messenger =  Messenger(service)
            isBound = true
            val message = Message.obtain(null, BoundServiceUsingMessanger.JOB_1)
            val bundle = Bundle()
            bundle.putString("message", "Nisha is saying "+tv.text.toString())
            message.setData(bundle)
            message.replyTo = mActivityMessenger
            try {
                messenger!!.send(message)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }

            iservice = MyAidlInterface.Stub.asInterface(service)
            iservice.multiply(10,20)
            //myService!!.playFile()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
            messenger = null // in case of mesagner
        }
    }



}

package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class UsingMessanger extends Service {
	final public static int Msg = 1;

	@Override
	public void onCreate() {
		super.onCreate();
	}
	@Override
	public IBinder onBind(Intent arg0) {
		Messenger mssMessenger = new Messenger(new HandlerMessag());
		return mssMessenger.getBinder();
	}

	public class HandlerMessag extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Msg:
				Toast.makeText(getApplicationContext(), "Service: I got your message",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}
}

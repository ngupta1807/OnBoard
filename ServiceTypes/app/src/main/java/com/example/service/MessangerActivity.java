package com.example.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MessangerActivity extends Activity {
	Messenger messenger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bindService();
	}

	public void bindService() {
		Intent intent = new Intent(this, UsingMessanger.class);
		bindService(intent, new ConnectService(), Context.BIND_AUTO_CREATE);
	}

	public void stoppingService() {
		Intent intent = new Intent(this, UsingMessanger.class);
		stopService(intent);
	}

	public class ConnectService implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			messenger = new Messenger(service);
			Message msg = Message.obtain(null, UsingMessanger.Msg, 0, 0);
			try {
				messenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			/**
			 * Called when service disconnected
			 */
		}

	}
}

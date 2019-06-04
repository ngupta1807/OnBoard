/*
package com.example.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import com.example.servicetypes.R;


public class BindAidlActivity extends Activity {
	ConnectService mcConnectService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		mcConnectService = new ConnectService();
		findViewById(R.id.button1).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bindService();
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					unbindService();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void bindService() {
		Intent intent = new Intent(this, AidlService.class);
		bindService(intent, mcConnectService, Context.BIND_AUTO_CREATE);
	}

	public void unbindService() throws RemoteException {
		Process.killProcess(uAidlService.getPid());
		unbindService(mcConnectService);
	}

	public void stoppingService() {
		Intent intent = new Intent(this, AidlService.class);
		stopService(intent);
	}

	IRemoteService uAidlService;

	public class ConnectService implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			uAidlService = IRemoteService.Stub.asInterface(service);
			try {
				uAidlService.valueChanged(0);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			*/
/**
			 * Called when service disconnected
			 *//*

			uAidlService = null;
		}

	}
}
*/

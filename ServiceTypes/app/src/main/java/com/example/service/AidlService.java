/*
package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.widget.Toast;

public class AidlService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder.asBinder();
	}

	private final IRemoteService.Stub mBinder = new IRemoteService.Stub() {

		@Override
		public int getPid() throws RemoteException {
			return Process.myPid();
		}

		@Override
		public void basicTypes(int anInt, long aLong, boolean aBoolean,
				float aFloat, double aDouble, String aString)
				throws RemoteException {

		}

		@Override
		public void valueChanged(int value) throws RemoteException {
			mHandler.sendEmptyMessage(BUMP_MSG);
		}

	};
	private static final int BUMP_MSG = 1;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case BUMP_MSG:
				Toast.makeText(AidlService.this, "thiss", Toast.LENGTH_LONG)
						.show();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};

}
*/

package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SimpleService extends Service {

	@Override
	public void onCreate() {
		super.onCreate();
		/**
		 * Service Created and will not call again until the service stopped
		 */
		Toast.makeText(this, "Created", Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
		/**
		 * Perform Your background Task Here
		 */
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
		/**
		 * Used when we need to bind service
		 */
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		/**
		 * Call when service is bind again after Unbind
		 */
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
		/**
		 * Call when service is Unbind
		 */
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/**
		 * Service Stopped
		 */
		Toast.makeText(this, "Created", Toast.LENGTH_LONG).show();
	}
}

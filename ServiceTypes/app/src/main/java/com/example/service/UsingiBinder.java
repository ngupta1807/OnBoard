package com.example.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class UsingiBinder extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return iBinder;
	}

	private IBinder iBinder = new ContainsLocal();

	public class ContainsLocal extends Binder {
		UsingiBinder getBinder() {
			return UsingiBinder.this;
		}
	}

}

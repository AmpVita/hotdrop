package com.gethotdrop.service;

import java.util.ArrayList;
import java.util.List;

import com.gethotdrop.api.Drop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SyncService extends Service {
	public static final int LOW = 0, MEDIUM = 1, HIGH = 2;
	public static LocationClient lClient;

	
	private DropStore dStore;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LocationEventListener listen = LocationEventListener.getListener(this);
		lClient = new LocationClient(this, listen, listen);
		lClient.connect();
		// Use high accuracy
		dStore = DropStore.getDropStore(this);
		return Service.START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		lClient.disconnect();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

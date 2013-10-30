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

public class SyncService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	private final String unit = "Feed";
	public static final int LOW = 0, MEDIUM = 1, HIGH = 2;
	public static List<Drop> drops;
	public static LocationClient lClient;
	public static LocationRequest lRequest;

	public static List<Drop> getDrops() {
		if (drops == null) {
			drops = new ArrayList<Drop>();
		}
		return drops;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		lClient = new LocationClient(this, this, this);
		lClient.connect();
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

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {

		// initialize location request with default parameters
		Log.e(unit, "Connected 2 location");
		lRequest = new LocationRequest();
		SyncService.setLocationFrequency(HIGH);
 
		Log.e(unit, "setFreq");
		Intent sIntent = new Intent(this, LocationWorker.class);
		PendingIntent lWorker = PendingIntent.getService(this, 0, sIntent, 0);

		
		lClient.requestLocationUpdates(lRequest, lWorker);

	}

	@Override
	public void onDisconnected() {
		Log.e("LocationService", "DISCONNECTED");
	}

	public static void setLocationFrequency(int freq) {
		switch (freq) {
		case LOW:
			lRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
			break;
		case MEDIUM:
			lRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			break;
		case HIGH:
			lRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			break;
		default:
			lRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
		}
	}

}

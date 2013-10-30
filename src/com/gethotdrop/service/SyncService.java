package com.gethotdrop.service;

import java.util.ArrayList;
import java.util.List;

import com.gethotdrop.api.Drop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class SyncService extends Service implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	public static List<Drop> drops;
	public LocationClient lClient;
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
		Location loc = lClient.getLastLocation();
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		drops.add(new Drop(0, 0, 0, 0, loc.getLatitude() + " " + loc.getLongitude(), null, null));		
		
	}
	@Override
	public void onDisconnected() {
		Log.e("LocationService", "DISCONNECTED");
		
	}

}

package com.gethotdrop.service;

import java.util.Date;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.gethotdrop.android.FeedActivity;
import com.gethotdrop.api.Drop;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.internal.l;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class LocationEventListener implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener,
		LocationClient.OnRemoveGeofencesResultListener,
		LocationClient.OnAddGeofencesResultListener, LocationListener {

	private static LocationEventListener listen;

	private Context context;

	private LocationEventListener(Context c) {
		context = c;
	}

	public static LocationEventListener getListener(Context c) {
		if (listen == null) {
			listen = new LocationEventListener(c);
		}
		return listen;
	}

	@Override
	public void onRemoveGeofencesByPendingIntentResult(int arg0,
			PendingIntent arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRemoveGeofencesByRequestIdsResult(int arg0, String[] arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		if (!SyncService.MOCK) {
			LocationRequest lRequest;
			lRequest = LocationRequest.create();
			lRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			// TODO: investigate what interval is (perhaps set lower)
			lRequest.setInterval(5000);
			lRequest.setFastestInterval(1000);
			SyncService.lClient.requestLocationUpdates(lRequest, this);
			Log.w("LocationListener", "Connected, Request made");
		}
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAddGeofencesResult(int arg0, String[] arg1) {
		Log.e("geofence", "result");
	}

	@Override
	public void onLocationChanged(Location arg0) {

	}

}

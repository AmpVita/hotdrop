package com.gethotdrop.service;

import com.gethotdrop.api.Drop;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class LocationWorker extends IntentService {

	private final String unit = "LocationWorker";
	public LocationWorker(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e(unit, "Called");
		Location loc = SyncService.lClient.getLastLocation();
		SyncService.drops.add(new Drop(loc.getLatitude() + " " + loc.getLongitude()));SyncService.lClient.getLastLocation();
		
	}

}

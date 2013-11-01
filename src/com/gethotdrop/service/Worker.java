package com.gethotdrop.service;

import java.io.IOException;

import org.json.JSONException;

import com.gethotdrop.android.Feed;
import com.gethotdrop.api.Drop;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

public class Worker extends IntentService {

	public Worker(String name) {
		super("UpdateHotdropService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Drop d = new Drop("HEY!");
		DropStore.store.getDrops().add(d);
		Feed.adapter.notifyDataSetChanged();
		switch (intent.getIntExtra("action", 0)) {
		// location update
		case 0:
			updateLocation(intent);
			break;
		// geofence trigger
		case 1:
			geofenceUpdate(intent);
			break;
		// post drop
		case 2:
			postDrop(intent);
			break;
		}
		
	}

	private void geofenceUpdate(Intent i) {
		int transitionType =
                LocationClient.getGeofenceTransition(i);
		switch(transitionType) {
		case Geofence.GEOFENCE_TRANSITION_ENTER:
		}
	}

	private void updateLocation(Intent i) {
		Log.e("test", "test");
		Drop d = new Drop("HEY!");
		DropStore.store.getDrops().add(d);

	}
	
	private void postDrop(Intent i) {
		Drop d = DropStore.getNextOutgoing();
		try {
			while (d != null)
			DropStore.post(d);
			d = DropStore.getNextOutgoing();
			Log.e("Worker getting drop as", d.message);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		
	}

}

package com.gethotdrop.service;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.List;

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

	public Worker() {
		super("UpdateHotdropService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		switch (intent.getIntExtra("action", 0)) {
		// location update
		case 0:
			updateLocation(intent);
			break;
		}

	}

	private void updateLocation(Intent i) {
		if (SyncService.lClient.isConnected()) {
			Location loc = SyncService.lClient.getLastLocation();
			if (loc != null) {
				if (loc.getAccuracy() < 400) {
					Log.v("Worker: updateLocation", "Loc: " + loc.getLatitude()
							+ ", " + loc.getLongitude());

					DropStore ds = DropStore.getInstance();
					if (ds != null) {
						if (ds.refreshCache(loc)) {
							ds.updateActiveDropsList();
						}
					} else
						Log.e("Worker: updateLocation", "Drop Store Failed");
				} else
					Log.e("Worker: updateLocation",
							"Accuracy too low: " + loc.getAccuracy());
			} else
				Log.e("Worker: updateLocation", "Location was null");
		} else
			Log.e("Worker: updateLocation", "Provider not connected");

	}

}

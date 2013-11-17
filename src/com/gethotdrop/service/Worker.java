package com.gethotdrop.service;

import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.gethotdrop.android.FeedActivity;
import com.gethotdrop.api.Api;
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
		case 0: //update location
			updateLocation(intent);
			break;
		case 1: //post drop
			String message = intent.getStringExtra("message");
			postDrop(message);
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
	
	private void postDrop(String message) {
		if (SyncService.lClient.isConnected()) {
			Location loc = SyncService.lClient.getLastLocation();
			if (loc != null) {
				//if (loc.getAccuracy() < 400) {
					Log.v("Worker: postDrop", "Loc: " + loc.getLatitude()
							+ ", " + loc.getLongitude());
						
						try {
							Api.setHotdrop(loc.getLatitude(), loc.getLongitude(), message);
						} catch (IOException e) {
							Log.e("Worker: postDrop", "API IO Exception");
						} catch (JSONException e) {
							Log.e("Worker: postDrop", "API JSON Exception");
						}
					
				//	} else
				//	Log.e("Worker: updateLocation",
				//			"Accuracy too low: " + loc.getAccuracy());
			} else
				Log.e("Worker: postDrop", "Location was null");
		} else
			Log.e("Worker: postDrop", "Provider not connected");
	}
	
//	public void createNotification() {
//        // Prepare intent which is triggered if the
//        // notification is selected
//        Intent intent = new Intent(this, Feed.class);
//        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        // Build notification
//        Notification noti = new Notification.Builder(this)
//                        .setContentTitle("Discovered Hotdrop")
//                        .setContentText("You have a new drop!").setSmallIcon(R.drawable.ic_launcher)
//                        .setContentIntent(pIntent).getNotification();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        
//        // Hide the notification after its selected
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        //Launch notification
//        notificationManager.notify(0, noti);
//        
//        //Get Vibrator service
//        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        
//        // Vibrate for 300 milliseconds
//        v.vibrate(300);
//	}

}

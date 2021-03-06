package com.gethotdrop.service;

import com.gethotdrop.android.FeedActivity;
import com.google.android.gms.location.LocationClient;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class SyncService extends Service {
	public static LocationClient lClient;
	Context c;
	private int mInterval = 1000; // 5 seconds by default, can be changed later
	private Handler mHandler;
	private DropStore dStore;
	public static final boolean MOCK = true;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		c = this;
		LocationEventListener listen = LocationEventListener.getListener(this);
		lClient = new LocationClient(this, listen, listen);
		lClient.connect();
		
		// Use high accuracy
		dStore = DropStore.initialize(this);
		mHandler = new Handler();
		this.startRepeatingTask();
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

	Runnable mStatusChecker = new Runnable() {
		@Override
		public void run() {
			Intent i = new Intent(c, Worker.class);
			i.putExtra("action", 0); // updating lists of drops
            c.startService(i); 
            FeedActivity.updateDrops();
			mHandler.postDelayed(mStatusChecker, mInterval);
		}
	};

	void startRepeatingTask() {
		mStatusChecker.run();
	}

	void stopRepeatingTask() {
		mHandler.removeCallbacks(mStatusChecker);
	}

}

package com.gethotdrop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.json.JSONException;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.gethotdrop.android.Feed;
import com.gethotdrop.api.Api;
import com.gethotdrop.api.Drop;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;

public class DropStore {
	private ArrayList<Drop> outgoingQueue;
	private Api api;
	private List<Drop> drops;
	private Map<Integer, Drop> allDrops;
	private LocationEventListener listener;
	private PendingIntent pIntent;
	public static DropStore store = null;

	protected DropStore(Context c) {
		outgoingQueue = new ArrayList<Drop>();
		drops = new ArrayList<Drop>();
		api = new Api(UniqueIdentifier.id(c));
		Intent intent = new Intent(c, Worker.class);
		// configure worker for geofence action
		intent.putExtra("action", 1);
		pIntent = PendingIntent.getService(c, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		listener = LocationEventListener.getListener(c);
	}
	
	public static DropStore getDropStore(Context c) {
		if (store == null) {
			store = new DropStore(c);
		}
		return store;
	}

	public List<Drop> getDrops() {
		return drops;
	}
	
	public static double getRadius() {
		try {
			return store.api.getRadius();
		} catch (Exception e) {
			Log.e("DropStore", "Could not get radius");
			return -1;
		}
	}

	public boolean updateGeofences(Location loc) {
		try {
			// double radius = 25;
			double radius = api.getRadius();
			Map<Integer, Drop> newDrops = api.getHotdrops(loc.getLatitude(),
					loc.getLongitude(), radius);

			// get inactive drop ids for geofence removal
			Set<Integer> iKey = allDrops.keySet();
			iKey.removeAll(newDrops.keySet());

			List<String> sKey = new ArrayList<String>();
			for (Integer i : iKey) {
				sKey.add(String.valueOf(i));
			}

			List<Geofence> geofences = new ArrayList<Geofence>();
			for (Drop d : newDrops.values()) {
				Geofence fence = d.toGeofense();
				geofences.add(fence);
			}

			SyncService.lClient.removeGeofences(sKey, store.listener);
			SyncService.lClient
					.addGeofences(geofences, pIntent, store.listener);
			allDrops = newDrops;
			return true;
		} catch (Exception e) {
			Log.e("Update Geofences", e.getMessage());
			e.printStackTrace();
			return false;
		}

	}

	public void addDrop(Integer id) {
		Drop d = allDrops.get(id);
		store.drops.add(d);
		Feed.adapter.notifyDataSetChanged();
	}

	public void removeDrop(Integer id) {
		Drop d = allDrops.get(id);
		drops.remove(d);
		Feed.adapter.notifyDataSetChanged();

	}
	public static Drop getNextOutgoing() {
		return store.outgoingQueue.remove(0);
	}
	
	public static boolean addOutgoing(Drop d) {
		return store.outgoingQueue.add(d);
	}
	public static void  post(Drop newDrop) throws IOException, JSONException {
		 store.api.setHotdrop(newDrop.getLatitude(), newDrop.getLatitude(),
				newDrop.getMessage());
		 store.drops.add(newDrop);
		 Feed.adapter.notifyDataSetChanged();
		
	}

}

package com.gethotdrop.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

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

/* 
 * Two types of drop lists, active and aware of
 * Active - needs to provide a list to DropAdapter
 * 		  - maintain list of current drops
 * 
 * Passive - needs to coordinate with geofence
 *
 * 
 * 
 */

public class DropStore {
	private ArrayList<Drop> outgoingQueue;
	private Api api;
	private List<Drop> drops;
	private Map<Integer, Drop> allDrops;
	private LocationEventListener listener;
	private PendingIntent pIntent;
	public static DropStore store = null;
	private static double radius;

	protected DropStore(Context c) {
		
		outgoingQueue = new ArrayList<Drop>();
		drops = new ArrayList<Drop>();
		api = new Api(UniqueIdentifier.id(c));
		try {
			radius = api.getRadius();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public static DropStore getDropStore() {
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
			double radius = 15;
			
			Map<Integer, Drop> newDrops = api.getHotdrops(loc.getLatitude(),
					loc.getLongitude(), radius);
			Log.w("Drops", "is :" + newDrops.size());

//			// get inactive drop ids for geofence removal
//			if (allDrops != null && allDrops.keySet() != null) {
//				Set<Integer> iKey = allDrops.keySet();
//				iKey.removeAll(newDrops.keySet());
//
//				List<String> sKey = new ArrayList<String>();
//				for (Integer i : iKey) {
//					sKey.add(String.valueOf(i));
//				}
//				if (sKey != null && !sKey.isEmpty())
//				SyncService.lClient.removeGeofences(sKey, store.listener);
//			}
//			List<Geofence> geofences = new ArrayList<Geofence>();
//			for (Drop d : newDrops.values()) {
//				Geofence fence = d.toGeofense();
//				Log.w("Geofence", "AttemptAdd");
//				geofences.add(fence);
//			}
//			if (geofences != null && geofences.size() > 0)
//			SyncService.lClient
//					.addGeofences(geofences, pIntent, store.listener);
			for (Drop d : newDrops.values()){
				allDrops.put(d.getId(), d);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	

	private Drop getDrop(int id) {
		Drop d = allDrops.get(id);
		return d;
	}
	
	public void evaluateLocation(Location l) {
		if (store.allDrops == null) return;
		
		ArrayList<Drop> activatedDrops = new ArrayList<Drop>();
		for (Drop d : store.allDrops.values()) {
			if (d.getLocation().distanceTo(l) <= radius) {
				activatedDrops.add(d);
			}
		}
		store.drops = activatedDrops;
	}
	
	public void handleGeofence(String idIn) {
		int id = Integer.parseInt(idIn);
		Iterator<Drop> i = store.drops.iterator();
		boolean found = false;
		while (i.hasNext()) {
			Drop d = i.next();
			if (d.getId() == id) {
				found = true;
				break;
			}
		}
		if (!found) {
			store.drops.add(getDrop(id));
			Log.e("AddedDrop", "ID: " + id);
		}
	}

	public static Drop getNextOutgoing() {
		return store.outgoingQueue.remove(0);
	}

	public static boolean addOutgoing(Drop d) {
		return store.outgoingQueue.add(d);
	}

	public static void post(Drop newDrop) throws IOException, JSONException {
		store.api.setHotdrop(newDrop.getLatitude(), newDrop.getLatitude(),
				newDrop.getMessage());
		store.drops.add(newDrop);
		Feed.adapter.notifyDataSetChanged();

	}

}

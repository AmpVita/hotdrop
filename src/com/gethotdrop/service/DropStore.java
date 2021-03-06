package com.gethotdrop.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.gethotdrop.api.Api;
import com.gethotdrop.api.Drop;

/* 
 * Two types of drop lists, active and aware of
 * Active - needs to provide a list to DropAdapter
 * 		  - maintain list of current drops
 * 
 * Passive - needs to coordinate with geofence
 *
 */

public class DropStore {
	private ArrayList<Drop> drops;
	
	private Map<Integer, Drop> activeDrops = new HashMap<Integer, Drop>();
	private Map<Integer, Drop> allDrops = new HashMap<Integer, Drop>();

	private Map<Integer, Drop> newActiveDrops = new HashMap<Integer, Drop>();

	private static DropStore instance = null;

	protected DropStore(Context context) { }

	public static DropStore initialize(Context context) {
		if (instance == null)
			instance = new DropStore(context);
		return instance;
	}

	public static DropStore getInstance() {
		return instance;
	}

	public List<Drop> getDropList() {
		if (drops == null)
			drops = new ArrayList<Drop>();
		this.updateActiveDropsList();
		return drops;
	}

	public boolean refreshCache(Location location) {
		if (location == null) {
			return false;
		}
		double radius = 0;
		Map<Integer, Drop> newAllDrops;
		try {
			radius = 25; //api.getRadius();
			Map<Integer, Drop> oldAllDrops = allDrops;
			newAllDrops = Api.getHotdrops(location.getLatitude(),
					location.getLongitude());
		} catch (Exception e) {
			return false;
		}

		newActiveDrops = new HashMap<Integer, Drop>();
		for (Drop drop : newAllDrops.values()) {
			if (drop.getLocation().distanceTo(location) <= radius * 1000) {
				newActiveDrops.put(drop.getId(), drop);
			}
		}
		Log.e("New Active", "Drops" + newActiveDrops.size());

		if (equalMaps(activeDrops, newActiveDrops))
			return false; // false if they didn't change
		else {
			activeDrops = newActiveDrops;
			return true;
		}
	}

	public boolean isNewDrop() {
		int i = 0;
		for (Integer k : newActiveDrops.keySet()) {
			if (activeDrops.containsKey(k))
				i += 1;
		}
		activeDrops = newActiveDrops;
		if (i != newActiveDrops.keySet().size())
			return true;
		return false;
	}

	public boolean equalMaps(Map<Integer, Drop> a, Map<Integer, Drop> b) {
		if (a.size() != b.size())
			return false;
		for (Integer k : a.keySet()) {
			if (!(b.containsKey(k)))
				return false;
		}
		return true;
	}

	public Map<Integer, Drop> getActiveDrops() {
		return activeDrops;
	}

	public void updateActiveDropsList() {
		ArrayList<Drop> dropList = new ArrayList<Drop>();

		for (Drop drop : activeDrops.values()) {
			dropList.add(drop);
		}
		Collections.sort(dropList, new Comparator<Drop>() {

			@Override
			public int compare(Drop arg0, Drop arg1) {
				return -arg0.getCreatedAt().compareTo(arg1.getCreatedAt());
			}

		});
		Log.e("Drop List", "Length: " + activeDrops.size());
		drops.clear();
		drops.addAll(dropList);
	}

	public Map<Integer, Drop> getAllDrops() {
		return allDrops;
	}

}

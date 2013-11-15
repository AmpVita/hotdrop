package com.gethotdrop.api;

import java.util.Date;

import com.gethotdrop.service.DropStore;
import com.google.android.gms.location.Geofence;

import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;

public class Drop {
	private int id;
	private int userId;
	private int grabs;
	private double latitude;
	private double longitude;
	public String message;
	public Bitmap image;
	private String createdAt;
	private Date updatedAt;

	public Drop(int id, int userId, int grabs, double latitude, double longitude,
			String message, String createdAt, Date updatedAt) {
		this.id = id;
		this.userId = userId;
		this.grabs = grabs;
		this.latitude = latitude;
		this.longitude = longitude;
		this.message = message;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public Drop(String message, Bitmap image, String date, int grabs) {
		this.message = message;
		this.image = image;
		this.createdAt = date;
		this.grabs = grabs;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public int getId() {
		return id;
	}

	public int getUserId() {
		return userId;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getMessage() {
		return message;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public Location getLocation() {
		Location location = new Location(LocationManager.PASSIVE_PROVIDER);
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		return location;

	}

	public Geofence toGeofense() {
        	// Build a new Geofence object
            return new Geofence.Builder()
            	.setRequestId(String.valueOf(getId()))
            	.setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            	.setCircularRegion(getLatitude(), getLongitude(), (float) 40)
            	.setExpirationDuration(Geofence.NEVER_EXPIRE)
            	.build();
            	
        }

	public int getGrabs() {
		return grabs;
	}

	public void setGrabs(int grabs) {
		this.grabs = grabs;
	}
}
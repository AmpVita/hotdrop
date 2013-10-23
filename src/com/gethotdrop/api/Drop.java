package com.gethotdrop.api;

import java.util.Date;

import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;


public class Drop {
        private int id;
        private int userId;
        private double latitude;
        private double longitude;
        public String message;
        public Bitmap image;
        private Date createdAt;
        private Date updatedAt;
        
        public Drop(int id, int userId, double latitude, double longitude, String message, Date createdAt, Date updatedAt) {
                this.id = id;
                this.userId = userId;
                this.latitude = latitude;
                this.longitude = longitude;
                this.message = message;
                this.createdAt = createdAt;
                this.updatedAt = updatedAt;
        }
        
        public Drop(String message, Bitmap image) {
        	this.message = message;
        	this.image = image;
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
        
        public Date getCreatedAt() {
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
}